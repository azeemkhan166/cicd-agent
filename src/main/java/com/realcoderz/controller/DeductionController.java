/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.AllowanceController.logger;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.realcoderz.service.DeductionService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Mayank
 */
@RestController
@RequestMapping("/deduction")
public class DeductionController {

    static final Logger logger = LoggerFactory.getLogger(DeductionController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DeductionService deductionService;

//    Save Deductions
    @PostMapping("/save")
    public Map saveDeduction(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    Get Deductions
    @PostMapping("/get")
    public Map getDeductions(@RequestBody String data, HttpServletRequest request,@RequestParam(required = false) String search) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            search=search!=null?search:"";
            resultMap = deductionService.fetch(Long.parseLong(map.get("org_id").toString()), request,search);
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> getDeductions() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    DeductionsById
    @PostMapping("/findById")
    public Map findById(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.findById(Long.parseLong(map.get("id").toString()));

        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> findById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    Delete Deductions
    @PostMapping("/delete")
    public Map deleteDeductionById(@RequestBody String data) {
        Map resultMap = null;
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.delete(Long.parseLong(map.get("id").toString()),map.get("employee_type").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> deleteDeductionById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    DeductionsByName
    @PostMapping("/findByNames")
    public Map findByNames(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = deductionService.fetchbyDeductionName(Long.parseLong(map.get("deduction_id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> findByNames() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    GetApproved Deductions
    @PostMapping("/getApprovedDeductions")
    public Map getApprovedDeductions(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.fetchApprovedDeductions(Long.parseLong(map.get("org_id").toString()),Integer.parseInt(map.get("month").toString()),Integer.parseInt(map.get("year").toString()), map.get("employee_Type").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> getApprovedDeductions() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    AlreadyExist Deductions
    @PostMapping("/isExist")
    public Map isExist(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.isAlreadyExist(map.get("name").toString(), Long.parseLong(map.get("org_id").toString()), map.get("employee_type").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> isExist() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getDeductionNameForSuperAdmin")
    public Map getDeductionNameForSuperAdmin() {
        Map resultMap = new HashMap<>();
        try {
            resultMap = deductionService.getDeductionForSuperAdmin();
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> getDeductionNameForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getParticularDeductionDataForSuperAdmin")
    public Map getParticularDeductionDataForSuperAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.findById(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> getParticularDeductionDataForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getDeductionNameForOrganization")
    public Map getDeductionsForOrganization(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.getDeductionNameForOrganization(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> getDeductionsForOrganization() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/checkDeductionType")
    public Map checkDeductionType(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.checkDeductionType(map.get("name").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> checkDeductionType() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/isExistForSuperAdmin")
    public Map isExistForSuperAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.isDeductionExistForSuperAdmin(map.get("name").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> isExistForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/approvedRejectDeducion")
    public Map approvedRejectDeducion(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.approvedRejectDeducion(Long.parseLong(map.get("id").toString()),map.get("status").toString());
        } catch (Exception ex) {
            logger.info("Problem in DeductionController -> approvedRejectDeducion() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
