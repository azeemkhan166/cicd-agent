/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.CustomAllowanceService;
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
@RequestMapping("/customAllowance")
public class CustomAllowanceController {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private CustomAllowanceService customAllowanceService;
    
    @PostMapping("/getAllAllowances")
    public Map getAllAllowances(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customAllowanceService.getAllAllowances(map);
        } catch (Exception ex) {
          
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
        @PostMapping("/save")
    public Map saveAllowance(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customAllowanceService.save(map);
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
            resultMap = customAllowanceService.findById(map);
        } catch (Exception ex) {
           
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
                 @PostMapping("/findAllowanceByEmployeeType")
    public Map findAllowanceByEmployeeType(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = customAllowanceService.findAllowanceByEmployeeType(map);
        } catch (Exception ex) {
           
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}
