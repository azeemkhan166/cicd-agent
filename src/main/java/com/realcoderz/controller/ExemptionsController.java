/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.ExemptionsService;
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
@RequestMapping("/exemptions")
public class ExemptionsController {

    static final Logger logger = LoggerFactory.getLogger(ExemptionsController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired

    private ExemptionsService exemptionsService;

    @PostMapping("/save")
    public Map saveExemptions(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = exemptionsService.saveExemptions(map);
        } catch (Exception ex) {
            logger.info("Problem in ExemptionsController -> saveExemptions() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    //    Get Exemptions
    @PostMapping("/get")
    public Map getExemptions(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = exemptionsService.getExemptions(map);
        } catch (Exception ex) {
            logger.info("Problem in ExemptionsController -> getExemptions() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
