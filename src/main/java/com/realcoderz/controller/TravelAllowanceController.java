
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.realcoderz.service.TravelAllowanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
 * @author Bipul Singh
 */
@RestController
@RequestMapping(path = "/travel")
public class TravelAllowanceController {

    static final Logger logger = LoggerFactory.getLogger(TravelAllowanceController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TravelAllowanceService travelAllowanceService;

    @PostMapping(path = "/list")
    public Map list(@RequestBody String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = travelAllowanceService.list(map);
        } catch (Exception ex) {
            logger.info("Problem in TravelAllowanceController -> list() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/save")
    public Map saveOrUpdate(@RequestBody String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = travelAllowanceService.saveOrUpdate(map);
        } catch (Exception ex) {
            logger.info("Problem in TravelAllowanceController -> saveOrUpdate() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/get")
    public Map getTravelAllowance(@RequestBody String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = travelAllowanceService.findById(map);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceController -> getTravelAllowance() :: ", ex);
        }

        return resultMap;
    }

    @PostMapping(path = "/getAmount")
    public Map getTravelAllowanceAmount(@RequestBody String data , HttpServletRequest request) {
        Map resultMap = new HashMap();
        try {
            resultMap = travelAllowanceService.getTravelAllowanceAmount(data,request);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceController -> getTravelAllowanceAmount() :: ", ex);
        }

        return resultMap;
    }
    
    @PostMapping(path = "/delete")
    public Map deleteTravelAllowance(@RequestBody String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = travelAllowanceService.deleteTravelAllowance(map);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in TravelAllowanceController -> deleteTravelAllowance() :: ", ex);
        }

        return resultMap;
    }

}