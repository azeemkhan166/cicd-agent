/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.PercentageOfBasic;
import com.realcoderz.repository.PercentageOfBasicRepository;
import com.realcoderz.service.PercentageOfBasicService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 * edited By Astha
 */
@Service
public class PercentageOfBasicServiceImpl implements PercentageOfBasicService{

    @Autowired
    public PercentageOfBasicRepository percentageOfBasicRepo; 
    
    static final Logger LOGGER = LoggerFactory.getLogger(PercentageOfBasicServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Map save(Map map) {
        Map resultMap=new HashMap();
        try {
            PercentageOfBasic perOfBasic = mapper.convertValue(map, PercentageOfBasic.class);
            System.out.println(perOfBasic);

            percentageOfBasicRepo.save(perOfBasic);
            resultMap.clear();
            resultMap.put("status", "success");

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "error");
        }

        return resultMap;
    }
    
    @Override
    public Map getPercentageOfBasic() {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap percentageOfBasic = percentageOfBasicRepo.getpercentageOfBasic();
            if (percentageOfBasic != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", percentageOfBasic);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Cess is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in PercentageOfBasicServiceImpl -> get() :: ", ex);

        }
        return resultMap;
    }
    
}
