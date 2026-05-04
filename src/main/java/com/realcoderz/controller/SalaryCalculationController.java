/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SalaryCalculationService;
import com.realcoderz.service.TaxService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
 * @author Lalit Raghav
 */
@RestController
@RequestMapping("/salaryCalculation")
public class SalaryCalculationController {

    static final Logger logger = LoggerFactory.getLogger(SalaryCalculationController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SalaryCalculationService salaryCalculationService;
    
    @Autowired
    private TaxService taxService;

    @PostMapping("/getYearTaxandHra")
    public Map getSalaryCalculationYearWise(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {

            resultMap = salaryCalculationService.calculateyearWorkDay(data, request);
             // resultMap = salaryCalculationService.calculateyearWorkDayPreviousVersion(data, request);
            
        } catch (Exception ex) {
            logger.info("Problem in SalaryCalculationController -> getSalaryCalculationYearWise() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/savetax")
    public Map saveAllTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = salaryCalculationService.saveAllTax(map);
        } catch (Exception ex) {
            logger.info("Problem in SalaryCalculationController -> saveAllTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/updateAllowance")
    public Map updateAllowanceTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = salaryCalculationService.updateAllowanceTaxNew(map);
        } catch (Exception ex) {
            logger.info("Problem in SalaryCalculationController -> updateAllowanceTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/editTds")
    public Map updateMonthlytTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = salaryCalculationService.updateTds(map);
        } catch (Exception ex) {
            logger.info("Problem in SalaryCalculationController -> updateMonthlytTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/calculateTaxWhileComparing")
    public Map calculateTaxWhileComparing(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {

           // resultMap = salaryCalculationService.calculateyearWorkDay(data, request);
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

           //  resultMap = salaryCalculationService.calculateyearWorkDayPreviousVersion(data, request);
             
             resultMap= taxService.calculateTaxWhileComparingTax(map);
            
        } catch (Exception ex) {
            logger.info("Problem in SalaryCalculationController -> getSalaryCalculationYearWise() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}
