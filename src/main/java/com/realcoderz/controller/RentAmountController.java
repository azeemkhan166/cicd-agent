/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.PercentageOfBasicController.logger;
import com.realcoderz.service.RentAmountService;
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
@RequestMapping(path = "/rentamount")
public class RentAmountController {

    static final Logger logger = LoggerFactory.getLogger(Employeecontroller.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RentAmountService rentamountservice;

    @PostMapping("/saveallrent")
    public Map getRentAmount(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = rentamountservice.findRentById(data);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in RentAmountController -> getRentAmount() :: ", ex);
        }

        return resultMap;
    }

}
