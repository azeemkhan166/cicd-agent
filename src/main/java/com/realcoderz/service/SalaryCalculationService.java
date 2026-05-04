/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.realcoderz.service;

 

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.LinkedCaseInsensitiveMap;

 

/**
*
* @author Lalit raghav
*/
public interface SalaryCalculationService {

 

   public Map calculateyearWorkDay(String data,HttpServletRequest request);

 

    public Map saveAllTax(Map map);

 

    public Map updateAllowanceTax(Map map);

    
    public Map updateTds(Map map);
    
//    public Map calculateTaxWithBulkEmployee(Map map, HttpServletRequest request);
    
    public Map calculateyearWorkDayPreviousVersion(String data, HttpServletRequest request);
    
      public Map createPdfDataPreviousVersion(LinkedCaseInsensitiveMap allowances, Map exempData, LinkedCaseInsensitiveMap deductions,LinkedCaseInsensitiveMap allowanceForPriviousAndCurrentMonth);
    
     public Map updateAllowanceTaxNew(Map map);
   

}