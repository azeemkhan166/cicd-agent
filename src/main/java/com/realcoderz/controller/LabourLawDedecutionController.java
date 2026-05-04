/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.LabourLawDeduction;
import com.realcoderz.service.LabourLawDeductionService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tauseef
 */
@RestController
@RequestMapping("/labourLawDeduction")
@Slf4j
public class LabourLawDedecutionController {
    @Autowired
    private LabourLawDeductionService service;
    
    @Autowired
    private ObjectMapper mapper;
    
    @PostMapping(value = "/getallStateLabourWelfareFunds")
    public Map getAllStatesDeduction() {
        Map response = new HashMap();
        try {       	
        	response = service.getAllStatesDeduction();
        } catch (Exception e) {
            log.error("exception in save() :: " + e.getMessage());
            response.put("status", "exception");
            response.put("msg","Please try again!");
        }
        return response;
    }
    
    @PostMapping("/save")
     public Map saveStatesDeduction(@RequestBody String body) {
         
        Map response = new HashMap();
        try {       	
            LabourLawDeduction map = mapper.readValue(EncryptDecryptUtils.decrypt(body), LabourLawDeduction.class);
            response = service.save(map);
        } catch (Exception e) {
            log.error("exception in save() :: " + e.getMessage());
            response.put("status", "exception");
            response.put("msg","Please try again!");
        }
        return response;
    }
     
      @PostMapping("/getSingleStateData")
     public Map getSingleStateData(@RequestBody String body) {         
        Map response = new HashMap();
        try {       	
            Long id = mapper.readValue(EncryptDecryptUtils.decrypt(body), Long.class);
            response = service.getSingleStateData(id);
        } catch (Exception e) {
            log.error("exception in save() :: " + e.getMessage());
            response.put("status", "exception");
            response.put("msg","Please try again!");
        }
        return response;
    }
    
    
}
