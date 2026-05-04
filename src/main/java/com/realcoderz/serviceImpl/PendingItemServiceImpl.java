/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.repository.DeductionLoanRepository;
import com.realcoderz.repository.EmployeeLoanRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.WorkerLoanRepository;
import com.realcoderz.service.PendingItemService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tauseef
 */
@Service
public class PendingItemServiceImpl implements PendingItemService
{
    @Autowired
    private EmployeeLoanRepository employeeLoanRepo;
    
    @Autowired
    private InvestmentDeclarationRepository investRepo;
    
    @Autowired
    private DeductionLoanRepository deductionLoanRepo;
    
    @Autowired
    private WorkerLoanRepository workerRepo;
    
     static final Logger logger = LoggerFactory.getLogger(PendingItemService.class);

    @Override
    public Map pendingLoans(Map map) {
        String role= map.get("role")!=null?map.get("role").toString():"";
        Long employeeId= map.get("employeeId")!=null?Long.parseLong(map.get("employeeId").toString()):0;
        Long organizationId= map.get("organizationId")!=null?Long.parseLong(map.get("organizationId").toString()):0;
      Map response=new HashMap();
      List<LinkedCaseInsensitiveMap> allData=new ArrayList<>();
      try{
//          if(role !=null &&(role.equalsIgnoreCase("Ac")||role.equalsIgnoreCase("AcS"))){
//              List<LinkedCaseInsensitiveMap> employeeLoans= employeeLoanRepo.getLoanForAccountant(organizationId,"Pending");
//              employeeLoans.stream().forEach(loan->{
//              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
//              data.put("employee_id", loan.get("employee_id"));
//              data.put("activity_type", "Employee Loan");
//              data.put("supervisorStatus", loan.get("accountant_status"));
//              data.put("name",loan.get("name"));
//              data.put("employee_code", loan.get("employee_code"));
//              data.put("startDate",loan.get("start_date"));
//              data.put("endDate","-");
//              data.put("url","/accountant_apply_loan");
//              data.put("payrollItems","Yes");
//              data.put("email",loan.get("email"));
//              allData.add(data);
//              });   
//          }
          if(role !=null &&(role.equalsIgnoreCase("A")||role.equalsIgnoreCase("AcS")||role.equalsIgnoreCase("Ac"))){
               List<String> dataToSend= Arrays.asList("Pending");
              List<LinkedCaseInsensitiveMap> employeeLoans= employeeLoanRepo.getLoanForSupervisorAndHr(organizationId,dataToSend);
              List<LinkedCaseInsensitiveMap> deductionLoans= deductionLoanRepo.employeeDeductionsLoans(organizationId, dataToSend);
              List<LinkedCaseInsensitiveMap> workerLoans= workerRepo.getWorkerLoanForPendingAction(organizationId, dataToSend);
              employeeLoans.stream().forEach(loan->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Employee Loan");
              data.put("supervisorStatus", loan.get("supervisor_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("url","/supervisor_loan");
              data.put("payrollItems","Yes");
              data.put("email",loan.get("email"));
              allData.add(data);
              }); 
              deductionLoans.stream().forEach(loan->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Deduction Loan");
              data.put("supervisorStatus", loan.get("supervisor_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("payrollItems","Yes");
              data.put("url","/admin_deduction_loan");
              data.put("email",loan.get("email"));
              allData.add(data);
              });
              
              workerLoans.stream().forEach(loan->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Worker Loan");
              data.put("supervisorStatus", loan.get("hr_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("payrollItems","Yes");
              data.put("url","/hr_loan");
              data.put("email",loan.get("email"));
              allData.add(data);
              });
          }
          if(role !=null &&(role.equalsIgnoreCase("A")||role.equalsIgnoreCase("AcS")||role.equalsIgnoreCase("Ac"))){
              List<LinkedCaseInsensitiveMap> employeeInvestments=investRepo.getOrganizationInvesetmentForPendingItems(organizationId, false);
          employeeInvestments.stream().forEach(investment->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", investment.get("employee_id"));
              data.put("activity_type", "Investment Decleration");
              data.put("supervisorStatus", investment.get("approved_by_acc_status"));
              data.put("name",investment.get("name"));
              data.put("employee_code", investment.get("employee_code"));
              data.put("startDate",investment.get("fy_year"));
              Integer endDate= Integer.parseInt(investment.get("fy_year").toString())+1;
              data.put("endDate",endDate);
              data.put("payrollItems","Yes");
              data.put("email",investment.get("email"));
              allData.add(data);
          
          
          });
          }
          
          response.put("data",allData);
          response.put("status","success");

          
      }catch(Exception e){
          response.put("status","exception");
          response.put("msg",e.getMessage());
          e.printStackTrace();
          logger.info("exception in pendingLoans()-> "+e.getMessage());
                  
      }
      return response;
    }

    @Override
    public Map archiveLoansAndInvsetment(Map map) {
         String role= map.get("role")!=null?map.get("role").toString():"";
        Long employeeId= map.get("employeeId")!=null?Long.parseLong(map.get("employeeId").toString()):0;
        Long organizationId= map.get("organizationId")!=null?Long.parseLong(map.get("organizationId").toString()):0;
      Map response=new HashMap();
      List<LinkedCaseInsensitiveMap> allData=new ArrayList<>();
      try{
//          if(role !=null &&(role.equalsIgnoreCase("Ac")||role.equalsIgnoreCase("AcS"))){
//              List<LinkedCaseInsensitiveMap> employeeLoans= employeeLoanRepo.getLoanForAccountant(organizationId,"Approved");
//              employeeLoans.stream().forEach(loan->{
//              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
//              data.put("employee_id", loan.get("employee_id"));
//              data.put("activity_type", "Employee Loan");
//              data.put("supervisorStatus", loan.get("accountant_status"));
//              data.put("name",loan.get("name"));
//              data.put("employee_code", loan.get("employee_code"));
//              data.put("startDate",loan.get("start_date"));
//              data.put("endDate","-");
//              data.put("payrollItems","Yes");
//              data.put("email",loan.get("email"));
//              allData.add(data);
//              });   
//          }
          if(role !=null &&(role.equalsIgnoreCase("A")||role.equalsIgnoreCase("AcS")||role.equalsIgnoreCase("Ac"))){
             List<String> dataToSend= Arrays.asList("Approved","Rejected","Reject");
                   
                     
              List<LinkedCaseInsensitiveMap> employeeLoans= employeeLoanRepo.getLoanForSupervisorAndHr(organizationId,dataToSend);
              List<LinkedCaseInsensitiveMap> deductionLoans= deductionLoanRepo.employeeDeductionsLoans(organizationId, dataToSend);
              List<LinkedCaseInsensitiveMap> workerLoans= workerRepo.getWorkerLoanForPendingAction(organizationId, dataToSend);

              employeeLoans.stream().forEach(loan->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Employee Loan");
              data.put("supervisorStatus", loan.get("supervisor_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("payrollItems","Yes");
              data.put("email",loan.get("email"));
              allData.add(data);
              });   
              deductionLoans.stream().forEach(loan->{
               LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Deduction Loan");
              data.put("supervisorStatus", loan.get("supervisor_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("payrollItems","Yes");
              data.put("email",loan.get("email"));
              allData.add(data);
              });
               workerLoans.stream().forEach(loan->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", loan.get("employee_id"));
              data.put("activity_type", "Worker Loan");
              data.put("supervisorStatus", loan.get("hr_status"));
              data.put("name",loan.get("name"));
              data.put("employee_code", loan.get("employee_code"));
              data.put("startDate",loan.get("start_date"));
              data.put("endDate","-");
              data.put("payrollItems","Yes");
              data.put("url","/hr_loan");
              data.put("email",loan.get("email"));
              allData.add(data);
              });
          }
                    if(role !=null &&(role.equalsIgnoreCase("A")||role.equalsIgnoreCase("AcS")||role.equalsIgnoreCase("Ac"))){

          List<LinkedCaseInsensitiveMap> employeeInvestments=investRepo.getOrganizationInvesetmentForPendingItems(organizationId, true);
          employeeInvestments.stream().forEach(investment->{
              LinkedCaseInsensitiveMap data = new LinkedCaseInsensitiveMap();
              data.put("employee_id", investment.get("employee_id"));
              data.put("activity_type", "Investment Decleration");
              data.put("supervisorStatus", investment.get("approved_by_acc_status"));
              data.put("name",investment.get("name"));
              data.put("employee_code", investment.get("employee_code"));
              data.put("startDate",investment.get("fy_year"));
              Integer endDate= Integer.parseInt(investment.get("fy_year").toString())+1;
              data.put("endDate",endDate);
              data.put("payrollItems","Yes");
              data.put("email",investment.get("email"));
              allData.add(data);
          
          
          });
                    }
          response.put("data",allData);
          response.put("status","success");

          
      }catch(Exception e){
          response.put("status","exception");
          response.put("msg",e.getMessage());
          e.printStackTrace();
          logger.info("exception in pendingLoans()-> "+e.getMessage());
                  
      }
      return response;
        
    }
    
    
    
}
