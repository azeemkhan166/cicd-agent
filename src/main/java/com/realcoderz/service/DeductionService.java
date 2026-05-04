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
public interface DeductionService {

    public Map save(Map map);

    public Map fetch(Long org_id, HttpServletRequest request,String search);

    public Map delete(Long id, String employee_type);

    public Map findById(Long id);

    public Map fetchbyDeductionName(Long id);

    public Map fetchApprovedDeductions(Long org_id, Integer month, Integer year, String employee_type);

    public Map isAlreadyExist(String name, Long id, String employee_type);

    public Map getDeductionForSuperAdmin();

    public Map getDeductionNameForOrganization(Long id);

    public Map checkDeductionType(String name);
    
    public Map isDeductionExistForSuperAdmin(String name);
    
    public Map approvedRejectDeducion(Long id,String status);

}
