/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.AllowanceController.logger;
import com.realcoderz.service.EmployeeNetPayService;
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
 * @author Mayank
 */
@RestController
@RequestMapping("/employeeNetPay")
public class EmployeeNetPayController {
    
    static final Logger logger = LoggerFactory.getLogger(EmployeeNetPayController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    
    private EmployeeNetPayService employeeNetPayService;
    
     @PostMapping("/get")
    public Map getEmployeesNetPay(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeNetPayService.get(map);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeNetPayController -> getEmployeesNetPay() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}
