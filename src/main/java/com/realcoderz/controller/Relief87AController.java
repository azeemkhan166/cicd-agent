/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.Relief87AService;
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
@RequestMapping("/relief87A")
public class Relief87AController {

    static final Logger logger = LoggerFactory.getLogger(Relief87AController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Relief87AService relief87AService;

    @PostMapping("/save")
    public Map saveRelief87A(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = relief87AService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in Relief87AController -> saveRelief87A() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getRelief87A")
    public Map getRelief87A() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = relief87AService.getRelief87A();
        } catch (Exception ex) {
            logger.info("Problem in Relief87AController -> getRelief87A() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
