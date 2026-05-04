/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.NewTaxRegimeSlab;
import com.realcoderz.repository.NewTaxRegimeSlabRepository;
import com.realcoderz.service.NewTaxRegimeSlabService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lalit Raghav
 */
@Service
public class NewTaxRegimeSlabServiceImpl implements NewTaxRegimeSlabService {
   
    
    ObjectMapper mapper=new ObjectMapper();
    
     static final Logger logger = LoggerFactory.getLogger(NewTaxRegimeSlabServiceImpl.class);
    
    @Autowired
     private NewTaxRegimeSlabRepository newTaxRegimeRepo;
    
    @Override
    public Map saveNewTaxSlab(Map map) {
        
        Map resultMap=new HashMap();
        
        try
        {
            NewTaxRegimeSlab newTaxSlab=mapper.convertValue(map,NewTaxRegimeSlab.class);
            
            if(newTaxSlab!=null)
            {
                newTaxRegimeRepo.save(newTaxSlab);
                resultMap.put("status", "success");
            }
            
            else
            {
                resultMap.put("status","error");
            }
          
        }
        catch(Exception ex)
        {
            resultMap.put("status","exception");
        }
        return resultMap;
    }

    @Override
    public Map fetchNewTaxSlab(Map map) {
        Map resultMap=new HashMap();
        
       try {
            List<NewTaxRegimeSlab> taxNewSlab = newTaxRegimeRepo.findNewTaxSlab();
            if (taxNewSlab != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", taxNewSlab);
//                System.out.println(taxSlab);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
            resultMap.put("msg", "taxNewSlab is not available..!");
            }
        }
        catch(Exception ex)
        {
            resultMap.put("status","exception"); 
        }
        return resultMap;
    }
    
   

    @Override
    public Map saveAllNewTaxSlabs(Map map) {
         Map resultMap = new HashMap<>();
         try {
     if(map.containsKey("list")){
         List<LinkedHashMap> dataList = (List<LinkedHashMap>) map.get("list");
         System.out.println("slab"+dataList);
         if(!dataList.isEmpty()){
//             List<TaxSlab> taxslabList = taxSlabRepo.findTaxSlab(Long.parseLong(dataList.get(0).get("organization_id").toString())); 
         List<NewTaxRegimeSlab> list = new ArrayList<>();
         
               dataList.stream().forEach(data -> {
                NewTaxRegimeSlab newallTaxSlab = mapper.convertValue(data, NewTaxRegimeSlab.class);
                 list.add(newallTaxSlab);
                 
                 });
                    
                 newTaxRegimeRepo.saveAll(list);
                 resultMap.clear();
                 resultMap.put("status", "success");
//                 resultMap.put("list", taxSlabRepo.saveAll(list));
             } else {
                 resultMap.clear();
                 resultMap.put("status", "error");
                 resultMap.put("msg", "Error while saving tax slab..!!");
             }}
         } catch (Exception ex) {
             resultMap.clear();
             resultMap.put("status", "exception");
//             logger.info("Problem in TaxSlabServiceImpl -> save() :: ", ex);

        }
           return resultMap;
    }

    @Override
    public Map getAllNewTaxSlabs() {
       
        Map resultMap = new HashMap<>();
        try {
            List<NewTaxRegimeSlab> taxSlab = newTaxRegimeRepo.newfindTaxSlab();
            if (taxSlab != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", taxSlab);
//                System.out.println(taxSlab);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "newtaxSlab is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in NewTaxRegimeSlabServiceImpl -> getAllNewTaxSlabs() :: ", ex);

        }
        
        return resultMap;
    }
    
    
    
}
