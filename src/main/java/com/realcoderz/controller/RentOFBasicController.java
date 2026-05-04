/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.RentOfBasicService;
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
 * @author lalit raghav
 * edited By Astha
 */
@RestController
@RequestMapping(path="/basicRent")
public class RentOFBasicController {
    
    
    static final Logger logger = LoggerFactory.getLogger(RentOFBasicController.class);

    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    public RentOfBasicService rentofBasicservice;
    
    @PostMapping(path="/addRentcalculation")
   public Map saveRentOfBasic(@RequestBody String data)
    {
        Map resultMap = new HashMap();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map != null) {
                resultMap = rentofBasicservice.saveforRent(map);
            } else {
                resultMap.put("status", "error");
            }

        } catch (Exception ex) {
            logger.info("Problem in RentOFBasicController -> saveRentOfBasic() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;

    }
   
   @PostMapping("/getBasicRent")
    public Map getBasicRent() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = rentofBasicservice.getRentOFBasic();
        } catch (Exception ex) {
            logger.info("Problem in RentOFBasicController -> getBasicRent() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
   
    
    
    
}
