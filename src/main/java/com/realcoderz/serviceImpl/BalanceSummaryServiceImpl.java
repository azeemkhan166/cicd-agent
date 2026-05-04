
package com.realcoderz.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.BalanceSummary;
import com.realcoderz.model.BalanceSummaryHistory;
import com.realcoderz.model.RunPayRoll;
import com.realcoderz.repository.BalanceSummaryHistoryRepo;
import com.realcoderz.repository.BalanceSummaryRepo;
import com.realcoderz.service.BalanceSummaryService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import java.time.LocalDate;

/**
 *
 * @author tauseef
 */
@Service
@RequiredArgsConstructor
public class BalanceSummaryServiceImpl implements BalanceSummaryService
{
    private final BalanceSummaryRepo balanceRepo;
    private final BalanceSummaryHistoryRepo historyRepo;
   
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map saveBalanceSummary(List<RunPayRoll> runpayROllList) {
        Map response=new HashMap();
        try{
          Long organizationId= runpayROllList.get(0).getOrganizationId();
          int month=runpayROllList.get(0).getPayRunMonth();
          int year=runpayROllList.get(0).getPayRunYear();
          List<BalanceSummary> listToSave=new ArrayList<>();
          if(month==1){
              month=12;
              year=year-1;
          }else{
              month=month-1;
          }
         // System.out.println(month+" "+" "+year);
          List<BalanceSummary> previousMonthSummary=balanceRepo.orgBalanceSummary(organizationId, month, year);
           // System.out.println(previousMonthSummary.toString());
          runpayROllList.forEach(payRun->{
              BalanceSummary balanceReport=new BalanceSummary();
                balanceReport.setEmployeeId(payRun.getEmployeeId());
                balanceReport.setOrganizationId(organizationId);
                balanceReport.setOpeningBalance(0.0);
                balanceReport.setCurrentMonthSalary(payRun.getNet_payable());
                balanceReport.setPayment(0.0);
                balanceReport.setNetBalance(balanceReport.getCurrentMonthSalary()+balanceReport.getOpeningBalance());
                balanceReport.setMonth(payRun.getPayRunMonth());
                balanceReport.setYear(payRun.getPayRunYear());
               previousMonthSummary.forEach(summary->{
                  
                  if(payRun.getEmployeeId().compareTo(summary.getEmployeeId())==0){
                       balanceReport.setOpeningBalance(summary.getNetBalance());
                       balanceReport.setNetBalance(balanceReport.getCurrentMonthSalary()+balanceReport.getOpeningBalance());
                  }
             });
               listToSave.add(balanceReport);
          
          });
             balanceRepo.saveAll(listToSave);
            
        }catch(Exception e){
            e.printStackTrace();
        }
         
        return response;
        
    }

    @Override
    public Map getMonthlyOrgBalanceSummary(String request) {
        Map response=new HashMap();
        try{
            System.out.println("request 82"+" "+request);
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(request), LinkedCaseInsensitiveMap.class);
            
            Long orgnanizationId=Long.parseLong(map.get("organizationId").toString());
            int month=Integer.parseInt(map.get("month").toString());
            int year=Integer.parseInt(map.get("year").toString());
            List<LinkedCaseInsensitiveMap> orgMonthlyBalance=balanceRepo.orgMonthlyBalanceSummary(orgnanizationId, month, year);
            response.put("data", orgMonthlyBalance);
            response.put("status", "success");
        }catch(Exception e){
             response.put("status", "exception");
            
            e.printStackTrace();
        }
        return response;
    }

 @Override
    public Map updatePayment(String data) {
        Map response=new HashMap();
        try{
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
             Long balanceSummaryId=Long.parseLong(map.get("balanceSummaryId").toString());
             Double paymentAmount=Double.parseDouble(map.get("amount").toString());
             String date=map.get("paymentDate").toString();
             LocalDate selectedDate=LocalDate.parse(date);
              LocalDate currentDate=LocalDate.now();

           if(selectedDate.getYear() > currentDate.getYear() || 
           (selectedDate.getYear() == currentDate.getYear() && selectedDate.getMonthValue() > currentDate.getMonthValue())){
             response.put("status", "error");
             response.put("msg", "You cannot set future month date in payment details!");
               
           }else{
               BalanceSummary balanceSummary= balanceRepo.findById(balanceSummaryId).get();
                if(balanceSummary.getMonth() > selectedDate.getMonthValue() || balanceSummary.getYear()>selectedDate.getYear()){
             response.put("status", "error");
             response.put("msg", "You cannot set previous month date in payment details!");
                }else{
             LinkedCaseInsensitiveMap maxBalanceSummaryId= balanceRepo.getBalanceSummaryId(balanceSummary.getEmployeeId());
            
             Long maxId=Long.parseLong(maxBalanceSummaryId.get("balance_summary_id").toString());
             if(Objects.equals(balanceSummary.getBalanceSummaryId(), maxId)){
                  balanceSummary.setPayment(paymentAmount+balanceSummary.getPayment());
             balanceSummary.setNetBalance(balanceSummary.getNetBalance()-paymentAmount);
             BalanceSummaryHistory history=new BalanceSummaryHistory();
             history.setBalanceSummaryId(balanceSummary.getBalanceSummaryId());
             history.setNetBalance(balanceSummary.getNetBalance());
             history.setOpeningBalance(balanceSummary.getOpeningBalance());
             history.setPaymentAmount(paymentAmount);
             history.setCurrentMonthSalary(balanceSummary.getCurrentMonthSalary());
             history.setMonth(balanceSummary.getMonth());
             history.setYear(balanceSummary.getYear());
             history.setEmployeeId(balanceSummary.getEmployeeId());
             history.setOrganizationId(balanceSummary.getOrganizationId());
             history.setPaymentDate(map.get("paymentDate").toString());
             balanceRepo.save(balanceSummary);
             historyRepo.save(history);
             response.put("status", "success");
             response.put("msg", "Payment updated!");
             }else{
             response.put("status", "error");
             response.put("msg", "You have already run payroll of future month you cannot update this month payment details!");
             }
                }
           }

             
             
            
        }catch(Exception e){
            response.put("status", "exception");
            response.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
   
    @Override
    public Map getSummaryHistory(String data) {
        Map response=new HashMap();
         try{
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
             Long balanceSummaryId=Long.parseLong(map.get("balanceSummaryId").toString());
             List<LinkedCaseInsensitiveMap> salaryHistory=balanceRepo.getBalanceHistory(balanceSummaryId);
              List<LinkedCaseInsensitiveMap> historyRecords=new ArrayList<>();
              salaryHistory.stream().forEach(record->{
                if(record.get("payment_date")!=null){
                    LinkedCaseInsensitiveMap historyMap =new LinkedCaseInsensitiveMap();
                     historyMap.put("payment_date",record.get("payment_date"));
                     historyMap.put("payment_amount",record.get("payment_amount"));
                     historyMap.put("netBalance",record.get("historyNetBalance"));
                     historyRecords.add(historyMap);
                }
              });
              Map outPut=new HashMap();
              outPut.put("current_month_salary", salaryHistory.get(0).get("current_month_salary"));
              outPut.put("net_balance", salaryHistory.get(0).get("net_balance"));
              outPut.put("payment", salaryHistory.get(0).get("payment"));
              outPut.put("opening_balance", salaryHistory.get(0).get("opening_balance"));
              outPut.put("history", historyRecords);
              response.put("data", outPut);
              response.put("status","success");
         }catch(Exception e){
            response.put("status", "exception");
            response.put("msg", e.getMessage());
            e.printStackTrace(); 
         }
         return response;
    }
    
    
}
