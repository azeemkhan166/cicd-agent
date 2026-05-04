package com.realcoderz.service;

import java.util.Map;
/**
 *
 * @author Astha
 */
public interface SalaryHistoryRecordService {
    
    public Map saveSalaryHistoryRecord(Map map);
    
    public Map getSalaryHistoryRecord(Map map);
    
    public Map GrossSalaryUpdate(Map map);
    
    public Map saveOrUpdateGrossSalary(Map map);
    
    public Map saveGrossSalaryInEmployee();
    
//    public Map UpdateSalary();
    
}