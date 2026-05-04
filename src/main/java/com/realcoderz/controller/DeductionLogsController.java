
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.DeductionLogsService;
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

@RestController
@RequestMapping(path = "/deductionLogs")
public class DeductionLogsController {
    
    static final Logger logger = LoggerFactory.getLogger(DeductionLogsController.class);

    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private DeductionLogsService deductionLogsService;
    
    
  @PostMapping(path = "/save")
  
  public Map save(@RequestBody String data)
  {
      Map resultMap=new HashMap();
      
      try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("deduction data"+map);
            resultMap = deductionLogsService.saveLogs(map);
        } catch (Exception ex) {
            
            logger.info("Problem in DeductionLogsController -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
      return resultMap;
  }
  
  @PostMapping(path = "/getAllDeductionLogs")
  public Map getAllowanceLog(@RequestBody String data)
  {
      Map resultMap=new HashMap();
      
      try
      {  
         Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

          resultMap=deductionLogsService.getDeductionLogs(map);
          
          resultMap.put("status","success");
      }
      catch(Exception ex)
      {
          resultMap.put("status","exception");
          logger.info("Problem in DeductionLogsController -> getDeductionLog() :: ", ex);
      }
      
      return resultMap;   
  }
    
}
