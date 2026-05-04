/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import com.realcoderz.model.RunPayRoll;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author bipulsingh
 */
public interface RunPayService {

    public Map saveAll(Map map);

    public Map updateRunPayRoll(Map map);

    public RunPayRoll findDataByMonthYearAndEmpId(Integer month, Integer year, Long empId);

    public Map getPreviousPayRunData(Map map);

    public Map getPayRunData(String data, HttpServletRequest request,String search);
    
    public Map getCustomPayRunData(String data, HttpServletRequest request,String search);
    
    public Map isPayrollSaved(Integer month, Integer year, Long organizationId);
    
    public Map getAllowanceAmount(Integer month, Integer year, Long organizationId, Long employeeId);
    
    public Map isSalaryBreakupSavedOfThisMonth(Map map);
    
    public Map getSavedRunPayroll(Map map);
    
    public Map deleteRunPayroll(Map map);
       
}
