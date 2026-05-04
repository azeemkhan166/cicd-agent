/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.realcoderz.model.AccountDetails;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Lalit Raghav
 */
@Service
public interface EmployeeService {

    public Map findAccountDetailsById(String data);

    public Map findAll(HttpServletRequest request, String data);

    public Map save(Map map);

    public Map filterID();

    public Map fetchBankDetailsByEmpId(String data);

    public Map saveBUlkBankDetails(Map map);

    public Map getEPFReport(String map, HttpServletRequest request);

    public Map getESICReport(String map, HttpServletRequest request);

    public Map getIncomTaxReport(String map, HttpServletRequest request);

    public Map getIncomTaxReportforquarter(String map, HttpServletRequest request);

    public Map getEmployess(String map, HttpServletRequest request, Integer pageNumber, Integer pageSize, String searchWord);

    public Map getEmployessWithSite(String map, HttpServletRequest request, Integer pageNumber, Integer pageSize, String searchWord);

    public Map getGeneratedPDfOfEmployee(String map, HttpServletRequest request);

    public Map getStandardDataOfEmployee(String map, HttpServletRequest request);

    public Map getStandardDataOfEmployeeForWorker(String map, HttpServletRequest request);

    public Map getStandardDataOfEmployeeForConsultant(String map, HttpServletRequest request);

    public Map getStandardDataOfEmployeeForIntern(String map, HttpServletRequest request);

    public Map getAllAppraisalList(String map, HttpServletRequest request);

    public Map isRunPayrollDoneInMonth(String map, HttpServletRequest request);

    public Map updateSalaryHoldFlag(String map, HttpServletRequest request);

    public Map getSalaryHoldEmployee(String map, HttpServletRequest request, String searchWord);

    public Map bulkBankDetailsService(List<AccountDetails> list);

    public Map getFAFDetails(String map, HttpServletRequest request);

    public Map holdSalaryOfEmployee(String map, HttpServletRequest request);

    public Map updateAllowanceFAF(String map, HttpServletRequest request);

    public Map saveFAFDetails(String map, HttpServletRequest request);

    public Map getStandardOfEmployeeWhenUpdate(Map map, HttpServletRequest request);

    public Map updatestandardOfEmployee(String map, HttpServletRequest request);

    public Map getStandardOfWorkerWhenUpdate(Map map, HttpServletRequest request);

    public Map redirectFunctionOfStandard(String data, HttpServletRequest request);

    //public Map getStanadardOfInternForUpdate(Map map,HttpServletRequest request);
//    public Map getSalarySheetReport(String map,HttpServletRequest request);
    public Map getAnnextureOfFulltimeEmployee(String map, HttpServletRequest request);

    public Map getAnnextureOfFulltimeEmployeeOnCTC(String map, HttpServletRequest request);

    public Map getAnnextureOfConsultantEmployee(String map, HttpServletRequest request);

    public Map updateEmployeePersonalDetails(List<Map> employeeData);

    public Map getLastRunPayrollOfEmployee(String map, HttpServletRequest request);

    public ResponseEntity<byte[]> downloadSalaryStandard(Long organizationId, String employeeType);

    public ResponseEntity<byte[]> downloadesicInexcelFormate(Long organizationId, Long month, Long year, HttpServletRequest request);

    public Double calculateEmployeeProfessionalTax(String gender, Double grossSalary, int month, int year, String stateName);

    public Map getStandardDataOfEmployeeForConsultantWhenUpdate(String data, HttpServletRequest request);

    public Map checkPreviousEmployeeType(String employeeType, Map map);

    public Map changeStandardFromUi(Map map, HttpServletRequest request);

    public Map getSavedEmployeeStandard(Map map, HttpServletRequest request);

    public Map recalculateEmployeeStandard(String data, HttpServletRequest request);

    public Map getSavedEmployeeStandardOfWorker(Map map, HttpServletRequest request);

    public ResponseEntity<byte[]> getSalarySheetReport(Long organizationId, String empType, Long month, Long year);

    public ResponseEntity<byte[]> getSalarySheetReportForConsultant(Long organizationId, String empType, Long month, Long year);

    public Map getStandardOnTheBasicOfBasicSalary(String map, HttpServletRequest request);

    //public Map getEpfReportNew(String map,HttpServletRequest request);
    public Map getBonusData(String map, HttpServletRequest request);

    public Map saveBonusData(String map, HttpServletRequest request);

    public Map getAllStandardOfEmployee(String map, HttpServletRequest request);

    public Map getStandardById(String map, HttpServletRequest request);

    public Map getEmployeeDataFromRunPayRoll(String data);
    
    public ResponseEntity<byte[]> individualEmployeeMonthWiseSalaryReport(Long organizationId, String fromDate, String toDate, Long employeeId);

    public ResponseEntity<byte[]> getBonusRegister(Long organizationId, String fromDate, String toDate);

    public Double calculateEmployeeLWF(String stateName, Double grossSalary, String empType, int month);

    public ResponseEntity<byte[]> getErForm(Long organizationId, String fromDate, String toDate);

    public ResponseEntity<byte[]> getPtForm(Long organizationId, String fromDate, String toDate);

    public ResponseEntity<byte[]> getPtReport(Long organizationId, String monthYear);

    public ResponseEntity<byte[]> locationAndDepartmentWiseReport(Long organizationId, String fromDate, String toDate, String location);

    public ResponseEntity<byte[]> downloadEmployeeList(Long organizationId, Long sId, String siteName, HttpServletRequest request);

    public ResponseEntity<byte[]> downloadPlanName(Long organizationId, Long sId, String siteName, HttpServletRequest request);
   
    public ResponseEntity<byte[]> getCustomSalarySheetReport(Long organizationId, String empType, Long month, Long year);
   
    public Map getEPFReportForVedant(String map, HttpServletRequest request);

    public Map getCustomESICReport(String map, HttpServletRequest request);
    
    public ResponseEntity<byte[]> CustomDownloadesicInexcelFormate(Long organizationId, Long month, Long year,String siteId, HttpServletRequest request);

    public Map getPtReport(String map, HttpServletRequest request);
    
    public ResponseEntity<byte[]> getBonusRegisterForVedant(Long organizationId, String fromDate, String toDate);

    public ResponseEntity<byte[]> DownloadRunPayrollSheet(Long organizationId, String sId, String siteName , String netPayable , String date , String year, HttpServletRequest request);

    public ResponseEntity<byte[]> getFAFForVedant(Long organizationId, String year, HttpServletRequest request);

    public Map getRunPayRollSheetData(String data);

}
