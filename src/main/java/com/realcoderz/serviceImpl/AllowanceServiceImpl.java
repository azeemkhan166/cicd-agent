/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Allowance;
import com.realcoderz.model.AllowanceTemplate;
import com.realcoderz.repository.AllowanceRepository;
import com.realcoderz.repository.AllowanceSubMappingRepository;
import com.realcoderz.service.AllowanceService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Mayank
 */
@Service
@Slf4j
public class AllowanceServiceImpl implements AllowanceService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(AllowanceServiceImpl.class);

    private final AllowanceRepository allowanceRepository;
    private final AllowanceSubMappingRepository allowanceSubMappingRepo;
    private final RestTemplate restTemplate;

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    public AllowanceServiceImpl(AllowanceRepository allowanceRepository,
                                AllowanceSubMappingRepository allowanceSubMappingRepo,
                                RestTemplate restTemplate,
                                @Value("${reimburshment_url}") String reimburshment_url) {
        this.allowanceRepository = allowanceRepository;
        this.allowanceSubMappingRepo = allowanceSubMappingRepo;
        this.restTemplate = restTemplate;
        this.reimburshment_url = reimburshment_url;
    }

    //    Save Allowances
    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            log.info("In Allowance save method execution starts with data -> " + map);

            Allowance allowance = mapper.convertValue(map, Allowance.class);
            String allowanceType = allowance.getType_of_allowance();
            if (allowance.getAllowance_name().equalsIgnoreCase("basic salary") && allowance.getEmployee_type().equalsIgnoreCase("full time")) {

                if (!allowanceType.equalsIgnoreCase("variable")) {
                    allowance.setPercentage(null);
                } else {
                    allowance.setAmount(null);
                }

            }
            if (allowance.getSupervisor_status().equalsIgnoreCase("Approved")) {
                if (allowanceType.equalsIgnoreCase("variable")) {
                    allowance.setAmount(null);
                } else {
                    allowance.setPercentage(null);
                }
            }

            Set<String> allowanceNames = new HashSet<>();
            List allowanceWithId = new ArrayList<>();
            if (allowance != null) {
                allowanceRepository.save(allowance);
                log.info("Allowance saved successfully");
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Allowance..!..!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> save() :: ", ex);

        }

        return resultMap;

    }

    //    Fetch All Allowances By Org Id
    @Override
    public Map fetch(Long org_id, HttpServletRequest request,String search) {
        Map resultMap = new HashMap<>();
        try {
            logger.info("In Allowance Fetch Method Execution Starts :: with organization id " + org_id);

            long startTime = System.currentTimeMillis();

           // List<Allowance> allowances = allowanceRepository.findAllowanceById(org_id);
           
           List<LinkedCaseInsensitiveMap> allowances=allowanceRepository.allowanceForGrid(org_id,search);

//            long endTime = System.currentTimeMillis();
//            long duration = endTime - startTime;
            
//            System.out.println("duration "+duration);

            if (allowances != null) {
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetch() :: ", ex);

        }
        return resultMap;
    }

    //    Delete Allowances
    @Override
    public Map delete(Long id) {
        Map resultMap = new HashMap<>();
        try {
            Optional<Allowance> allowance = allowanceRepository.findById(id);
            if (allowance.isPresent()) {
                allowanceRepository.delete(allowance.get());
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not deleted.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> delete() :: ", ex);
        }
        return resultMap;
    }

    //    Fetch Allowances By Id
    @Override
    public Map findById(Long id) {
        Map resultMap = new HashMap<>();
        try {
            Optional<Allowance> allowance = allowanceRepository.findById(id);
            if (allowance.isPresent()) {
                resultMap.clear();
                resultMap.put("list", allowance.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> findById() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map findAllowanceNameFromAllowanceId(Long allowance_id, Long org_id) {
        Map resultMap = new HashMap<>();
        try {
            log.info("In Allowance findAllowanceNameFromAllowanceId method execution starts with allowance_id => " + allowance_id + " and organization_id=> " + org_id);
            Optional<Allowance> allowance = allowanceRepository.findById(allowance_id);
            List<Allowance> allowanceNames = allowanceRepository.findAllowanceById(org_id);
            List<LinkedCaseInsensitiveMap> subAllowance = allowanceSubMappingRepo.getSubAllowances(org_id);
            List subAllowanceName = new ArrayList<>();
            List subAllowanceId = new ArrayList<>();
            if (subAllowance.size() == 1) {
                subAllowanceName.add("Daily Wages");
            }

            Allowance allowances = allowance.get();
            Set<AllowanceTemplate> uniqueTemplates = new LinkedHashSet<>(allowances.getAllowanceTemplate());

            allowances.setAllowanceTemplate(new ArrayList<>(uniqueTemplates));

            for (Allowance a : allowanceNames) {
                for (int idx = 0; idx < subAllowance.size(); idx++) {
                    if (Integer.parseInt(a.getAllowance_id().toString()) == Integer.parseInt(subAllowance.get(idx).get("allowance_id").toString())) {
                        subAllowanceId.add(subAllowance.get(idx).get("allowance_sub_mapping_id").toString());
                        subAllowanceName.add(a.getAllowance_name());
                    }
                }
            }
            if (allowance.isPresent()) {
                resultMap.clear();
                //resultMap.put("list", allowance.get());
                resultMap.put("list", allowance.get());
                resultMap.put("subAllowance", subAllowanceName);
                resultMap.put("subAllowanceId", subAllowanceId);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> findById() :: ", ex);

        }
        return resultMap;
    }

    //    Fetch All Approved Allowances
    @Override
    public Map fetchApprovedAllowances(Long org_id, Integer month, Integer year, String employeeType) {
        Map resultMap = new HashMap<>();
        try {
            List<Allowance> allowances = allowanceRepository.findApprovedAllowances(org_id, new Date(year - 1900, month, 1), employeeType);
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "No Allowance is approved by supervisor..");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetchApprovedAllowances() :: ", ex);

        }
        return resultMap;
    }

    //    Allowance is Already Exist
    @Override
    public Map isAlreadyExist(String name, Long id, String employee_type) {
        Map resultMap = new HashMap<>();
        try {
            int allowance = allowanceRepository.isAllowanceExist(name, id, employee_type);
            if (allowance > 0) {
                resultMap.clear();
                resultMap.put("result", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("result", false);
                resultMap.put("msg", "Allowance not found.!");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> isAlreadyExist() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getAllowanceNames(Long orgId, String type) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getAllowanceNames(orgId, type);
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getAllowanceNames() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getAllowanceNameForSuperAdmin(String type) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getAllowanceNameForSuperAdmin(type);
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getAllowanceNameForSuperAdmin() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getAllowanceDataForSuperAdmin() {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getAllowancesDataForSuperAdmin();
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getAllowanceDataForSuperAdmin() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getParticularAllowanceDataForSuperAdmin(Long id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getParticularAllowanceDataForSuperAdmin(id);
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance data is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getParticularAllowanceDataForSuperAdmin() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map isAllowanceExistForSuperAdmin(String name) {
        Map resultMap = new HashMap<>();
        try {
            int allowance = allowanceRepository.isAllowanceExistForSuperAdmin(name);
            if (allowance > 0) {
                resultMap.clear();
                resultMap.put("result", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("result", false);
                resultMap.put("msg", "Allowance not found.!");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> isAllowanceExistForSuperAdmin() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getAllowanceNameForOrganization(Long id, String type) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getAllowanceNameForOrganization(id, type);
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetch() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map checkAllowanceType(String name) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.checkTypeOfAllowance(name);
            resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", allowances);
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> checkAllowanceType() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getWorkerAllowanceName(Long org_id, String employeeType) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepository.getWorkerAllowanceName(org_id, employeeType);
            List<LinkedCaseInsensitiveMap> subAllowance = allowanceSubMappingRepo.getSubAllowances(org_id);
            List subAllowanceName = new ArrayList<>();
            if (subAllowance.size() == 1) {
                subAllowanceName.add("Daily Wages");
            }
            for (LinkedCaseInsensitiveMap allowance : allowances) {
                for (int idx = 0; idx < subAllowance.size(); idx++) {
                    if (Integer.parseInt(allowance.get("allowance_id").toString()) == Integer.parseInt(subAllowance.get(idx).get("allowance_id").toString())) {
                        subAllowanceName.add(allowance.get("allowance_name"));
                    }
                }
            }
            if (allowances != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", allowances);
                resultMap.put("subAllowance", subAllowanceName);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetch() :: ", ex);

        }
        return resultMap;
    }

    //    Fetch Allowances By Id
    @Override
    public Map fetchbyAllowanceName(Long id) {
        Map resultMap = new HashMap<>();
        try {

            Optional<Allowance> allowance = allowanceRepository.findById(id);
            if (allowance.isPresent()) {
                resultMap.clear();
                resultMap.put("list", allowance.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance name  not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetchbyAllowanceName() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map approvedRejectAllowance(Long id, String status) {

        Map resultMap = new HashMap<>();
        try {
            int allowances = allowanceRepository.updateStatus(status, id);
            if (allowances >= 0) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("msg", "updated successfully");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> fetch() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map getGroupList(Long org_id) {
        Map resultMap = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("organizationId", org_id);
        String encryptedPayload = EncryptDecryptUtils.encrypt(json.toString());
        Map employeeListResp = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(encryptedPayload, headers);

        Map employeeListReq = restTemplate.exchange(reimburshment_url + "/users/getGroupListByOrgId", HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }

        resultMap.put("status", "success");
        resultMap.put("data", employeeListResp);

        try {

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getGroupList() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map getGradeList(Long org_id) {
        Map resultMap = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("organizationId", org_id);
        String encryptedPayload = EncryptDecryptUtils.encrypt(json.toString());
        Map employeeListResp = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(encryptedPayload, headers);

        Map employeeListReq = restTemplate.exchange(reimburshment_url + "/grade/getallgrades", HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }

        resultMap.put("status", "success");
        resultMap.put("data", employeeListResp);

        try {

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getGroupList() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map getEmployeeByOrgIdAndEmployeeType(Long id, String type) {

        Map resultMap = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("organizationId", id);
        json.put("employeeType", type);
        String encryptedPayload = EncryptDecryptUtils.encrypt(json.toString());
        Map employeeListResp = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(encryptedPayload, headers);

        Map employeeListReq = restTemplate.exchange(reimburshment_url + "/users/getEmployeeByOrgIdAndEmployeeType", HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }

        resultMap.put("status", "success");
        resultMap.put("data", employeeListResp);

        try {

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getGroupList() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getGradeOrGroupList(Long id, String type, String employeeType) {

        Map resultMap = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("type", type);
        json.put("employeeType", employeeType);
        String encryptedPayload = EncryptDecryptUtils.encrypt(json.toString());
        Map employeeListResp = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(encryptedPayload, headers);

        Map employeeListReq = restTemplate.exchange(reimburshment_url + "/users/getGroupORGradeList", HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            resultMap = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }

//            resultMap.put("status", "success");
//            resultMap.put("data", employeeListResp);
        try {

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceServiceImpl -> getGroupList() :: ", ex);

        }
        return resultMap;
    }
}
