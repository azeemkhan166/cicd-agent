/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.AllowanceController.logger;
import com.realcoderz.service.AdvanceRcmService;
import com.realcoderz.serviceImpl.AdvanceRcmServiceImpl;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/advanceRcm")
public class AdvanceRcmController {
    
    @Autowired
    private AdvanceRcmService advanceRcmService;
    
    @Autowired
    private AdvanceRcmServiceImpl advanceRcmServiceImpl;
    
    ObjectMapper mapper = new ObjectMapper();
    
    @PostMapping("/getAllAdvanceForSupervisor")
    public Map getAllAdvanceForSupervisor(@RequestBody String data,HttpServletRequest request) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.getAllAdvanceForSupervisor(map,request);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> getAllAdvance() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/saveOrUpdateStatus")
    public Map saveOrUpdateStatus(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.saveOrUpdateStatus(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> saveOrUpdateStatus() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/getAllAdvanceForRcm")
    public Map getAllAdvanceForRcm(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.getAllAdvanceForRcm(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> getAllAdvanceForRcm() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
            @PostMapping("/rcmApprovedOrReject")
    public Map rcmApprovedOrReject(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.rcmApprovedOrReject(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> rcmApprovedOrReject() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
                @PostMapping("/hoApprovedOrReject")
    public Map hoApprovedOrReject(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.hoApprovedOrReject(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> hoApprovedOrReject() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
         @GetMapping("/downloadExcelReport")
    public ResponseEntity<byte[]> downloadesicInexcelFormate(@RequestParam()String organizationId,@RequestParam()String month,@RequestParam()String year,@RequestParam()String id,@RequestParam()String siteName,HttpServletRequest request) {
        try { 
        Long orgId = Long.parseLong(organizationId);
//        Long m = Long.parseLong(month);
        Long y = Long.parseLong(year);
        Long ids = Long.parseLong(id);
        return advanceRcmService.downloadAdvanceDetailsInexcelFormate(siteName,orgId,month,y,ids,request);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
        @PostMapping("/updateAdvanceInBulk")
    public Map<String, Object> updateAdvanceInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId,@RequestParam("year") Long year,@RequestParam("month") String month,@RequestParam("id") Long id) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.ms-excel")
                && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("msg", "Only Excel files (.xls, .xlsx) are allowed");
            return error;
        }
        return advanceRcmServiceImpl.updateAdvanceInBulk(file, orgId,year,month,id);
    }
    
    @PostMapping("/getDateFormAdvance")
    public Map getDateFormAdvance(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.getDateFormAdvance(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> getDateFormAdvance() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/updateStatusInAdvance")
    public Map updateStatusInAdvance(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = advanceRcmService.updateStatusInAdvance(map);
        } catch (Exception ex) {
            logger.info("Problem in AdvanceRcmController -> updateStatusInAdvance() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}
