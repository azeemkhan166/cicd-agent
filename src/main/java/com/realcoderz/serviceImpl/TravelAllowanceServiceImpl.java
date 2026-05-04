/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.TravelAllowance;
import com.realcoderz.repository.TravelAllowanceRepository;
import org.springframework.stereotype.Service;
import com.realcoderz.service.TravelAllowanceService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Bipul Singh
 */
@Service
public class TravelAllowanceServiceImpl implements TravelAllowanceService {

    ObjectMapper mapper = new ObjectMapper();

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    static final Logger logger = LoggerFactory.getLogger(TravelAllowanceServiceImpl.class);

    @Autowired
    private TravelAllowanceRepository travelAllowanceRepository;

    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map list(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("organizationId") && map.get("organizationId") != null) {
                Long orgId = Long.parseLong(map.get("organizationId").toString());
                List list = travelAllowanceRepository.findByOrganizationId(orgId);
                resultMap.put("list", list);
                resultMap.put("status", "success");
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceServiceImpl -> list() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map saveOrUpdate(Map map) {
        Map resultMap = new HashMap<>();
        try {
            TravelAllowance allowance = mapper.convertValue(map, TravelAllowance.class);
            TravelAllowance alreadyExist = travelAllowanceRepository.findByGroupId(allowance.getGroupId());
            if (alreadyExist != null) {
                allowance.setId(alreadyExist.getId());
            }
            allowance = travelAllowanceRepository.save(allowance);
            resultMap.put("status", "success");
            resultMap.put("data", allowance);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceServiceImpl -> saveOrUpdate() :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map findById(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("id") && map.get("id") != null) {
                Optional<TravelAllowance> allowance = travelAllowanceRepository.findById(Long.parseLong(map.get("id").toString()));
                if (allowance.isPresent()) {
                    TravelAllowance data = allowance.get();
                    resultMap.put("status", "success");
                    resultMap.put("data", data);
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "No data found with this id");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceServiceImpl -> get() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map getTravelAllowanceAmount(String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("employeeId") && map.get("employeeId") != null) {
                String bearerToken = authenticationFilter.getJwtFromRequest(request);
                HttpHeaders header = new HttpHeaders();
                header.setBearerAuth(bearerToken);
                header.setContentType(MediaType.TEXT_PLAIN);
                HttpEntity<?> entity = new HttpEntity<>(data, header);
                Map employeeListReq = restTemplate.exchange(reimburshment_url + "/groupmapping/getgroupByEmployee", HttpMethod.POST, entity, HashMap.class).getBody();
                Map employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);
                System.out.println("employeeListResp ::" + employeeListResp);
                if (employeeListResp.containsKey("status") && employeeListResp.get("status").equals("success")) {
                    List ids = (List) employeeListResp.get("groupEmployee");
                    List<Long> gropuIdList = new ArrayList<>();
                    ids.stream().forEach(val -> gropuIdList.add(Long.parseLong(val.toString())));
                    List<TravelAllowance> allowanceList = travelAllowanceRepository.findByGroupIdList(gropuIdList);
                    if (!allowanceList.isEmpty()) {
                        System.out.println("allowanceList" + allowanceList.get(0).getAllowanceAmount());
                        resultMap.put("status", "success");
                        resultMap.put("amount", allowanceList.get(0).getAllowanceAmount());
                    } else {
                        resultMap.put("status", "error");
                        resultMap.put("msg", "No data found with this id");
                    }
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "No group found with this employeeId");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceServiceImpl -> getTravelAllowanceAmount() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map deleteTravelAllowance(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("id") && map.get("id") != null) {
                Long id = Long.parseLong(map.get("id").toString());
                Optional<TravelAllowance> alreadyExist = travelAllowanceRepository.findById(id);
                if (alreadyExist.isPresent()) {
                    travelAllowanceRepository.delete(alreadyExist.get());
                    resultMap.put("status", "success");
                    resultMap.put("msg", "Travel Allowance deleted successfully");
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "No data found with this id");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceServiceImpl -> saveOrUpdate() :: ", ex);
        }
        return resultMap;
    }

}
