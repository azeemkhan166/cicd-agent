/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.CustomDeductionService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/customDeduction")
public class CustomDeductionController {
    
     ObjectMapper mapper = new ObjectMapper();
     
     @Autowired
     private CustomDeductionService customDeductionService;
     
         @PostMapping("/getAllDeductions")
    public Map getAllDeductions(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customDeductionService.getAllDeductions(map);
        } catch (Exception ex) {
          
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
        
        @PostMapping("/save")
    public Map save(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customDeductionService.save(map);
        } catch (Exception ex) {
        
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
                @PostMapping("/findById")
    public Map findById(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customDeductionService.findById(map);
        } catch (Exception ex) {
           
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/findDeductionByEmployeeType")
    public Map findDeductionByEmployeeType(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customDeductionService.findDeductionByEmployeeType(map);
        } catch (Exception ex) {
           
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}
