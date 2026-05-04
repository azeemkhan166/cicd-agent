/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.PercentageOfBasicService;
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
 * edited By Astha
 */
@RestController
@RequestMapping(path = "/basicPercentage")
public class PercentageOfBasicController {

    @Autowired
    public PercentageOfBasicService percentageOfBasicservice;

    static final Logger logger = LoggerFactory.getLogger(Employeecontroller.class);

    ObjectMapper mapper = new ObjectMapper();

    @PostMapping(path = "/addBasicPer")
    public Map saveBasicOfPercentage(@RequestBody String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = percentageOfBasicservice.save(map);
        } catch (Exception ex) {
            logger.info("Problem in PercentageOfBasicController -> saveBasicOfPercentage() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;

    }

     @PostMapping("/getBasicPercentage")
    public Map getBasicPercentage() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = percentageOfBasicservice.getPercentageOfBasic();
        } catch (Exception ex) {
            logger.info("Problem in PercentageOfBasicController -> getBasicPercentage() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}
