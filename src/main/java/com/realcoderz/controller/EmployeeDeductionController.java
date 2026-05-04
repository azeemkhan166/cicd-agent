/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.EmployeeDeductionService;
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

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping(path = "/employeeDeduction")
public class EmployeeDeductionController {

    static final Logger logger = LoggerFactory.getLogger(SalaryBreakupController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EmployeeDeductionService deductionService;

    @PostMapping(path = "/getAllEmployeeAllowances")
    public Map getDeduction(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.getEmployeeDeduction(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in EmployeeDeductionController -> getDeduction() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }

        return resultMap;

    }

    @PostMapping("/save")
    public Map saveDeduction(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = deductionService.saveEmployeeDeduction(map);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> saveAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
