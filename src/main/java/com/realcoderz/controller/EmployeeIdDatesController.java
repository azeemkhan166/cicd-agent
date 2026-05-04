/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.EmployeeIdDatesService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tause
 */
@RestController
@RequestMapping("/employeeIDDates")
public class EmployeeIdDatesController {
    
    
    @Autowired
    private ObjectMapper mapper ;
    
    @Autowired
    private EmployeeIdDatesService service;
    
    @PostMapping("/extendInvestmentWindow")
    public Map updateEmployeeInvestmentWindow(@RequestBody String data){
        Map response=new HashMap();
         try{
            Map map= mapper.readValue(EncryptDecryptUtils.decrypt(data), Map.class);
            Long organizationId=Long.parseLong(map.get("organizationId").toString());
            Integer fyYear=Integer.parseInt(map.get("fyYear").toString());
            String startDate= map.get("startDate").toString();
            String endDate=map.get("endDate").toString();
            Long employeeId=Long.parseLong(map.get("employeeId").toString());
            response= service.updateEmployeeInvestmentDates(employeeId, fyYear, organizationId, startDate, endDate);
             
         }catch(Exception e){
            e.printStackTrace();
            response.put("status","Exception");
             response.put("msg","Exception in updateEmployeeInvestmentWindow=>"+e.getMessage());
            
         }
         return response;
    }
    
    @PostMapping("/getRequestedInvestmentWindow")
    public Map getRequestedInvestmentWindow(@RequestBody String data){
        Map response=new HashMap();
        try{
          Map map= mapper.readValue(EncryptDecryptUtils.decrypt(data), Map.class);    
          Long organizationId=Long.parseLong(map.get("organizationId").toString());
          Integer fyYear=Integer.parseInt(map.get("fyYear").toString()); 
          response=service.getRequestedInvestmentWindowDates(organizationId, fyYear);
        }catch(Exception e){
          e.printStackTrace();
          response.put("status","Exception");
          response.put("msg","Exception in updateEmployeeInvestmentWindow=>"+e.getMessage());  
        }
        return response;
    }
    
     @PostMapping("/updateRequestedInvestmentWindow")
    public Map updateRequestedInvestmentWindow(@RequestBody String data){
        Map response=new HashMap();
        try{
          Map map= mapper.readValue(EncryptDecryptUtils.decrypt(data), Map.class);    
          Long rowId=Long.parseLong(map.get("rowId").toString());
          String status=map.get("status").toString(); 
          response=service.updateEmployeeWindowRequestWindow(rowId, status);
        }catch(Exception e){
          e.printStackTrace();
          response.put("status","Exception");
          response.put("msg","Exception in updateEmployeeInvestmentWindow=>"+e.getMessage());  
        }
        return response;
    }
    
}
