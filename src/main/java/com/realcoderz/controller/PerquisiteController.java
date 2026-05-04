package com.realcoderz.controller;

import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import com.realcoderz.util.EncryptDecryptUtils;
import com.realcoderz.service.PerquisiteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping("/perquisite")
public class PerquisiteController {
    
    static final Logger logger = LoggerFactory.getLogger(PerquisiteController.class);
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private PerquisiteService service;
    
    @PostMapping("/savePerquisites")
    public Map savePerquisite(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = service.savePerquisite(map);
        } catch (Exception ex) {
            logger.info("Problem in PerquisiteController -> savePerquisite() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/getPerquisites")
    public Map getPerquisite(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = service.getPerquisite(Long.parseLong(map.get("organization_id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in PerquisiteController -> getPerquisite() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
         logger.info("In PerquisiteController -> getPerquisites method executed succcessfuly ...!!");
        return resultMap;
    }
    
    @PostMapping("/deletePerquisites")
    public Map deletePerquisite(@RequestBody String data) {
        Map resultMap = null;
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = service.deletePerquisite(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in PerquisiteController -> deletePerquisite() :: ", ex);
        }
        return resultMap;
    }
    
     @PostMapping("/findByPerquisiteId")
    public Map findByPerquisiteId(@RequestBody String data) {
        Map<String,Object> resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = service.findByPerquisiteId(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in PerquisiteController -> findByPerquisiteId() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}