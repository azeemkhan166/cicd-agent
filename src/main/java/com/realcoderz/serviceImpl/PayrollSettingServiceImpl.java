/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.PayrollSetting;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.repository.PayrollSettingRepository;
import com.realcoderz.service.PayrollSettingService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha
 */
@Service
public class PayrollSettingServiceImpl implements PayrollSettingService {

     static final Logger logger = LoggerFactory.getLogger(PayrollSettingServiceImpl.class);
     
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PayrollSettingRepository payrollRepo;
    
    @Autowired
    private OrganizationSetUpRepository orgRepo;
    
    //saving the payroll setting 
    @Override
    public Map save(Map map) {
         Map resultMap = new HashMap();
        PayrollSetting payroll = mapper.convertValue(map, PayrollSetting.class);
        try{
            if (payroll != null) {
            payrollRepo.save(payroll);
            resultMap.put("status", "success");
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "PayrollSetting is not saving");
        }}catch(Exception ex){
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PayrollSettingServiceImpl -> save() :: ", ex);
          }
        return resultMap;   
    }

    //fetching payroll setting 
    @Override
    public Map fetch(Long id) {
        Map resultMap = new HashMap();
        List<PayrollSetting> pay = payrollRepo.getPayrollSetting(id);
        List<LinkedCaseInsensitiveMap> org=orgRepo.getOrganizationData(id);
        try{
        if (!pay.isEmpty()) {
            resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", pay);
            resultMap.put("orgData",org);
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "PayrollSetting list is not exit");
        }}catch(Exception ex){
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PayrollSettingServiceImpl -> fetch() :: ", ex);
          }
        return resultMap;
    }
    
    //finding by thier Id
    @Override
    public Map findById(Long id) {
        Map resultMap = new HashMap();
        try{
            Optional<PayrollSetting> payroll = payrollRepo.findById(id);
            if(payroll.isPresent()){
                resultMap.clear();
                resultMap.put("list",payroll.get());
               resultMap.put("status","success");
            }else{
                resultMap.clear();
                 resultMap.put("msg","payroll not found");
               resultMap.put("status","error");
            }
        }catch(Exception ex){
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PayrollSettingServiceImpl -> findById() :: ", ex);
        }
         return resultMap;
    }
    
    

    
}
