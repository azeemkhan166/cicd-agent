/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.StandardDeduction;
import com.realcoderz.repository.StandardDeductionRepository;
import com.realcoderz.service.StandardDeductionService;
import static com.realcoderz.serviceImpl.PayrollSettingServiceImpl.logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lalit Raghav
 */
@Service
public class StandardDeductionServiceImpl implements StandardDeductionService{
    
static final Logger LOGGER = LoggerFactory.getLogger(StandardDeductionServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    public StandardDeductionRepository standardDeductionRepository;
    
    @Override
    public Map save(Map map) {

        Map resultMap=new HashMap();
        
        try
        {
            StandardDeduction  standDeduction = mapper.convertValue(map, StandardDeduction.class);
            StandardDeduction deductionDb=standardDeductionRepository.getStandardDeductionAccordingtoType(standDeduction.getFinancialYear(), standDeduction.getTypeOfRegime());
           // System.out.println("deductionDb 43"+" "+deductionDb.toString());
            
            if(deductionDb==null){
              standardDeductionRepository.save(standDeduction);  
            }else{
               deductionDb.setStandard_deduction(standDeduction.getStandard_deduction());
               standardDeductionRepository.save(deductionDb);  
            }
            resultMap.clear();
            resultMap.put("status","success");
        }
        catch(Exception ex)
        {  ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status","Exception");
        }
        
return resultMap;
    }

    @Override
    public Map get(Map map) {
        Map resultMap = new HashMap();
       int fyYear= Integer.parseInt(map.get("fyYear").toString());
       String type=map.get("type").toString();
       StandardDeduction deductionDb=standardDeductionRepository.getStandardDeductionAccordingtoType(fyYear, type);

      //  List<StandardDeduction> standardDeduction = standardDeductionRepository.getstandDeduction();
        try{
        if (deductionDb!=null) {
            resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", deductionDb);
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Standard Deduction is not Available..!");
        }}catch(Exception ex){
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in StandardDeductionServiceImpl -> get() :: ", ex);
          }
        return resultMap;
    }
    
}
