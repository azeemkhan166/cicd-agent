/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.auditable.BearerTokenUtil;
import static com.realcoderz.controller.NewTaxRegimeSlabController.logger;
import com.realcoderz.model.OrganizationIdDates;
import com.realcoderz.serviceImpl.OrganizationIdDatesServiceImpl;

import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tauseef
 */
@RestController
@RequestMapping(path="/IDDates")
public class OrganizationIdDatesController {
    
     ObjectMapper mapper=new ObjectMapper();
     
     @Autowired
    private OrganizationIdDatesServiceImpl service;
    
     @PostMapping("/saveIDDates")
     public Map saveIdDates(@RequestBody String data,HttpServletRequest request)
    {
        Map resultMap=new HashMap();
        try
        {      
            String token = BearerTokenUtil.getBearerTokenHeader();
            System.out.println("token 45 "+token);
           OrganizationIdDates org=mapper.readValue(EncryptDecryptUtils.decrypt(data), OrganizationIdDates.class);
           resultMap= service.saveOrganizationIdDeclerationDates(org,token);
        }
        catch(Exception ex)
        {
             resultMap.put("status","exception");
             ex.printStackTrace();
             logger.info("Problem in NewTaxRegimeSlabController -> saveNewTaxRegime() :: ", ex);
        }  
        return resultMap;   
    }
     
     @PostMapping("/getIDDates")
     public Map getOrganizationInvestmentDates(@RequestBody String data){
         Map response=new HashMap();
          try{
              Map map= mapper.readValue(EncryptDecryptUtils.decrypt(data), Map.class);
              Long organizationId=Long.parseLong(map.get("organizationId").toString());
              Integer fyYear=Integer.parseInt(map.get("fyYear").toString());
              response=service.getOrganizationFyYearData(fyYear,organizationId);
          }catch(Exception e){
             response.put("status","exception");
             e.printStackTrace();
             logger.info("Problem in NewTaxRegimeSlabController -> saveNewTaxRegime() :: ", e);
          }
          return response;
     }
    
}
