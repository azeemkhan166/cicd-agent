/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AppraisalSalary;
import com.realcoderz.service.AppraisalSalaryService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/appraisal")
public class AppraisalSalaryController {
    
    
    static final Logger logger = LoggerFactory.getLogger(AppraisalSalaryController.class);
    
     ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private AppraisalSalaryService appraisalSalaryService;
    
   
    @PostMapping("/save")
    public Map saveAppraisalLetterData(@RequestBody String data) {
        logger.info("saveAppraisalLetterData called");
        Map resultMap = new HashMap<>();

        try {
            AppraisalSalary map = mapper.readValue(EncryptDecryptUtils.decrypt(data), AppraisalSalary.class);
            resultMap =appraisalSalaryService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in saveAppraisalLetterData -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    } 
    

    @PostMapping("/getAppraisalData")
    public Map getAppraisalLetterData(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            LinkedCaseInsensitiveMap map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap =appraisalSalaryService.getAppraisalData(map);
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    } 
    
    
        @GetMapping("/downloadAppraisalReport")
    public ResponseEntity<byte[]> downloadAppraisalReport(@RequestParam()String organizationId,HttpServletRequest request) {
        try { 
        Long orgId = Long.parseLong(organizationId);
        return appraisalSalaryService.downloadAppraisalReport(orgId,request);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
}
