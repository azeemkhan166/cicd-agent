/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.OtherAllowancesService;
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
 * @author Astha & Mayank
 */
@RestController
@RequestMapping(path = "/otherAllowances")
public class OtherAllowancesController {

    static final Logger logger = LoggerFactory.getLogger(OtherAllowancesController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OtherAllowancesService otherAllowanceService;

    @PostMapping(path = "/getAllotherAllowances")
    public Map getOtherAllowance(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = otherAllowanceService.getOtherAllowances(map);
        } catch (Exception ex) {
            logger.info("Problem in OtherAllowancesController -> getOtherAllowance() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }

        return resultMap;

    }

    @PostMapping("/save")
    public Map saveOtherAllowances(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = otherAllowanceService.saveOtherAllowances(map);
        } catch (Exception ex) {
            logger.info("Problem in OtherAllowancesController -> saveOtherAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
