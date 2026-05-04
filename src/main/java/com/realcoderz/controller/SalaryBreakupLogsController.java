/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.DeductionLogsController.logger;
import com.realcoderz.service.SalaryBreakupLogsService;
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
 * @author lalit Raghav
 */
@RestController
@RequestMapping(path = "/salaryBreakupLog")
public class SalaryBreakupLogsController {
    
    static final Logger logger = LoggerFactory.getLogger(SalaryBreakupLogsController.class);

    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private SalaryBreakupLogsService salaryBreakupLogsService;
    
    @PostMapping(path="/save")
    public Map salaryBreakupSave(@RequestBody String data)
    {
        Map resultMap=new HashMap();
        
        try
        {
            Map map= mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap=salaryBreakupLogsService.salaryBreakupsave(map);
            
            
        }
        
        catch(Exception ex)
        {
            
             resultMap.clear();
             logger.info("Problem in SalaryBreakupLogsController -> save() :: ", ex);
             resultMap.put("status","exception");
        }
        return resultMap;
    }
    
    
}
