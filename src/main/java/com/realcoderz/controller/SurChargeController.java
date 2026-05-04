/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SurChargeService;
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
 * edited By Astha
 */
@RestController
@RequestMapping("/surCharge")
public class SurChargeController {

    static final Logger logger = LoggerFactory.getLogger(SurChargeController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SurChargeService surChargeService;

    @PostMapping("/saveAllsurCharge")
    public Map saveSurCharge(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
//            resultMap = surChargeService.save(map);
            resultMap = surChargeService.saveAllSurchages(map);
        } catch (Exception ex) {
            logger.info("Problem in SurChargeController -> saveSurCharge() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getAllsurCharge")
    public Map getSurCharge() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = surChargeService.getAllSurchages();
        } catch (Exception ex) {
            logger.info("Problem in SurChargeController -> getSurCharge() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
   @PostMapping("/update")
    public Map updateSurchage(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = surChargeService.updateSurchages(map);
        } catch (Exception ex) {
            logger.info("Problem in SurChargeController -> updateTaxSlabs() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}
