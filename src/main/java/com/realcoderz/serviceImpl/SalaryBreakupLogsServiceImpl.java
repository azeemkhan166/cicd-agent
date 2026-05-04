/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.SalaryBreakupLogs;
import com.realcoderz.repository.SalaryBreakupLogsRepository;
import com.realcoderz.service.SalaryBreakupLogsService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lalit
 */
@Service
public class SalaryBreakupLogsServiceImpl implements SalaryBreakupLogsService{

  ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private SalaryBreakupLogsRepository salaryBreakupLogsrepo;
    
    @Override
    public Map salaryBreakupsave(Map map) {
        Map resultMap=new HashMap();
        try
        {
            SalaryBreakupLogs salaryBreakupLog=mapper.convertValue(map,SalaryBreakupLogs.class);
            if(salaryBreakupLog!=null)
            {
               salaryBreakupLogsrepo.save(salaryBreakupLog); 
               resultMap.put("status","success");
            }
            
            else
            {
                resultMap.put("status","error");
                
            }
          
          
        }
        catch(Exception ex)
        {
            
        }
        return resultMap;
    }

    @Override
    public Map salaryBreakupFetch(Map map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
