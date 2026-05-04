package com.realcoderz.controller;

import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import com.realcoderz.util.EncryptDecryptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.PerksandPerquisiteService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping("/PerksandPerquisite")
public class PerksandPerquisiteController {
    
    static final Logger logger = LoggerFactory.getLogger(PerksandPerquisiteController.class);
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private PerksandPerquisiteService service;
    
    @PostMapping("/savePerksandPerquisite")
    public Map savePerksandPerquisite(@RequestBody String data,HttpServletRequest request) {
        logger.info("In PerksandPerquisiteController -> savePerksandPerquisite method execution .....!");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
        logger.info("In PerksandPerquisiteController -> savePerksandPerquisite method  Request Data :-" + map); 
            resultMap = service.savePerksandPerquisite(map,request, data);
        logger.info("In PerksandPerquisiteController -> savePerksandPerquisite method  Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in PerksandPerquisiteController -> savePerksandPerquisite() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
       logger.info("In PerksandPerquisiteController -> savePerksandPerquisite method executed succcessfuly ...!!");
        return resultMap;
    }
    
      @PostMapping("/getPerksandPerquisite")
    public Map getPerksandPerquisite(@RequestBody String data) {
       logger.info("In PerksandPerquisiteController -> getPerksandPerquisite method execution .....!");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           logger.info("In PerksandPerquisiteController -> getPerksandPerquisite method  Request Data :-" + map); 
            resultMap = service.getPerksandPerquisite(Long.parseLong(map.get("organization_id").toString()),Long.parseLong(map.get("employee_id").toString()));
        logger.info("In PerksandPerquisiteController -> getPerksandPerquisite method  Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in PerksandPerquisiteController -> getPerksandPerquisite() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
      logger.info("In PerksandPerquisiteController -> getPerksandPerquisite method executed succcessfuly ...!!");
        return resultMap;
    }
    
     @PostMapping("/getDataById")
     public Map getDataById(@RequestBody String data) {
        logger.info("In PerksandPerquisiteController -> getDataById method started.");
        Map resultMap = new HashMap();
        try {
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In PerksandPerquisiteController -> getDataById()  :: Request Data :-" + map);
            resultMap = service.getDataById(map);
            logger.info("In PerksandPerquisiteController -> getDataById()  :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in PerksandPerquisiteController -> getDataById() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In PerksandPerquisiteController -> getDataById() method executed succcessfuly !!");
        return resultMap;
    }
    
}