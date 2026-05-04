/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mayank
 */
public interface AllowanceService {

    public Map save(Map map);

    public Map fetch(Long org_id, HttpServletRequest request,String search);

    public Map delete(Long id);

    public Map findById(Long id);
    
    public Map findAllowanceNameFromAllowanceId(Long allowance_id, Long org_id);

    public Map fetchbyAllowanceName(Long id);

    public Map fetchApprovedAllowances(Long org_id, Integer month, Integer year, String employeeType);

    public Map isAlreadyExist(String name, Long id, String employee_type);
    
    public Map getAllowanceNames(Long orgId,String type);
    
    public Map getAllowanceNameForSuperAdmin(String type);
    
    public Map getAllowanceDataForSuperAdmin();
    
    public Map getParticularAllowanceDataForSuperAdmin(Long id);
    
    public Map isAllowanceExistForSuperAdmin(String name);
    
    public Map getAllowanceNameForOrganization(Long id, String type);
    
    public Map checkAllowanceType(String name);
    
    public Map getWorkerAllowanceName(Long org_id,String employeeType);
    
    public Map approvedRejectAllowance(Long id, String status);
    
    public Map getGroupList(Long org_id);
    
    public Map getGradeList(Long org_id);
    
    public Map getEmployeeByOrgIdAndEmployeeType(Long id, String type);
    
    public Map getGradeOrGroupList(Long id, String type,String employeeType);
        
}
