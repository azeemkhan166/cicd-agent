/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.model.AppraisalSalary;
import com.realcoderz.repository.AppraisalSalaryRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.service.AppraisalSalaryService;
import com.realcoderz.util.CommonExcelData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class AppraisalSalaryServiceImpl implements AppraisalSalaryService{

    private final AppraisalSalaryRepository appraisalSalaryRepository;
    private final SalaryBreakuprepo SalaryBreakuprepo;
    private final CommonExcelData commonExcelData;

    @Override
    public Map save(AppraisalSalary map) {
        
         Map resultMap = new HashMap<>();
        try {
            System.out.println("AppraisalSalary Save method "+map.toString());
            LinkedCaseInsensitiveMap previousGrossSalary= SalaryBreakuprepo.getGrossSalaryOFEmployee(map.getEmployeeId());
            
             if(previousGrossSalary ==null || previousGrossSalary.get("gross_salary") ==null || previousGrossSalary.get("annual_ctc") ==null){
                resultMap.put("status", "error");
                resultMap.put("msg", "Please Save your Standard");
                return resultMap;
            }
            
            if(previousGrossSalary !=null && previousGrossSalary.get("gross_salary") !=null){
                Double pGrossSalary=Double.parseDouble(previousGrossSalary.get("gross_salary").toString());
                Double pAnnualSalary=Double.parseDouble(previousGrossSalary.get("annual_ctc").toString());
               
                map.setPreviousAnnualCtc(pAnnualSalary);
                map.setPreviousMonthlyGross(pGrossSalary);
            }
            
            Double monthlyGross1 = ((double) Math.round((map.getRevisedAnnualCtc() - map.getVariablePart()) / 12.0) -map.getEmployerCost());
            map.setMonthlyGross(monthlyGross1);
            
            Double incrementAmount=map.getRevisedAnnualCtc()-map.getPreviousAnnualCtc();
            
            if(map.getPreviousAnnualCtc()<=0){
                resultMap.put("status", "error");
                resultMap.put("msg", "Please Add CTC in Amount in Standard");
                return resultMap;
            }
            Double incrementPercentage = (incrementAmount / map.getPreviousAnnualCtc()) * 100;

            map.setIncrementAmount(incrementAmount);
            map.setIncrementPercent(incrementPercentage);
            appraisalSalaryRepository.save(map);
            resultMap.put("status", "success");
            resultMap.put("msg", "Data Save successFully");
           
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", "exception occure during save the appraisal");
           
        }
        return resultMap;
        
    }

    @Override
    public Map getAppraisalData(LinkedCaseInsensitiveMap map) {
   
         Map resultMap = new HashMap<>();
        try {
           List<LinkedCaseInsensitiveMap> value= appraisalSalaryRepository.getAllData(Long.parseLong(map.get("organizationId").toString()));
            resultMap.put("status", "success");
            resultMap.put("value", value);
           
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", "exception occure in getAppraisalData");
           
        }
        return resultMap;
    }
    
    
    
     @Override
    public ResponseEntity<byte[]> downloadAppraisalReport(Long organizationId, HttpServletRequest request) {

        try {
            List<LinkedCaseInsensitiveMap> resultList = new ArrayList<>();
            
            String[] combinedHeaderArray = {"S.No","Name", "Employee Code", "Date (Letter issue date)", "Prev Annual CTC","Prev Monthly Gross","Effective Date", "Revised Annual CTC", "Variable","Employer Cost","Monthly Grosss","Increment Amount","Increment (%)"};

            String[] combinedRowArray = {"name", "employee_code", "created_date","previous_annual_ctc","previous_monthly_gross", "effective_date", "revised_annual_ctc", "variable_part","employer_cost","monthly_gross","increment_amount","increment_percent"};

            List<LinkedCaseInsensitiveMap> value= appraisalSalaryRepository.getAllData(organizationId);
          
            resultList.removeIf(Map::isEmpty);

            return commonExcelData.excelData(value, combinedHeaderArray, combinedRowArray, "Appraisal-report", "AppraisalReport");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }

    }
    
}
