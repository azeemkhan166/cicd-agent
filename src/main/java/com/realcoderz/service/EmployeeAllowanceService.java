/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Mayank
 */
public interface EmployeeAllowanceService {

    public Map getEmployeeAllowance(Map map);

    public Map saveEmployeeAllowance(Map map, HttpServletRequest request,String data);
    
    public Map savePayrollData(Map map, HttpServletRequest request,String data);
  
    public Map findByEmployeeId(Map map);
    
    public Map saveStandardValueOfEmployee(Map map, HttpServletRequest request,String data);
    
//    public Map gettingAllowances(Long salaryBreaupId);
    
    public Map gettingAllowances(Long employeeId,Long organizationId,Integer year,Integer month,Long salaryBreaupId);
    
    public Map updateEmployeeAllowanceAndDeductions(Map map);
    
    public Map updateTds(Map map);
    
    public Map updateemployeedetails(Map map);
    
    public Map form16Details(Map map,HttpServletRequest request);
    
    public Map updateForm16(Map map,HttpServletRequest request);
    
    public Map uploadForm16Document(MultipartFile file, String fileName, Long employeeId, Long organizationId, String financialYear);
    
    public Map viewForm16Document(Map map);
}