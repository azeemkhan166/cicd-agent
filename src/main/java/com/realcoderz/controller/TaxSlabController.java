/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.TaxSlabService;
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
@RequestMapping("/taxSlab")
public class TaxSlabController {

    static final Logger logger = LoggerFactory.getLogger(TaxSlabController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TaxSlabService taxSlabService;

    @PostMapping("/saveTaxSlabs")
    public Map saveTaxSlab(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
//            resultMap = taxSlabService.save(map);
            resultMap = taxSlabService.saveAllTaxSlabs(map);
        } catch (Exception ex) {
            logger.info("Problem in TaxSlabController -> saveTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getTaxSlabs")
    public Map getTaxSlab() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = taxSlabService.getAllTaxSlabs();
        } catch (Exception ex) {
            logger.info("Problem in TaxSlabController -> getTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/update")
    public Map updateTaxSlab(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = taxSlabService.updateTaxSlabs(map);
        } catch (Exception ex) {
            logger.info("Problem in TaxSlabController -> updateTaxSlabs() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
   
}
