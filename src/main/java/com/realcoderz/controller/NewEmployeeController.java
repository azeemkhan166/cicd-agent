package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.NewEmployeeService;
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
@RequestMapping(path = "/payrollEmployee")
public class NewEmployeeController {
    
    @Autowired
    private NewEmployeeService service;
    
    static final Logger logger = LoggerFactory.getLogger(Employeecontroller.class);
    ObjectMapper mapper = new ObjectMapper();
    
    //API for saving Gross Salary in Payroll Employee Database
    @PostMapping(path = "/saveGrossSalary")
    public Map saveSalary(@RequestBody String data){
        logger.info("In NewEmployeeController -> saveSalary method started.");
       Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In NewEmployeeController -> saveSalary method :: Request Data :-" + map);
            resultMap = service.saveSalary(map);
             logger.info("In NewEmployeeController -> saveSalary method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in NewEmployeeController -> saveSalary() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In NewEmployeeController -> saveSalary method executed succcessfuly !!");
        return resultMap;  
    }
    
     @PostMapping(path = "/isExistSalary")
    public Map isExistSalary(@RequestBody String data){
        logger.info("In NewEmployeeController -> isExistSalary method started.");
       Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In NewEmployeeController -> isExistSalary method :: Request Data :-" + map);
            resultMap = service.isExistSalary(Long.parseLong("id"));
            logger.info("In NewEmployeeController -> isExistSalary method :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in NewEmployeeController -> isExistSalary() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In NewEmployeeController -> isExistSalary method executed succcessfuly !!");
        return resultMap;  
    }
}