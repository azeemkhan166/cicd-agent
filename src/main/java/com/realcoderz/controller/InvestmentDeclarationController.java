/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.InvestmentDeclarationService;
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
@RequestMapping("/investmentdeclaration")
public class InvestmentDeclarationController {

    static final Logger logger = LoggerFactory.getLogger(InvestmentDeclarationController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public InvestmentDeclarationService inverstmentdeclarationservice;

    @PostMapping("/alldeclarationsave")
    public Map save(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = inverstmentdeclarationservice.save(map);

        } catch (Exception ex) {
            logger.info("Problem in InvestmentDeclarationController -> save() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/saveForAcc")
    public Map saveForAcc(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = inverstmentdeclarationservice.saveForAcc(map);
        } catch (Exception ex) {
            logger.info("Problem in InvestmentDeclarationController -> saveForAcc() :: ", ex);
            resultMap.put("status", "exception");
            resultMap.put("msg", "something went wrong");
        }
        return resultMap;
    }

    @PostMapping("/getDeclarationByEmployeeId")
    public Map getDeclarationDataByEmployeeId(@RequestBody String data) {
        System.out.println("data print" + data);
        Map resultMap = new HashMap<>();
        try {

            resultMap = inverstmentdeclarationservice.getDeclarationByEmployeeId(data);
        } catch (Exception ex) {
            logger.info("Problem in InvestmentDeclarationController -> getDeclarationDataByEmployeeId() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    // on basics of fy anf emp id fetch Employee Choose Tax Slab Key

    @PostMapping("/getTaxSlabKey")
    public Map getTaxSlabKeyByEmpIdOrFy(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {

            resultMap = inverstmentdeclarationservice.getTaxSlabKeyByEmpIdOrFy(data);
        } catch (Exception ex) {
            logger.info("Problem in InvestmentDeclarationController -> getTaxSlabKeyByEmpIdOrFy() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @PostMapping("/currentYear")
    public Map getCurrentyearAndMonth() {
        Map resultMap = new HashMap();
        try {
            resultMap = inverstmentdeclarationservice.getCurrentFYYear();
        } catch (Exception ex) {
            logger.info("Problem in InvestmentDeclarationController -> getCurrentyearAndMonth() :: ", ex);

            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
