/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.DeductionLoan;
import com.realcoderz.service.DeductionLoanService;
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
@RequestMapping("/deductionLoan")
public class DeductionLoanController {
    
     static final Logger logger = LoggerFactory.getLogger(DeductionLoan.class);
     
     ObjectMapper mapper = new ObjectMapper();
   
     @Autowired
     private DeductionLoanService deductionLoanService;
     
     
        @PostMapping("/getAllDeductionLoan")
        public Map getAllDeductionLoan(@RequestBody String data,@RequestParam(required = false)String search) { 
        Map resultMap = new HashMap<>();
        try { 
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            search=search!=null?search.toString():"";
            if(map.get("flag") !=null && Boolean.parseBoolean(map.get("flag").toString())){
                resultMap = deductionLoanService.getAllDeductionoanOfEmployee(Long.parseLong(map.get("employeeId").toString()));
            }
            else{                
                      resultMap = deductionLoanService.getAllDeductionLoan(Long.parseLong(map.get("organizationId").toString()),search);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
     @PostMapping("/saveDeductionLoan")
     public Map saveDeductionLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionLoanService.saveDeductionLoan(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
     @PostMapping("/approvedOrRejectDeductionLoan")
     public Map approvedOrRejectDeductionLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionLoanService.approvedOrRejectDeductionLoan(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    } 
     
     @PostMapping("/adjustDeductionLoan")
     public Map loanAdjustment(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionLoanService.loanAdjustment(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    } 
     
    @GetMapping("/downloadDeductionLoanData")
    public ResponseEntity<byte[]> downloadDeductionLoanData(@RequestParam()String organizationId,HttpServletRequest request) {
        try { 
        Long orgId = Long.parseLong(organizationId);
        return deductionLoanService.downloadDeductionLoanData(orgId,request);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
        
}
