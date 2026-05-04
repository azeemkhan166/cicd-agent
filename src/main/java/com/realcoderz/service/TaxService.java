/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import com.realcoderz.model.Tax;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
public interface TaxService {

    public Map getTax(Map map);

    public Map saveTax(Map map);
    
    public Map getTaxPreviousVersion(Map map);
    
     public Map saveTaxInBulk(List<Tax> incometax);
       
    public Map calcuteTaxInBulk(List<LinkedHashMap> list,int month,int year,Long orgnizationId);
    
    public List<LinkedCaseInsensitiveMap> investmentDeclarationCalculation(int year,Long organizationId,List<Long> allEmployees);
    
    public List<Map> sortListOfTaxAccordingToPaySlip(List<Map> taxList, Long organizationId, int month,int year);
    
    public Map getTaxSlip(Long employeeId,int month,int year);
    
    public Map calculateTaxWhileUpdatingAllowance(Double previosAllowance,Double updatedAllowance,List<Map> employeeAllowancesUi,Long employeeId,int month,int year,Double previousExpense);
    
    public Map updateTds(Long employeeId,int month,int year,Double taxForThisMonth);
    
    public Map calculateTaxWhileComparingTax(Map map);
    
    public Map alreadyPiadAllowances(Long employeeId,int startYer,int endYear);
    
    public Map employeeCurrentAndFutureAllowance(Long employeeId,int startYer,int endYear);
    
    public Map employeeTotalInvestemt(Map map);
    
    public Map calucluteCommonTax(Double taxableIncome,String taxslabType,int startYear );
    
    public Map getClosingAllowances(List<LinkedCaseInsensitiveMap> organizationStandard,int month,int year);

}
