/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.common.MonthHandler;
import com.realcoderz.model.DaIndex;
import com.realcoderz.repository.DaIndexRepository;
import com.realcoderz.service.DaIndexService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class DaIndexServiceImpl implements DaIndexService{

      ObjectMapper mapper = new ObjectMapper();

      static final Logger logger = LoggerFactory.getLogger(DaIndexServiceImpl.class);

      private final DaIndexRepository daIndexRepository;

    @Override
    public Map getDaIndexCalculation(Map map) {
   
            Map resultMap=new HashMap();
            List<LinkedCaseInsensitiveMap> finalData= new ArrayList<>();
         try
         {
             List<LinkedCaseInsensitiveMap> linkingFactorData = daIndexRepository.getLinkingFactor();
             System.out.println("data");
             System.out.println(map);
             if(linkingFactorData.size()==0 || linkingFactorData.size()>1){
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Please Check your Linking Factor Data");
                    return resultMap;
             }
             System.out.println("linkingFactorData");
             System.out.println(linkingFactorData);
           int considerFrom= Integer.parseInt(map.get("considerFrom").toString());
           int considerTo=Integer.parseInt(map.get("considerTo").toString());
           int year=Integer.parseInt(map.get("year").toString());  
             
           AtomicLong counter = new AtomicLong(1); // Starting point for ID generation
          List<Map<String, Object>> result = MonthHandler.getMonthsBetween(considerFrom, considerTo);
          result.forEach(m -> {
          
              LinkedCaseInsensitiveMap json=new LinkedCaseInsensitiveMap();
              
              json.put("consideredMonth", m.get("title")+" - "+year);
              json.put("indexValue", "");
              json.put("linkingFactor2016", linkingFactorData.get(0).get("linking_factor_one"));
              json.put("linkingFactor2001", linkingFactorData.get(0).get("linking_factor_two"));
              json.put("linkingFactor1982", linkingFactorData.get(0).get("linking_factor_three"));
              json.put("daIndexValue", "");
               json.put("uniqueId",counter.getAndIncrement());
              finalData.add(json);
            });
          
          resultMap.put("status", "success"); 
          resultMap.put("value", finalData); 
         }
         catch(Exception ex)
         {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            logger.info("Problem in getDaIndexCalculation -> DaIndexServiceImpl() :: ", ex);
         }
        
        return resultMap;
        
    }

 @Override
public Map getCPICalculation(Map map) {

    Map<String, Object> resultMap = new HashMap<>();
    
    try {
        // Safely retrieve and cast the data to List<LinkedHashMap> to avoid ClassCastException
        List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>) map.get("indexData");

        // Initialize CPI array
        Double[] cpi=new Double[1];
         cpi[0]=0.0;

        // Iterate over each item in the data list
        data.forEach(action -> {
            double index = Double.parseDouble(action.get("indexValue").toString());
            double linkingFactor2016 = Double.parseDouble(action.get("linkingFactor2016").toString());
            double linkingFactor2001 = Double.parseDouble(action.get("linkingFactor2001").toString());
            double linkingFactor1982 = Double.parseDouble(action.get("linkingFactor1982").toString());

            // Calculate daIndex
            double daIndexCalculation = index * linkingFactor2016 * linkingFactor2001 * linkingFactor1982;

           // Round to two decimal places
            BigDecimal bd = new BigDecimal(daIndexCalculation).setScale(2, RoundingMode.HALF_UP);
            daIndexCalculation = bd.doubleValue();
           
            // Update the action map with the formatted daIndex
            action.put("daIndexValue", daIndexCalculation);
            
            cpi[0] += daIndexCalculation;
        });

         cpi[0]= ((cpi[0]/3)-200)/4;
        
        
          BigDecimal bd = new BigDecimal(cpi[0]).setScale(2, RoundingMode.HALF_UP);
          cpi[0] = bd.doubleValue();
         
        // Return results in resultMap
        resultMap.put("status", "success");
        resultMap.put("value", data);
        resultMap.put("avgCPI", cpi[0]);

    } catch (Exception e) {
        // Handle exceptions
        e.printStackTrace();
        resultMap.put("status", "exception");
        logger.info("Problem in getCPICalculation -> DaIndexServiceImpl() :: ", e);
    }

    return resultMap;
}

    @Override
    public Map saveCPICalculation(Map map) {
        
         Map<String, Object> resultMap = new HashMap<>();
        try {
            System.out.println("Payload");
            
            System.out.println(map);
            
            DaIndex daDataToSave = mapper.convertValue(map, DaIndex.class);
            daIndexRepository.save(daDataToSave);
            resultMap.put("status", "success");
            resultMap.put("msg", "Save Successfully");
            
        } catch (Exception e) {
            
        e.printStackTrace();
        resultMap.put("status", "exception");
        logger.info("Problem in getCPICalculation -> DaIndexServiceImpl() :: ", e);
            
        }
         
         
         return resultMap;
    }

    @Override
    public Map getAllDaIndexData(Map map) {
        
          Map<String, Object> resultMap = new HashMap<>();
        try {
            
          List<LinkedCaseInsensitiveMap> allData = daIndexRepository.getAllData(Long.parseLong(map.get("organizationId").toString()));
          resultMap.put("status", "success");
          resultMap.put("value", allData); 
        } catch (Exception e) {
            
        e.printStackTrace();
        resultMap.put("status", "exception");
        logger.info("Problem in getCPICalculation -> DaIndexServiceImpl() :: ", e);
        }
            return resultMap;
    }

    @Override
    public Map getDaIndexById(Map map) {
   
        Map<String, Object> resultMap = new HashMap<>();
        try {
            
            Optional<DaIndex>  allData=  daIndexRepository.findById(Long.parseLong(map.get("id").toString()));
         
            if(allData.isPresent()){
                
                  resultMap.put("status", "success");
                  resultMap.put("value", allData.get()); 
            }
            else{
                  resultMap.put("status", "success");
                  resultMap.put("msg", "Data Not Found"); 
            }
        
        } catch (Exception e) {
            
        e.printStackTrace();
        resultMap.put("status", "exception");
        logger.info("Problem in getCPICalculation -> DaIndexServiceImpl() :: ", e);
        }
            return resultMap;
    
    }

    
}
