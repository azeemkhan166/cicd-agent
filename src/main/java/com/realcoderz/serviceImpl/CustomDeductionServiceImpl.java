/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.CustomAllowance;
import com.realcoderz.model.CustomDeduction;
import com.realcoderz.repository.CustomDeductionRepository;
import com.realcoderz.service.CustomDeductionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class CustomDeductionServiceImpl implements CustomDeductionService{

      ObjectMapper mapper = new ObjectMapper();

      private final CustomDeductionRepository customDeductionRepository;
      
    @Override
    public Map getAllDeductions(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            List<CustomDeduction> data = customDeductionRepository.findDeductionById(ids);
            resultMap.put("status", "success");
            resultMap.put("value", data);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
    
            @Override
    public Map save(Map map) {

        Map resultMap = new HashMap<>();

        try {

            CustomDeduction data = mapper.convertValue(map, CustomDeduction.class);

            customDeductionRepository.save(data);
            resultMap.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
    
     @Override
    public Map findById(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("id").toString());
            Optional<CustomDeduction> data = customDeductionRepository.findById(ids);
            resultMap.put("status", "success");
            if (data.isPresent()) {
                resultMap.put("value", data.get());
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
         @Override
    public Map findDeductionByEmployeeType(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            String employeeType=map.get("employeeType").toString();
            List<CustomDeduction> data = customDeductionRepository.findDeductionByEmployeeType(ids,employeeType);
            resultMap.put("status", "success");
            resultMap.put("value", data);
          
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
}
