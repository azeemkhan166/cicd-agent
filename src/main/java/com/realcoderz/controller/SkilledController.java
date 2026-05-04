/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SkilledService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/skilled")
public class SkilledController {
    
    @Autowired
    private SkilledService skilledService;
    
    ObjectMapper mapper = new ObjectMapper();
    
    
        @PostMapping("/save")
    public Map saveSkilled(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = skilledService.save(map);
        } catch (Exception ex) {
        
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
       @PostMapping("/getAllSkilled")
    public Map getAllSkilled(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = skilledService.getAllSkilled(map);
        } catch (Exception ex) {
          
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
     @PostMapping("/findById")
    public Map findById(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = skilledService.findById(map);
        } catch (Exception ex) {
           
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
            @PostMapping("/deleteById")
    public Map deleteById(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = skilledService.deleteById(map);
        } catch (Exception ex) {
          
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}
