/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.DeductionLogs;
import com.realcoderz.repository.DeductionLogsRepository;
import com.realcoderz.service.DeductionLogsService;
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
public class DeductionLogsServiceImpl implements DeductionLogsService{
  ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(DeductionLogsServiceImpl.class);

    private final DeductionLogsRepository deductionlogsRepo;
    
    @Override
    public Map saveLogs(Map map) {
        Map resultMap=new HashMap();
         try
         {
             DeductionLogs deductionlogs=mapper.convertValue(map,DeductionLogs.class);
             
             if(deductionlogs!= null)
             {
                 deductionlogsRepo.save(deductionlogs);
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
            logger.info("Problem in DeductionLogsServiceImpl -> saveLogs() :: ", ex);
         }
        
        return resultMap;
    }

    @Override
    public Map getDeductionLogs(Map map) {
        Map resultMap=new HashMap();
        
        try
        {
            List<LinkedCaseInsensitiveMap> deductionLogs = deductionlogsRepo.fetchDataByDeductionName(map.get("deduction_name").toString());
             resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", deductionLogs);
            
        }
        catch(Exception ex)
        {
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionLogsServiceImpl -> getDeductionLogs() :: ", ex);

        }
        return resultMap;
    }
    
}

