/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AccountDetails;
import com.realcoderz.model.RentAmount;
import com.realcoderz.repository.RentAmountRepository;
import com.realcoderz.service.RentAmountService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Service
public class RentAmountServiceImpl implements RentAmountService{
  ObjectMapper mapper = new ObjectMapper();
    @Autowired
   public RentAmountRepository rentamountrepo;
//    
    
    @Override
    public Map findRentById(String data) {
    
       Map resultMap =new HashMap<>();
       try
       {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           LinkedCaseInsensitiveMap rentAmount= rentamountrepo.findRentAmountById(Long.parseLong(map.get("employeeId").toString()));
               resultMap.put("status","success");
 
       }
       catch(Exception ex)
       {
           resultMap.clear();
           resultMap.put("ststus","exception");
       }
       
       
       return resultMap;
    }
    
    
}
