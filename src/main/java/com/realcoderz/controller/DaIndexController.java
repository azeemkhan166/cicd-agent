/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.AllowanceController.logger;
import com.realcoderz.service.DaIndexService;
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
 * @author Admin
 */
@RestController
@RequestMapping("/daIndex")
public class DaIndexController {

    static final Logger logger = LoggerFactory.getLogger(DaIndexController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DaIndexService daIndexService;

    @PostMapping("/getDaIndexCalculation")
    public Map getDaIndexCalculation(@RequestBody String data) {

        Map resultMap = new HashMap();

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = daIndexService.getDaIndexCalculation(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in DaIndexController -> getDaIndexCalculation() :: ", e);
            resultMap.put("status", "exception");

        }
        return resultMap;
    }

    @PostMapping("/getCPICalculation")
    public Map getCPICalculation(@RequestBody String data) {

        Map resultMap = new HashMap();

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = daIndexService.getCPICalculation(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in DaIndexControllerDaIndexController -> getCPICalculation() :: ", e);
            resultMap.put("status", "exception");

        }
        return resultMap;
    }

    @PostMapping("/saveCPICalculation")
    public Map saveCPICalculation(@RequestBody String data) {

        Map resultMap = new HashMap();

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = daIndexService.saveCPICalculation(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in DaIndexControllerDaIndexController -> saveCPICalculation() :: ", e);
            resultMap.put("status", "exception");

        }
        return resultMap;
    }

    @PostMapping("/getAllDaIndexData")
    public Map getAllDaIndexData(@RequestBody String data) {

        Map resultMap = new HashMap();

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = daIndexService.getAllDaIndexData(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in DaIndexControllerDaIndexController -> getAllDaIndexData() :: ", e);
            resultMap.put("status", "exception");

        }
        return resultMap;
    }

    @PostMapping("/getDaIndexById")
    public Map getDaIndexById(@RequestBody String data) {

        Map resultMap = new HashMap();

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = daIndexService.getDaIndexById(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in DaIndexControllerDaIndexController -> getAllDaIndexData() :: ", e);
            resultMap.put("status", "exception");

        }
        return resultMap;
    }

}
