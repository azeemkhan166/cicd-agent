/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.CustomAllowance;
import com.realcoderz.model.PayPlan;
import com.realcoderz.repository.CustomAllowanceRepository;
import com.realcoderz.service.CustomAllowanceService;
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
public class CustomAllowanceServiceImpl implements CustomAllowanceService{

    private final CustomAllowanceRepository CustomAllowanceRepository;

     ObjectMapper mapper = new ObjectMapper();
     
     
    @Override
    public Map getAllAllowances(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            List<CustomAllowance> data = CustomAllowanceRepository.findAllowanceById(ids);
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

            CustomAllowance data = mapper.convertValue(map, CustomAllowance.class);

            CustomAllowanceRepository.save(data);
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
            Optional<CustomAllowance> data = CustomAllowanceRepository.findById(ids);
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
    public Map findAllowanceByEmployeeType(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            String employeeType=map.get("employeeType").toString();
            List<CustomAllowance> data = CustomAllowanceRepository.findAllowanceByEmployeeType(ids,employeeType);
            resultMap.put("status", "success");          
            resultMap.put("value", data);         

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
     
    
}
