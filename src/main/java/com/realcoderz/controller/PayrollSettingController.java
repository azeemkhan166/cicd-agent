/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.PayrollSettingService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping(path = "/payrollSetting")
public class PayrollSettingController {
    
    static final Logger logger = LoggerFactory.getLogger(PayrollSettingController.class);
   
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private PayrollSettingService payrollService;
    
     @PostMapping(path = "/savePayroll")
    public Map savePayrollSetting(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            
          Map  map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = payrollService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in PayrollSettingController -> savePayrollSetting() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    
    @PostMapping(path = "/getPayroll")
    public Map getPayrollSetting(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map  map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
             Map payroll = payrollService.fetch(Long.parseLong(map.get("organization_id").toString()));
            resultMap.put("list", payroll);
        } catch (Exception ex) {
            logger.error("Problem in PayrollSettingController -> getPayrollSetting() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;

    }
    
     @PostMapping("/findById")
    public Map findById(@RequestBody String data){
      Map<String,Object>  resultMap = new HashMap<>();
       try {
           Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = payrollService.findById(Long.parseLong(map.get("id").toString()));
            
         }catch(Exception ex){
              logger.error("Problem in PayrollSettingController -> findById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
              
        } 
      return resultMap;
    }

}
