package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SalaryHistoryRecordService;
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
@RequestMapping(path = "/SalaryHistory")
public class SalaryHistoryRecordController {
    
    static final Logger logger = LoggerFactory.getLogger(SalaryHistoryRecordController.class);

    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private SalaryHistoryRecordService HistoryRecordService;

     @PostMapping(path = "/saveSalaryDetails")
    public Map saveSalaryDetails(@RequestBody String data) {
        logger.info("In SalaryHistoryRecordController -> saveSalaryDetails method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           logger.info("In SalaryHistoryRecordController -> saveSalaryDetails method :: Request Data :-" + map);
            resultMap = HistoryRecordService.saveSalaryHistoryRecord(map);
            logger.info("In SalaryHistoryRecordController -> saveSalaryDetails method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryHistoryRecordController -> saveSalaryDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryHistoryRecordController -> saveSalaryDetails method executed succcessfuly !!");
        return resultMap;

    }
    
    @PostMapping("/getSalaryHistoryRecord")
    public Map getSalaryHistoryRecord(@RequestBody String data) {
        logger.info("In SalaryHistoryRecordController -> getSalaryHistoryRecord method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In SalaryHistoryRecordController -> getSalaryHistoryRecord method :: Request Data :-" + map);
            resultMap = HistoryRecordService.getSalaryHistoryRecord(map);
            logger.info("In SalaryHistoryRecordController -> getSalaryHistoryRecord method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryHistoryRecordController -> getSalaryHistoryRecord() :: ", ex);
             resultMap.clear();
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryHistoryRecordController -> getSalaryHistoryRecord method executed succcessfuly !!");
        return resultMap;
    }
    
    @PostMapping(value = "/GrossSalaryUpdating")
    public Map GrossSalaryUpdate(@RequestBody String data) {
        logger.info("In SalaryHistoryRecordController -> GrossSalaryUpdate method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In SalaryHistoryRecordController -> GrossSalaryUpdate method :: Request Data :-" + map);
            resultMap = HistoryRecordService.GrossSalaryUpdate(map);
            logger.info("In SalaryHistoryRecordController -> GrossSalaryUpdate method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryHistoryRecordController -> GrossSalaryUpdate() :: ", ex);
             resultMap.clear();
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryHistoryRecordController -> GrossSalaryUpdate method executed succcessfuly !!");
        return resultMap;
    }
    
    @PostMapping(value = "/saveGrossSalaryInEmployee")
    public Map saveGrossSalaryInEmployee() {
        logger.info("In SalaryHistoryRecordController -> saveGrossSalaryInEmployee method started.");
        Map resultMap = new HashMap<>();
        try {
            resultMap = HistoryRecordService.saveGrossSalaryInEmployee();
             logger.info("In SalaryHistoryRecordController -> saveGrossSalaryInEmployee method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryHistoryRecordController -> saveGrossSalaryBaseOnEffectiveDate() :: ", ex);
             resultMap.clear();
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryHistoryRecordController -> saveGrossSalaryInEmployee method executed succcessfuly !!");
        return resultMap;
    }
    
//    @PostMapping(value = "/GrossSalaryUpdating")
//    public Map UpdateSalary(@RequestBody String data) {
//        logger.info("In SalaryHistoryRecordController -> UpdateSalary method started.");
//        Map resultMap = new HashMap<>();
//        try {
////            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
////            logger.info("In SalaryHistoryRecordController -> UpdateSalary method :: Request Data :-" + map);
//            resultMap = HistoryRecordService.UpdateSalary();
//            logger.info("In SalaryHistoryRecordController -> UpdateSalary method :: Response Data :-" + resultMap);
//        } catch (Exception ex) {
//            logger.info("Problem in SalaryHistoryRecordController -> UpdateSalary() :: ", ex);
//             resultMap.clear();
//            resultMap.put("status", "exception");
//        }
//        logger.info("In SalaryHistoryRecordController -> UpdateSalary method executed succcessfuly !!");
//        return resultMap;
//    }
}