/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Site;
import com.realcoderz.model.Skilled;
import com.realcoderz.repository.SkilledRepository;
import com.realcoderz.service.SkilledService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class SkilledServiceImpl implements SkilledService {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SkilledRepository skilledRepository;

    @Override
    public Map save(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Skilled siteData = mapper.convertValue(map, Skilled.class);

            skilledRepository.save(siteData);
            resultMap.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
    
        @Override
    public Map getAllSkilled(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            List<Skilled> data = skilledRepository.findSkilledById(ids);
            resultMap.put("status", "success");
            resultMap.put("value", data);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }
    
    
      @Override
    public Map findById(Map map) {
        
        
    Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("id").toString());
            Optional<Skilled> data = skilledRepository.findById(ids);
            resultMap.put("status", "success");
            if(data.isPresent()){
                resultMap.put("value", data.get());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap; }
    
       @Override
    public Map deleteById(Map map) {
        
            Map resultMap = new HashMap<>();
        try {

            Long ids = Long.parseLong(map.get("id").toString());
            skilledRepository.deleteById(ids);
            resultMap.put("status", "success");
            resultMap.put("msg", "Data Deleted Success");
           
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
     }
    
    
}
