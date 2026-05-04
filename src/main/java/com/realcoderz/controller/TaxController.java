/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Tax;
import com.realcoderz.service.TaxService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
 */
@RestController
@RequestMapping("/tax")
public class TaxController {

    static final Logger logger = LoggerFactory.getLogger(TaxController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired

    private TaxService taxService;

    @PostMapping("/save")
    public Map saveTax(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = taxService.saveTax(map);
        } catch (Exception ex) {
            logger.info("Problem in TaxController -> saveTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/get")
    public Map getTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = taxService.getTax(map);
        } catch (Exception ex) {
            logger.info("Problem in TaxController -> getTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/saveTaxInBulk")
    public Map saveTaxInBulk(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
              List<Tax> tax = Arrays
                    .asList(mapper.readValue(EncryptDecryptUtils.decrypt(data), Tax[].class));
            resultMap = taxService.saveTaxInBulk(tax);
        } catch (Exception ex) {
            logger.info("Problem in TaxController -> getTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/gettaxslip")
    public Map getTaxSlip(@RequestBody String data){
       Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            Long employeeId= map.get("emp_id")!=null?Long.parseLong(map.get("emp_id").toString()):0 ;
            int month= map.get("selected_month")!=null?Integer.parseInt(map.get("selected_month").toString()):0;
            int year= map.get("selected_year")!=null?Integer.parseInt(map.get("selected_year").toString()):0;
            resultMap = taxService.getTaxSlip(employeeId,month,year);
        } catch (Exception ex) {
            logger.info("Problem in TaxController -> getTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}
