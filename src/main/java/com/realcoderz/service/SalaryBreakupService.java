package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lalit Raghav edited By Astha
 */
public interface SalaryBreakupService {

    public Map save(Map map);

    //  ---------- update -----------------
    public Map findSalaryDetailsById(Long id);

    //  -----get ---------
    public Map calculateSalaryData(String map, HttpServletRequest request);
    
    public Map SalaryBreakUporConsultant(String map, HttpServletRequest request);
    
    public Map SalaryBreakUporIntern(String map, HttpServletRequest request);

    public Map calculateSalaryDataInPDF(String data);
    
    public Map calSalaryDataInPdfForConsultant(String data);
    
    public Map calSalaryDataInPdfForIntern(String data);

    public Map calculateEPF(String res, Long org_id, Double basic,Double payableBasic, Double payableDeduction, Double netAmount, Double gross, String data, HttpServletRequest request,Integer month,Integer year, Double working_day, Double total_days);

    public Map getEmployeeCurrentDeatils(String data);

    public Map getGrossSalary(String data, HttpServletRequest request);

    public Map savePdf(MultipartFile fileStream, int empId, int month, int year, Long orgId);

    public Map financialYearDropdown(String date);

    public Map financialMonthDropdown(String date, String financialYear);

    public Map getPdfurl(String data);
    
    public Map fetchOrganizationEpfStatus(Long organization_id);
    
    public Map fetchOrganizationEsicStatus(Long organization_id);
    
    public Map calculateBifurcationOnGross(Map map);
    
    public Map calculateSalaryDataPreviousVersion(String data, HttpServletRequest request);
    
     public Map isSalaryBreakUpSavedPreviousVersion(String month, String year, String empId, String orgId, String email, String employee_Type);
      public Map getWorkingDayPreviousVersion(Map map, HttpEntity leaveEntity);
      
       public Map calculateSalaryDataNew(String data, HttpServletRequest request);
       
     public Map calculationBasedOnWorkingDayNew(Double working_day, Long days, Map salarybreakupData, Map map);
    
    public Map getGrossSalaryOfEmployee(String data, HttpServletRequest request);
}
