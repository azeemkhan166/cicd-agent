/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.service.OtherSectionService;
import com.realcoderz.util.EncryptDecryptUtils;
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
 */
@Service
public class OtherSectionServiceImpl implements OtherSectionService {
      static final Logger LOGGER = LoggerFactory.getLogger(OtherSectionServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();
    
   @Autowired
   private OtherSectionRepository otherSectionrepo;

    

    @Override
    public Map findOtherSectionById(String data) {
         Map resultMap = new HashMap();
        try {
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           LinkedCaseInsensitiveMap otherSection= otherSectionrepo.findOtherSectionById(Long.parseLong(map.get("employeeId").toString()));
          
            if (otherSection != null) {
                resultMap.put("status", "success");
                resultMap.put("list", otherSection);

            } else {
                resultMap.clear();
                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in OtherSectionServiceImpl -> save() :: ", ex);
        }
        return resultMap;
    }

    
    
    
    
}
