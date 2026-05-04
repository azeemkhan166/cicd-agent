/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.OtherAllowances;
import com.realcoderz.repository.OtherAllowancesRepository;
import com.realcoderz.service.OtherAllowancesService;
import static com.realcoderz.serviceImpl.SalaryBreakupServiceImpl.LOGGER;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Astha & Mayank
 */
@Service
public class OtherAllowancesServiceImpl implements OtherAllowancesService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(OtherAllowancesServiceImpl.class);

    @Autowired
    private OtherAllowancesRepository otherAllowancesRepository;

    //    Get Other Allowances By Id
    @Override
    public Map getOtherAllowances(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Long id = Long.parseLong(map.get("id").toString());
            Optional<OtherAllowances> otherAllowance = otherAllowancesRepository.findById(id);
            if (otherAllowance.isPresent()) {
                resultMap.clear();
                resultMap.put("list", otherAllowance);
                resultMap.put("status", "success");

            } else {
                resultMap.clear();
                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in OtherAllowancesServiceImpl -> getOtherAllowances() :: ", ex);
        }

        return resultMap;
    }

    //    Save Other Allowances
    @Override
    public Map saveOtherAllowances(Map map) {
        Map resultMap = new HashMap<>();
        try {
            List<OtherAllowances> otherAllowance = mapper.convertValue(map, new TypeReference<List<OtherAllowances>>() {
            });
            //            Other Allowances is Empty
            if (!otherAllowance.isEmpty()) {
                otherAllowancesRepository.saveAll(otherAllowance);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not saved.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OtherAllowancesServiceImpl -> saveOtherAllowances() :: ", ex);

        }
        return resultMap;
    }

}
