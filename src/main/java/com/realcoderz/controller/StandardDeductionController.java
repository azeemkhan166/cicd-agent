/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.PayrollSettingController.logger;
import com.realcoderz.service.StandardDeductionService;
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
 * @author Lalit Raghav
 */
@RestController
@RequestMapping(path = "/standard_deduction")
public class StandardDeductionController {

    static final Logger logger = LoggerFactory.getLogger(AllowanceController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StandardDeductionService standardDeductionService;

    @PostMapping(path = "/save")

    public Map saveStandardDeduction(@RequestBody String data) {
        Map resultMap = new HashMap();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = standardDeductionService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in StandardDeductionController -> saveStandardDeduction() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
     @PostMapping(path = "/getStandardDeduction")
    public Map getStandardDeduction(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
          Map  map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
          resultMap = standardDeductionService.get(map);
        } catch (Exception ex) {
            logger.error("Problem in StandardDeductionController -> getStandardDeduction() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;

    }
   

}
