/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.repository.EmployeeNetPayRepository;
import com.realcoderz.service.EmployeeNetPayService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mayank
 */

@Service
public class EmployeeNetPayServiceImpl implements  EmployeeNetPayService {
    
    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(EmployeeNetPayServiceImpl.class);
    
    @Autowired
    private EmployeeNetPayRepository employeeNetPayRepo;

    @Override
    public Map get(Map map) {
        Map resultMap=new HashMap<>();
        try {
               List<Integer> l=new ArrayList<>();
               List<Integer> year=new ArrayList<>();
               int sum=0;
               int startMonth=Integer.parseInt(map.get("month").toString());
               for(int i=0;i<3;i++){
                   if(startMonth==1){
                       year.add((Integer.parseInt(map.get("year").toString()))-1);
                       startMonth=13;
                   l.add(--startMonth);
                   }else{
                       year.add(Integer.parseInt(map.get("year").toString()));
                   l.add(--startMonth);
                   }
               }
               
               System.out.println("");
                List<String> netPay = employeeNetPayRepo.employeeNetPay(Long.parseLong(map.get("organizationId").toString()), year , l);
                
                System.out.println("net Payable");
                if(netPay != null){
                resultMap.clear();
                resultMap.put("month", l);
                resultMap.put("employeeNetPay", netPay);
                resultMap.put("status", "success");
                }else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowance's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeNetPayServiceImpl -> get() :: ", ex);

        }
        return resultMap;
    }
    
}
