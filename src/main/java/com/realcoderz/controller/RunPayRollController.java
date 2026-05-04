/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.realcoderz.service.RunPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.serviceImpl.RunPayServiceImpl;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author bipulsingh
 */
@RestController
@RequestMapping("/runpay")
public class RunPayRollController {

    static final Logger logger = LoggerFactory.getLogger(RunPayRollController.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RunPayService runPayService;
    
    @Autowired
    private RunPayServiceImpl runpayserviceimpl;

    @PostMapping("/getdata")
    public Map getPayRunData(@RequestBody String data, HttpServletRequest request , @RequestParam(required = false) String search) {
        String searchWord=search!=null?search.toString():"";
        return runPayService.getPayRunData(data, request,searchWord);
    }
    
    @PostMapping("/getcustomdata")
    public Map getCustomPayRunData(@RequestBody String data, HttpServletRequest request , @RequestParam(required = false) String search) {
        String searchWord=search!=null?search.toString():"";
        return runPayService.getCustomPayRunData(data, request,searchWord);
    }

    @PostMapping("/approveAndSubmit")
    public Map saveApprovedRunPayRoll(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.saveAll(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> saveApprovedRunPayRoll() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/update")
    public Map updateRunPayRoll(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.updateRunPayRoll(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> updateRunPayRoll() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/prevdata")
    public Map getPreviousPayRunData(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.getPreviousPayRunData(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> getPreviousPayRunData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

     @PostMapping("/isPayRunSaved")
    public Map isPayrollRun(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.isPayrollSaved(Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), Long.parseLong(map.get("org_id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> isPayrollRun() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/isSalaryBreakupSaved")
    public Map isSalarySaved(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.isSalaryBreakupSavedOfThisMonth(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> isSalaryBreakupSaved() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
    @PostMapping("/updateallowanceinbulk")
    public Map<String, Object> updateAllowanceInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId) throws Exception {

        return runpayserviceimpl.updateAllowanceInBulk(file, orgId);
    }
  
    @PostMapping("/getSavedRunPayroll")
    public Map getSavedRunPayroll(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.getSavedRunPayroll(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> getSavedRunPayroll() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/deleteRunPayroll")
    public Map deleteRunPayroll(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = runPayService.deleteRunPayroll(map);
        } catch (Exception ex) {
            logger.info("Problem in RunPayRollController -> deleteRunPayroll() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}
