/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.PendingItemService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tause
 */
@RestController
@RequestMapping("/pending-items")
public class PendingItemsController {
    
      static final Logger logger = LoggerFactory.getLogger(PendingItemsController.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PendingItemService pendingService;
    
    @PostMapping("/get-pending-items")
    public Map getPendingItems(@RequestBody String data) {
        Map response=new HashMap();
        try{
      Map  map = objectMapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
      response= pendingService.pendingLoans(map);     
        }catch(Exception e){
            response.put("status","exception");
            response.put("msg",e.getMessage());
            e.printStackTrace();
            logger.error("exception in  getPendingItems->() "+e.getMessage());
        }
        return response;
        
    }   
    
     @PostMapping("/get-archieve-items")
    public Map getArcieveItems(@RequestBody String data) {
        Map response=new HashMap();
        try{
      Map  map = objectMapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
      response= pendingService.archiveLoansAndInvsetment(map);
        }catch(Exception e){
            response.put("status","exception");
            response.put("msg",e.getMessage());
            e.printStackTrace();
            logger.error("exception in  getPendingItems->() "+e.getMessage());
        }
        return response;
        
    }   
}
