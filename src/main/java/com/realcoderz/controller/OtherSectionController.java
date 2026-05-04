/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.OtherSectionService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lalit Raghav
 */
@RestController
@RequestMapping("/otherSection")
public class OtherSectionController {

    ObjectMapper mapper = new ObjectMapper();
    static final Logger logger = LoggerFactory.getLogger(Employeecontroller.class);

    @Autowired
    private OtherSectionService otherSectionservice;

    @PostMapping(path = "/getaccountsdetails")
    public Map getOtherSectionById(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {

            resultMap = otherSectionservice.findOtherSectionById(data);
        } catch (Exception ex) {
            logger.info("Problem in OtherSectionController -> getOtherSectionById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
