/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.DeductionLoan;
import com.realcoderz.model.LoanCloser;
import com.realcoderz.repository.DeductionLoanRepository;
import com.realcoderz.repository.LoanCloserRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.DeductionLoanService;
import com.realcoderz.util.CommonExcelData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
public class DeductionLoanServiceImpl implements DeductionLoanService {

    ObjectMapper mapper = new ObjectMapper();

    private final DeductionLoanRepository deductionLoanRepository;
    private final LoanCloserRepository loanCloserRepository;
    private final employeeDetailsRepository employeeDetailsRepository;
    private final CommonExcelData commonExcelData;
      
    @Override
    public Map saveDeductionLoan(Map map) {

        Map resultMap = new HashMap<>();
        try {
            DeductionLoan deductionLoan = mapper.convertValue(map, DeductionLoan.class);

            if (deductionLoan != null) {
                deductionLoanRepository.save(deductionLoan);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Employee Loan");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
        }

        return resultMap;

    }

    @Override
    public Map getAllDeductionLoan(Long org_id,String searchString) {
   
    
        
           Map resultMap = new HashMap<>();
        try {
            
           List<DeductionLoan> getAllDeductionLoanList=deductionLoanRepository.getAllDeductionLoanList(org_id,searchString);
           List<LinkedCaseInsensitiveMap> employeeList = employeeDetailsRepository.getFullTimeEmployeeList(org_id);
           List<LinkedCaseInsensitiveMap> deductionList = deductionLoanRepository.getDeductionList(org_id);
           
            Set<Long> distinctLoanTypeIds = getAllDeductionLoanList.stream()
                    .map(DeductionLoan::getLoanTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            System.out.println("distinctLoanTypeIds ");
            System.out.println(distinctLoanTypeIds);
            
            List<LinkedCaseInsensitiveMap> employeeDeductionList=deductionLoanRepository.getDeductionLoanOfEachEmployee(distinctLoanTypeIds);
            
            System.out.println(getAllDeductionLoanList.toString());
            
            List<LinkedCaseInsensitiveMap> loanCloser=deductionLoanRepository.getLoanCloser(org_id);
            
            System.out.println("loanCloser ");
            System.out.println(loanCloser.toString());
            
           
            System.out.println("employeeDeductionList ");
            System.out.println(employeeDeductionList);
  
            getAllDeductionLoanList.stream().forEach(action->{
            
                
              Long empId= action.getEmployeeId();
              Long loanTypeId=  action.getLoanTypeId();
              Long deductionLoanId=  action.getDeductionLoanId();
              
              employeeDeductionList.stream().forEach(a->{
              
               Long eid=  Long.parseLong(a.get("employee_id").toString());
               Long deduction_id=  Long.parseLong(a.get("deduction_id").toString());
                  
               if(Objects.equals(empId, eid) && Objects.equals(loanTypeId, deduction_id)){
               
                   action.setAmountRepaid(Double.parseDouble(a.get("total_deduction").toString()));
                   
               }
                  
              });
              
               loanCloser.stream().forEach(a->{
              
               Long deduction_loan_id=  Long.parseLong(a.get("deduction_loan_id").toString());
               double discount= a.get("discount") !=null ? Double.parseDouble(a.get("discount").toString()):0;
               double loan_repay= a.get("loan_repay") !=null ? Double.parseDouble(a.get("loan_repay").toString()):0;
               
               
               if(Objects.equals(deductionLoanId, deduction_loan_id)){
                   action.setDiscount((action.getDiscount() !=null? action.getDiscount():0)+discount);
                   action.setAmountRepaid((action.getAmountRepaid() !=null? action.getAmountRepaid():0)+loan_repay);
               
               }
                  
              });
            
            });
            
            resultMap.clear();
            resultMap.put("list", getAllDeductionLoanList);
            resultMap.put("empList", employeeList);
            resultMap.put("deductionList", deductionList);
            resultMap.put("status", "success");
            
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            ex.printStackTrace();
            
        }
        return resultMap;
    
        
    
    }
    
    
       @Override
    public Map getAllDeductionoanOfEmployee(Long empId) {
   
    
        
           Map resultMap = new HashMap<>();
        try {
            
           List<DeductionLoan> getAllDeductionLoanList=deductionLoanRepository.getAllDeductionLoanOfEmployee(empId);
          
            resultMap.clear();
            resultMap.put("list", getAllDeductionLoanList);
            resultMap.put("status", "success");
            
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            ex.printStackTrace();
            
        }
        return resultMap;
    
        
    
    }

    @Override
    public Map approvedOrRejectDeductionLoan(Map map) {
        
         Map resultMap = new HashMap<>();
        try {
            
            deductionLoanRepository.approvedOrRejectLaon(map.get("supervisorStatus").toString(),Long.parseLong(map.get("id").toString()));
            resultMap.put("status", "success");
            
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            ex.printStackTrace();
            
        }
        return resultMap;
    }
    
    @Override
    public Map loanAdjustment(Map map) {
        
         Map resultMap = new HashMap<>();
        try {
            
            LoanCloser loanCloser = mapper.convertValue(map, LoanCloser.class);
            if (loanCloser != null) {
                loanCloserRepository.save(loanCloser);
                deductionLoanRepository.updateLoanWhileClosedLoan(loanCloser.getRemainingLoan(),loanCloser.getTenure(),loanCloser.getDeductionLoanId());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while Closed Loan");
            }
            
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            ex.printStackTrace();
            
        }
        return resultMap;
    }
    
    @Override
    public ResponseEntity<byte[]> downloadDeductionLoanData(Long org_id,HttpServletRequest request) {
        
        
        try{
            
           List<DeductionLoan> getAllDeductionLoanList=deductionLoanRepository.getAllDeductionLoanList(org_id,"");
           
            Set<Long> distinctLoanTypeIds = getAllDeductionLoanList.stream()
                    .map(DeductionLoan::getLoanTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            List<LinkedCaseInsensitiveMap> employeeDeductionList=deductionLoanRepository.getDeductionLoanOfEachEmployee(distinctLoanTypeIds);
            
            
            List<LinkedCaseInsensitiveMap> loanCloser=deductionLoanRepository.getLoanCloser(org_id);
            
            getAllDeductionLoanList.stream().forEach(action->{
            
                
              Long empId= action.getEmployeeId();
              Long loanTypeId=  action.getLoanTypeId();
              Long deductionLoanId=  action.getDeductionLoanId();
              
              employeeDeductionList.stream().forEach(a->{
              
               Long eid=  Long.parseLong(a.get("employee_id").toString());
               Long deduction_id=  Long.parseLong(a.get("deduction_id").toString());
                  
               if(Objects.equals(empId, eid) && Objects.equals(loanTypeId, deduction_id)){
               
                   action.setAmountRepaid(Double.parseDouble(a.get("total_deduction").toString()));
                   
               }
                  
              });
              
               loanCloser.stream().forEach(a->{
              
               Long deduction_loan_id=  Long.parseLong(a.get("deduction_loan_id").toString());
               double discount= a.get("discount") !=null ? Double.parseDouble(a.get("discount").toString()):0;
               double loan_repay= a.get("loan_repay") !=null ? Double.parseDouble(a.get("loan_repay").toString()):0;
               
               if(Objects.equals(deductionLoanId, deduction_loan_id)){
                   action.setDiscount((action.getDiscount() !=null? action.getDiscount():0)+discount);
                   action.setAmountRepaid((action.getAmountRepaid() !=null? action.getAmountRepaid():0)+loan_repay);
               
               }
                  
              });
            
            });
            
            getAllDeductionLoanList.stream().forEach(action -> {

                if (action.getDiscount() == null) {

                    action.setDiscount(0.0);
                }

            });
            
            List<LinkedCaseInsensitiveMap> resultList
                    = getAllDeductionLoanList.stream()
                            .map(loan -> mapper.convertValue(
                            loan,
                            new TypeReference<LinkedCaseInsensitiveMap>() {
                    }
                    ))
                            .collect(Collectors.toList());
            
            // for employee code
            
            List<LinkedCaseInsensitiveMap> orgList = employeeDetailsRepository.findByOrgId(org_id);
           
            System.out.println("resultList");
            System.out.println(resultList);
            
            resultList.stream().forEach(ac->{
                
                Long empId= Long.parseLong(ac.get("employeeId").toString());
            
               orgList.stream().forEach(a->{
              
               Long eid=  Long.parseLong(a.get("employee_id").toString());
                  
               if(Objects.equals(empId, eid)){
               
                   ac.put("employeeCode", a.get("employee_code"));
                   
               }
                  
              });
            
            });
                        
            String[] combinedHeaderArray = {"S.No", "Name","Employee Code", "Loan Type", "Date", "Principal Loan", "Loan with Int.", "Monthly Installment", "Loan Repaid","Discount","Tenure","Remaining Loan","Status"};
            String[] combinedRowArray = {"employeeName","employeeCode", "loanType", "startDate", "description", "demandLoan", "monthlyInstallment","amountRepaid","discount","tenure","remainingAmount","supervisorStatus"};
            return commonExcelData.excelData(resultList, combinedHeaderArray, combinedRowArray, "Deduction-Loan-report", "DeductionLoanReport");

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
        
    }
    
}
