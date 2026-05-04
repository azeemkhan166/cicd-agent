/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.AllowanceLogsService;
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
@RequestMapping(path = "/allowanceLogs")
public class AllowanceLogsController {
    
    static final Logger logger = LoggerFactory.getLogger(AllowanceLogsController.class);

    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private AllowanceLogsService allowanceLogsService;
    
    
  @PostMapping(path = "/save")
  
  public Map save(@RequestBody String data)
  {
      Map resultMap=new HashMap();
      
      try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceLogsService.saveLogs(map);
        } catch (Exception ex) {
            
            logger.info("Problem in AllowanceLogsController -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
      return resultMap;
  }
  
  @PostMapping(path = "/getAllAllowanceLogs")
  public Map getAllowanceLog(@RequestBody String data)
  {
      Map resultMap=new HashMap();
      
      try
      {  
         Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

          resultMap=allowanceLogsService.getAllowanceLogs(map);
          
          resultMap.put("status","success");
      }
      catch(Exception ex)
      {
          resultMap.put("status","exception");
          logger.info("Problem in AllowanceLogsController -> getAllowanceLog() :: ", ex);
      }
      
      return resultMap;   
  }
    
}
