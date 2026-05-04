/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AllowanceLogs;
import com.realcoderz.repository.AllowanceLogsRepository;
import com.realcoderz.service.AllowanceLogsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
/**
 *
 * @author Lalit Raghav
 */
@Service
@RequiredArgsConstructor
public class AllowanceLogsServiceImpl implements AllowanceLogsService{
  ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(AllowanceLogsServiceImpl.class);

    private final AllowanceLogsRepository allowancelogsRepo;
    
    @Override
    public Map saveLogs(Map map) {
        Map resultMap=new HashMap();
         try
         {
             AllowanceLogs allowancelogs=mapper.convertValue(map,AllowanceLogs.class);
             
             if(allowancelogs!= null)
             {
                 allowancelogsRepo.save(allowancelogs);
                 resultMap.clear();
                 resultMap.put("status","success");
                 
             }
             else
             {
                  resultMap.put("status","error");
             }
               
         }
         catch(Exception ex)
         {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceLogsServiceImpl -> saveLogs() :: ", ex);
         }
        
        return resultMap;
    }

    @Override
    public Map getAllowanceLogs(Map map) {
        Map resultMap=new HashMap();
        
        try
        {
            List<LinkedCaseInsensitiveMap> allowanceLogs = allowancelogsRepo.fetchDataByAllowanceName(map.get("allowance_name").toString());
             resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", allowanceLogs);
            
        }
        catch(Exception ex)
        {
            resultMap.put("status", "exception");
            logger.info("Problem in AllowanceLogsServiceImpl -> getAllowanceLogs() :: ", ex);

        }
        return resultMap;
    }
    
}
