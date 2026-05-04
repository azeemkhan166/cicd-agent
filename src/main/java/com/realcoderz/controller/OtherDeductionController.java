/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.EmployeeNetPayController.logger;
import com.realcoderz.service.OtherDeductionService;
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
@RequestMapping("/otherDeduction")
public class OtherDeductionController {
    
     static final Logger logger = LoggerFactory.getLogger(OtherDeductionController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OtherDeductionService otherDeductionService;
    
     @PostMapping("/get")
    public Map getEmployeeIncomeTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = otherDeductionService.getTDS(map);
        } catch (Exception ex) {
            logger.info("Problem in OtherDeductionController -> getEmployeeIncomeTax() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
    
    
}
