/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.ProofOfInvestmentController.logger;
import com.realcoderz.service.EmployeeAllowanceService;
import com.realcoderz.serviceImpl.EmployeeAllowanceServiceImpl;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping(path = "/employeeAllowance")
public class EmployeeAllowanceController {

    static final Logger logger = LoggerFactory.getLogger(SalaryBreakupController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EmployeeAllowanceService allowanceService;
    
    @Autowired
    private EmployeeAllowanceServiceImpl employeeAllowanceServiceImpl;

    @PostMapping(path = "/getAllEmployeeAllowances")
    public Map getAllowance(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getEmployeeAllowance(map);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> getAllowance() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        System.out.println(resultMap);
        return resultMap;

    }

    @PostMapping("/save")
    public Map saveAllowances(@RequestBody String data, HttpServletRequest request) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.saveEmployeeAllowance(map, request, data);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> saveAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/savePayrollData")
    public Map savePayrollData(@RequestBody String data, HttpServletRequest request) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.savePayrollData(map, request, data);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> saveAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/findByEmpId")
    public Map findById(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.findByEmployeeId(map);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> findById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/savestandardvalueofemployee")
    public Map saveStandardValueOfEmployee(@RequestBody String data, HttpServletRequest request) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.saveStandardValueOfEmployee(map, request, data);
        } catch (Exception ex) {
           
            logger.info("Problem in EmployeeAllowanceController -> saveStandardValueOfEmployee() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
            ex.printStackTrace();
        }
        return resultMap;
    }
    
     @PostMapping("/update")
    public Map updateAllowances(@RequestBody String data, HttpServletRequest request) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.saveEmployeeAllowance(map, request, data);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeAllowanceController -> saveAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/getAllEmployeeAllowancesAndDeductions")
    public Map getAllEmployeeAllowancesAndDeductions(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {  
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            Long employeeId=map.get("employeeId")!=null?Long.parseLong(map.get("employeeId").toString()):0;
            Long organizationId=map.get("organizationId")!=null?Long.parseLong(map.get("organizationId").toString()):0;
            Integer year=map.get("year")!=null?Integer.parseInt(map.get("year").toString()):0;
            Integer month=map.get("month")!=null?Integer.parseInt(map.get("month").toString()):0;
            Long salaryBreaupId=map.get("salaryBreakUpId")!=null?Long.parseLong(map.get("salaryBreakUpId").toString()):0;
//            resultMap = allowanceService.gettingAllowances(salaryBreaupId);
            resultMap = allowanceService.gettingAllowances(employeeId,organizationId,year,month,salaryBreaupId);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in EmployeeAllowanceController -> getAllEmployeeAllowancesAndDeductions() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/updateEmployeeAllowanceAndDeductions")
    public Map updateEmployeeAllowanceAndDeductions(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {  
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           
            resultMap = allowanceService.updateEmployeeAllowanceAndDeductions(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in EmployeeAllowanceController -> updateEmployeeAllowanceAndDeductions() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/updateTds")
    public Map updateTds(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {  
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);          
            resultMap = allowanceService.updateTds(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in EmployeeAllowanceController -> updateEmployeeAllowanceAndDeductions() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
        @PostMapping("/updateemployeedetails")
    public Map updateemployeedetails(@RequestBody String data, HttpServletRequest request) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.updateemployeedetails(map);
        } catch (Exception ex) {
           
            logger.info("Problem in EmployeeAllowanceController -> updateemployeedetails() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
            ex.printStackTrace();
        }
        return resultMap;
    }
    @PostMapping("/getForm16Details")
    public Map form16Details(@RequestBody String data,HttpServletRequest request){
      Map resultMap =new HashMap<>();
        try {
            
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.form16Details(map,request);
            
        } catch (Exception e) {
            
            logger.info("Problem in EmployeeAllowanceController -> form16Details :: ", e);
            resultMap.clear();
            resultMap.put("status", "exception");
            e.printStackTrace();
        }
      
      return resultMap;
    }
    
     @PostMapping("/updateform16")
    public Map updateForm16(@RequestBody String data,HttpServletRequest request){
     
        Map resultMap =new HashMap<>();
      
        try {
            
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.updateForm16(map,request);
            
        } catch (Exception e) {
            
            logger.info("Problem in EmployeeAllowanceController -> updateForm16 :: ", e);
            resultMap.clear();
            resultMap.put("status", "exception");
            e.printStackTrace();
        }
      
      return resultMap;
    }
   
    @PostMapping("/uploadform16document")
    public Map uploadForm16Document(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
           @RequestParam("employee_id") Long employeeId, @RequestParam("organization_id") Long organizationId, @RequestParam("financialYear") String financialYear) {
        Map resultMap = new HashMap<>();
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            resultMap.put("status", "error");
            resultMap.put("msg", "Only PDF files are allowed");
            return resultMap;
        }
        try {
            resultMap = allowanceService.uploadForm16Document(file, fileName, employeeId, organizationId, financialYear);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeAllowanceController -> uploadForm16Document() :: ", ex);
        }

        return resultMap;
    }
    
    @PostMapping("/viewForm16Document")
    public Map viewForm16Document(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.viewForm16Document(map);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }
    
    
    @PostMapping("/updateallowanceinbulk")
    public Map<String, Object> updateAllowanceInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.ms-excel")
                && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("msg", "Only Excel files (.xls, .xlsx) are allowed");
            return error;
        }
        return employeeAllowanceServiceImpl.updateAllowanceInBulk(file, orgId);
    }

    @PostMapping("/customupdateallowanceinbulk")
    public Map<String, Object> CustomUpdateAllowanceInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId,@RequestParam("siteId") Long siteId) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.ms-excel")
                && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("msg", "Only Excel files (.xls, .xlsx) are allowed");
            return error;
        }
        return employeeAllowanceServiceImpl.CustomUpdateAllowanceInBulk(file, orgId,siteId);
    }

}
