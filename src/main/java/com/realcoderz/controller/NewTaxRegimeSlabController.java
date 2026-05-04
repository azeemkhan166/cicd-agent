/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.repository.NewTaxRegimeSlabRepository;
import com.realcoderz.service.NewTaxRegimeSlabService;
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
@RequestMapping(path="/newTaxSlab")

public class NewTaxRegimeSlabController {
    
    ObjectMapper mapper=new ObjectMapper();
    
    static final Logger logger = LoggerFactory.getLogger(NewTaxRegimeSlabController.class);
    
    @Autowired
    private NewTaxRegimeSlabService newTaxRegimeSlabService;
    
    @Autowired
     private NewTaxRegimeSlabRepository newTaxRegimeRepo;

    @PostMapping(path="/save")
    
     public Map saveNewTaxRegime(@RequestBody String data)
    {
        Map resultMap=new HashMap();
        try
        {
            
          Map map=mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
          resultMap=newTaxRegimeSlabService.saveNewTaxSlab(map);
          
        }
        catch(Exception ex)
        {
            resultMap.put("status","exception");
             logger.info("Problem in NewTaxRegimeSlabController -> saveNewTaxRegime() :: ", ex);
        }
        
        
        return resultMap;   
    }
     
     @PostMapping("/newsaveTaxSlabs")
    public Map newsaveTaxSlab(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = newTaxRegimeSlabService.saveAllNewTaxSlabs(map);
        } catch (Exception ex) {
            logger.info("Problem in NewTaxRegimeSlabController -> newsaveTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
     
     @PostMapping("/get")
    public Map getNewTaxSlab(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = newTaxRegimeSlabService.fetchNewTaxSlab(map);
        } catch (Exception ex) {
            logger.info("Problem in NewTaxRegimeSlabController -> getNewTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
  @PostMapping("/getnewTaxSlabs")
    public Map getAllNewTaxSlab() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = newTaxRegimeSlabService.getAllNewTaxSlabs();
        } catch (Exception ex) {
            logger.info("Problem in NewTaxRegimeSlabController -> getAllNewTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }  
    
     @PostMapping("/deleteNewTaxSlab")
    public Map deleteNewTaxSlab(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
         Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
         newTaxRegimeRepo.deleteNewTaxRegimeSlab(Long.parseLong(map.get("id").toString()));
         resultMap.put("status", "success");
        } catch (Exception ex) {
            logger.info("Problem in NewTaxRegimeSlabController -> deleteTaxSlab() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    } 
    
}
