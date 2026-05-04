/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.model.LabourLawDeduction;
import com.realcoderz.repository.LabourLawDeductionRepo;
import com.realcoderz.service.LabourLawDeductionService;
import com.realcoderz.util.CommonExcelData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tause
 */
@Service
public class LabourLawDeductionServiceImpl implements LabourLawDeductionService {

    @Autowired
    private LabourLawDeductionRepo repo;

    @Autowired
    private CommonExcelData commonExcelData;

    @Override
    public Map getAllStatesDeduction() {
        Map response = new HashMap();
        try {
            List<LabourLawDeduction> labourLaw = repo.findAll();
            response.put("data", labourLaw);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "exception");
            response.put("msg", "Exception in getAllStatesDeduction->");
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map save(LabourLawDeduction deduction) {
        Map response = new HashMap();
        try {
            response = this.validateDataBeforeSave(deduction);
            if (response.containsKey("status") && response.get("status").toString() == "success") {
                if (deduction.getLabourLawDeductionId() == null) {
                    deduction.setTotalContribution(deduction.getEmployeeDeduction() + deduction.getEmployerDeduction());
                    repo.save(deduction);
                    response.put("status", "success");
                    response.put("msg", "Deduction added successfully!");
                } else {
                    LabourLawDeduction labour = repo.findById(deduction.getLabourLawDeductionId()).get();
                    labour.setEmployeeDeduction(deduction.getEmployeeDeduction());
                    labour.setEmployerDeduction(deduction.getEmployerDeduction());
                    labour.setPercentageOfSalary(deduction.getPercentageOfSalary());
                    labour.setFrequencyOfDeduction(deduction.getFrequencyOfDeduction());
                    labour.setTotalContribution(labour.getEmployeeDeduction() + labour.getEmployerDeduction());
                    labour.setStart(deduction.getStart());
                    labour.setEnd(deduction.getEnd());
                    repo.save(labour);
                    response.put("status", "success");
                    response.put("msg", "Deduction updated successfully!");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("satus", "exception");
            response.put("msg", "Exception in Deduction added successfully!");

        }
        return response;
    }

    @Override
    public Map getSingleStateData(Long labourLawDataId) {
        Map response = new HashMap();
        try {
            LabourLawDeduction labourLaw = repo.findById(labourLawDataId).get();
            response.put("status", "success");
            response.put("data", labourLaw);

        } catch (Exception e) {
            response.put("satus", "exception");
            response.put("msg", "Exception in Deduction added successfully!");
        }
        return response;
    }

    @Override
    public Map validateDataBeforeSave(LabourLawDeduction deduction) {
        Map response = new HashMap();
        try {
            if (deduction.getEmployeeDeduction() == null) {
                response.put("status", "error");
                response.put("msg", "Employee deduction cannot be null");
            } else if (deduction.getEmployerDeduction() == null) {
                response.put("status", "error");
                response.put("msg", "Employer deduction cannot be null");
            } else if (deduction.getFrequencyOfDeduction() == null) {
                response.put("status", "error");
                response.put("msg", "Frequency of deduction cannot be null");
            } else if (deduction.getStateName() == null) {
                response.put("status", "error");
                response.put("msg", "State cannot be null");
            } else if (deduction.getStart() == null) {
                response.put("status", "error");
                response.put("msg", "Frequency of deduction cannot be null");
            } else if (deduction.getEnd() == null) {
                response.put("status", "error");
                response.put("msg", "Frequency of deduction cannot be null");
            } else {
                response.put("status", "success");
            }

        } catch (Exception e) {

        }
        return response;
    }

    @Override
    public ResponseEntity<byte[]> getLwfReport(Long organizationId, String month) {
        try {
            // Check if payroll is run
            List<LinkedCaseInsensitiveMap> lwfData = repo.getLwfReport(organizationId);
            System.out.println("lwfData ------------- " + lwfData);
            if (lwfData.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Run Payroll Not Done").getBytes());
            }

            // Convert date string (2025-05-31) to month format (May-25)
            String formattedMonth = formatMonthFromDate(month);
            String mainHeader = "LWF Report " + formattedMonth;

            String[] headerData = {"SR No", "EMP_ID", "Employee Name", "Employee Amount", "Employer Amount", "Total"};
            String[] rowData = {"employeeCode", "employeeName", "employeeDeduction", "employerDeduction", "totalContribution"};

            // Generate Excel with main header
            return commonExcelData.excelDataWithHeader(mainHeader, lwfData, headerData, rowData, "LWF_Report", "LWF_Report", true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }

// Using Java 8 Time API (recommended)
    private String formatMonthFromDate(String dateString) {
        try {
            // Parse the input date (e.g., "2025-05-31")
            LocalDate date = LocalDate.parse(dateString);

            // Format to month-year (e.g., "May-25")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yy", Locale.ENGLISH);
            return date.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: return current month
            return LocalDate.now().format(DateTimeFormatter.ofPattern("MMM-yy"));
        }
    }

}
