/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.auditable.BearerTokenUtil;
import com.realcoderz.model.EmployeeIdDates;
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.model.OrganizationIdDates;
import com.realcoderz.model.employeeDetails;
import com.realcoderz.repository.EmployeeIdDatesRepo;
import com.realcoderz.repository.OrganizationIdDatesRepo;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.EmployeeIdDatesService;
import com.realcoderz.service.NotificationByWebsocketService;
import static com.realcoderz.serviceImpl.ProofOfInvestmentServiceImpl.logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tause
 */
@Service
@RequiredArgsConstructor
public class EmployeeIdDatesServiceImpl implements EmployeeIdDatesService{

    private final SalaryBreakuprepo salaryRepo;
    private final EmployeeIdDatesRepo repo;
    private final OrganizationIdDatesRepo orgDatesRepo;
    private final employeeDetailsRepository empDetailsRepo;
    private final NotificationByWebsocketService notificationService;
    private final CommonMailService mailService;
    
    
     static final Logger logger = LoggerFactory.getLogger(EmployeeIdDatesServiceImpl.class);

    @Override
    public Map saveEmployeesIdDeclerationDates(Long organizationId, String startDate, String endDate, Integer fyYear,String token) {
       Map response=new HashMap();
        try{
            System.out.println("saveEmployeesIdDeclerationDates 38");
            List<Long> employeeIds= salaryRepo.getEmployeeIds(organizationId);
            List<EmployeeIdDates> organizationEmployees=repo.employeeList(organizationId,fyYear);
            organizationEmployees.stream().forEach(data->{
                if(!data.isEmployeeRequested()){
                    data.setStartdate(startDate);
               data.setEndDate(endDate);
                }
               
            
            });
            
             // Create a Set of employee IDs that exist in the organizationEmployees list means dates already saved
            Set<Long> organizationEmployeeIds = organizationEmployees.stream()
            .map(EmployeeIdDates::getEmployeeId)
            .collect(Collectors.toSet());

        // Filter employeeIds to include only those that dont exist in organizationEmployeeIds
             List<Long> filteredEmployeeIds = employeeIds.stream()
            .filter(id -> !organizationEmployeeIds.contains(id))
            .collect(Collectors.toList());
             
             if(!filteredEmployeeIds.isEmpty()){
                 filteredEmployeeIds.stream().forEach(data->{
                    EmployeeIdDates idDates=new EmployeeIdDates();
                     idDates.setEmployeeId(data);
                     idDates.setOrganizationId(organizationId);
                     idDates.setFinancialYear(fyYear);
                     idDates.setStartdate(startDate);
                     idDates.setEndDate(endDate);
                     idDates.setStatus("Approved");
                     idDates.setEmployeeRequested(false);
                     organizationEmployees.add(idDates);
                 });
             }
             
            repo.saveAll(organizationEmployees);
            new Thread(()->this.sendNotification(organizationEmployeeIds, filteredEmployeeIds, startDate, endDate, organizationId,token)).start();
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map getEmployeeInvestmentDates(Long employeeId, String fyYear) {
        Map response=new HashMap();
         try{
          EmployeeIdDates data=repo.employeeIdDates(employeeId, fyYear);
          response.put("status","success");
          response.put("data",data);
             
         }catch(Exception e){
          response.put("status","exception");
          response.put("msg","Exception in getEmployeeInvestmentDates");
         }
         return response;
    }

    @Override
    public Map saveEmployeeInvestmentDates(Long employeeId, Integer fyYear, Long organizationId,Boolean employeeRequested) {
        Map response=new HashMap();
        try{
         OrganizationIdDates organizationIdDates= orgDatesRepo.findByOrganizationIdAndFinancialYear(organizationId, fyYear);
          if(organizationIdDates!=null){
              if(LocalDate.now().getMonthValue()<4){
                fyYear=fyYear-1; 
             }
              EmployeeIdDates empDates= repo.employeeIdDates(employeeId, fyYear.toString());
              if(empDates==null){
              EmployeeIdDates edData=new EmployeeIdDates();
              edData.setEmployeeId(employeeId);
              edData.setStatus("Approved");
              edData.setOrganizationId(organizationId);
              edData.setFinancialYear(fyYear);
              edData.setStartdate(organizationIdDates.getStartdate());
              edData.setEndDate(organizationIdDates.getEndDate());
              edData.setEmployeeRequested(employeeRequested);
              repo.save(edData);
              }
             
          }
          
          response.put("status", "success");
          response.put("msg","Investment window open request send!");
            
        }catch(Exception e){
          e.printStackTrace();
          logger.info("Exception in saveEmployeeInvestmentDates=>"+e.getMessage());
          response.put("status","Exception");
          response.put("msg","Exception in saveEmployeeInvestmentDates"+e.getMessage());
        }
        return response;
    }

    @Override
    public Map updateEmployeeInvestmentDates(Long employeeId, Integer fyYear,Long organizationId,String startDate,String endDate) {
       Map response=new HashMap();
        try{
            EmployeeIdDates empDates= repo.employeeIdDates(employeeId, fyYear.toString());
              if(empDates==null){
              EmployeeIdDates edData=new EmployeeIdDates();
              edData.setEmployeeId(employeeId);
              edData.setStatus("Pending");
              edData.setOrganizationId(organizationId);
              edData.setFinancialYear(fyYear);
              edData.setStartdate(startDate);
              edData.setEndDate(endDate);
              edData.setEmployeeRequested(true);
              repo.save(edData);
              }else{
              empDates.setStartdate(startDate);
              empDates.setEndDate(endDate);
              empDates.setEmployeeRequested(true); 
              empDates.setStatus("Pending");
              repo.save(empDates);
              }
              response.put("status","success");
              response.put("msg","Investment window change request received successfully");
              
               List<String> RecipientEmails = new ArrayList<>();
                        List<String> RecipientNames = new ArrayList<>();

                       List<LinkedCaseInsensitiveMap> getOrgAccountant=empDetailsRepo.getAccountantOfOrg(organizationId);
                       logger.info("Accountant Details for notification "+getOrgAccountant);
                        getOrgAccountant.stream().forEach(action -> {
                            RecipientEmails.add(action.get("email").toString());
                            RecipientNames.add(action.get("name").toString());

                        });
                       
                        employeeDetails findByEmployeeId =empDetailsRepo.findByEmployeeId(employeeId);
                        logger.info("employeeDetails for notification "+findByEmployeeId);
                        
                        NotificationByWebsocket employeeNotification=new NotificationByWebsocket();
                
                        employeeNotification.setPriority("MODERATE");
                        employeeNotification.setType("INFO");
    			employeeNotification.setPushNotify(0);
    			employeeNotification.setMailNotify(0);
    			employeeNotification.setModuleName("Payroll");
    			employeeNotification.setOrganizationId(organizationId);
    			employeeNotification.setSenderId(0);
    			employeeNotification.setSenderName("");
    			employeeNotification.setTitle("Request to open investment windows");
    			employeeNotification.setBody(findByEmployeeId.getName() +" has requested to open the investment windows between "+startDate+" and "+endDate);
    			employeeNotification.setRecipientEmails(RecipientEmails);
    			employeeNotification.setRecipientNames(RecipientNames);
                        
                        new Thread(() -> notificationService.sendNotification(employeeNotification)).start();
            
        }catch(Exception e){
            e.printStackTrace();
            logger.info("Exception in updateEmployeeInvestmentDates=>"+e.getMessage());
            response.put("status","Exception");
            response.put("msg","Exception in updateEmployeeInvestmentDates"+e.getMessage());          
        }
        return response;
        
    }

    @Override
    public Map getRequestedInvestmentWindowDates(Long organizationId, Integer fyYear) {
        Map response=new HashMap();
         try{
           List<LinkedCaseInsensitiveMap> employeeRequest=repo.employeeRequestedWindowList(organizationId, fyYear);
           response.put("status","success");
           response.put("data",employeeRequest);
             
         }catch(Exception e){
            e.printStackTrace();
            logger.info("Exception in updateEmployeeInvestmentDates=>"+e.getMessage());
            response.put("status","Exception");
            response.put("msg","Exception in updateEmployeeInvestmentDates"+e.getMessage());      
         }
         return response;
    }

    @Override
    public Map updateEmployeeWindowRequestWindow(Long rowId, String status) {
        Map response=new HashMap();
        try{
           
          String token = BearerTokenUtil.getBearerTokenHeader();
          List<Long> employeeIds=new ArrayList<>();
          List<String> employeeNames=new ArrayList<>();
          List<String> employeeEmails=new ArrayList<>();
          EmployeeIdDates dates=repo.findById(rowId).get();
          dates.setStatus(status);
          repo.save(dates); 
          employeeIds.add(dates.getEmployeeId());
          response.put("status","success");
          response.put("msg","Investment window open request has been "+status);
          new Thread(()->{
           if(status.equalsIgnoreCase("Approved")){
             List<employeeDetails> empDetails= empDetailsRepo.findByEmployeeIds(employeeIds);
             empDetails.stream().forEach(data->{
                 employeeNames.add(data.getName());
                 employeeEmails.add(data.getEmail());          
             });
             String subject="Investment window open request has been "+status+".";
            
             String message = "Dear "+empDetails.get(0).getName() + "," + "<br/>" + "<br/>" + "Your request to open the investment window has been approved. You can now fill investments between  " + dates.getStartdate() + " " + "and" + " " + dates.getEndDate() + "." + "<br/>" + "<br/>" + "Regards" + "<br/>" + "<br/>" + "Admin";
             mailService.sendCommonMail(dates.getOrganizationId().toString(), token, empDetails.get(0).getEmail(), subject, message);
             NotificationByWebsocket employeeNotification=new NotificationByWebsocket(); 
              employeeNotification.setPriority("MODERATE");
              employeeNotification.setType("INFO");
              employeeNotification.setPushNotify(0);
    	      employeeNotification.setMailNotify(0);
    	      employeeNotification.setModuleName("Payroll");
    	      employeeNotification.setOrganizationId(dates.getOrganizationId());
    	      employeeNotification.setSenderId(0);
    	      employeeNotification.setSenderName("");
    	      employeeNotification.setTitle("Investment window open request has been "+status);
    	      employeeNotification.setBody("Your request to open the investment window has been approved. You can now fill investments between " +dates.getStartdate() +" and "+dates.getEndDate()+".");
    	      employeeNotification.setRecipientEmails(employeeEmails);
    	      employeeNotification.setRecipientNames(employeeNames);
              notificationService.sendNotification(employeeNotification);
          }
          }).start();
         
           
        }catch(Exception e){
            e.printStackTrace();
            logger.info("Exception in updateEmployeeWindowRequestWindow=>"+e.getMessage());
            response.put("status","Exception");
            response.put("msg","Exception in updateEmployeeWindowRequestWindow"+e.getMessage());   
        }
        return response;
    }

    @Override
    public void sendNotification(Set<Long> updateEmployeeIds, List<Long> newEmployeeIds, String startDate, String endDate, Long organizationId,String token) {
        try{
//            System.out.println("request "+request.getHeader("Authorization"));
//            String token = BearerTokenUtil.getBearerTokenHeader();
            List<Long> allEmployees= new ArrayList<>();

            allEmployees.addAll(newEmployeeIds);
            allEmployees.addAll(updateEmployeeIds);
            List<String> updateEmployeeEmails=new ArrayList<>();
            List<String> updateEmployeeName=new ArrayList<>();
            List<String> newEmployeeEmails=new ArrayList<>();
            List<String> newEmployeeNames=new ArrayList<>();
           
            List<employeeDetails> empDetails= empDetailsRepo.findByEmployeeIds(allEmployees);

             empDetails.stream().forEach(data->{
                  updateEmployeeIds.stream().forEach(employeeid->{
                    
                      if(Objects.equals(data.getEmployeeId(), employeeid)){
             String subject="Update in the Investment window dates";      
             String message = "Dear "+data.getName() + "," + "<br/>" + "<br/>" + "The organization has set new dates for investment window. Now You can now fill your investments between " + startDate + " " + "and" + " " + endDate + "." + "<br/>" + "<br/>" + "Regards" + "<br/>" + "<br/>" + "Admin";
             new Thread(()-> mailService.sendCommonMail(organizationId.toString(), token, data.getEmail(), subject, message)).start(); 
             updateEmployeeEmails.add(data.getEmail());
             updateEmployeeName.add(data.getName());
                      }  
                      
                  });
                  
                 newEmployeeIds.stream().forEach(employeeid->{
                      if(Objects.equals(data.getEmployeeId(), employeeid)){
             String subject="Investment window dates.";      
             String message = "Dear "+data.getName() + "," + "<br/>" + "<br/>" + "The organization has set the investment window dates. You can now fill your investments between " + startDate + " " + "and" + " " + endDate + "." + "<br/>" + "<br/>" + "Regards" + "<br/>" + "<br/>" + "Admin";
                         new Thread(()-> mailService.sendCommonMail(organizationId.toString(), token, data.getEmail(), subject, message)).start(); 
                          newEmployeeEmails.add(data.getEmail());
                          newEmployeeNames.add(data.getName());
                      }                
                 });
             });
             
             String subjectForNewEmployee="Investment window dates.";
             String msgForNewEmployee="The organization has set the investment window dates. You can now fill your investments between" +startDate+" and "+endDate+".";
             
             String subjectForUpdateEmployee="Update in the Investment window dates";
             String msgForUpdateEmployee="The organization has set new dates for investment window. Now You can now fill your investments between" +startDate+" and "+endDate+".";
             NotificationByWebsocket employeeNotification=new NotificationByWebsocket(); 
              employeeNotification.setPriority("MODERATE");
              employeeNotification.setType("INFO");
              employeeNotification.setPushNotify(0);
    	      employeeNotification.setMailNotify(0);
    	      employeeNotification.setModuleName("Payroll");
    	      employeeNotification.setOrganizationId(organizationId);
    	      employeeNotification.setSenderId(0);
    	      employeeNotification.setSenderName("");
    	      employeeNotification.setTitle(subjectForNewEmployee);
    	      employeeNotification.setBody(msgForNewEmployee);
    	      employeeNotification.setRecipientEmails(newEmployeeEmails);
    	      employeeNotification.setRecipientNames(newEmployeeNames);
              if(newEmployeeEmails!=null && !newEmployeeEmails.isEmpty()){
                   notificationService.sendNotification(employeeNotification);
              }
             
             
          
                
             NotificationByWebsocket employeeNotificationupdate=new NotificationByWebsocket(); 
              employeeNotificationupdate.setPriority("MODERATE");
              employeeNotificationupdate.setType("INFO");
              employeeNotificationupdate.setPushNotify(0);
    	      employeeNotificationupdate.setMailNotify(0);
    	      employeeNotificationupdate.setModuleName("Payroll");
    	      employeeNotificationupdate.setOrganizationId(organizationId);
    	      employeeNotificationupdate.setSenderId(0);
    	      employeeNotificationupdate.setSenderName("");
    	      employeeNotificationupdate.setTitle(subjectForUpdateEmployee);
    	      employeeNotificationupdate.setBody(msgForUpdateEmployee);
    	      employeeNotificationupdate.setRecipientEmails(updateEmployeeEmails);
    	      employeeNotificationupdate.setRecipientNames(updateEmployeeName);
              if(updateEmployeeEmails!=null && !updateEmployeeEmails.isEmpty()){
            notificationService.sendNotification(employeeNotificationupdate);

              }
              
              
            
             
          
          
            
        }catch(Exception e){
            e.printStackTrace();
            logger.info("Exception in updateEmployeeWindowRequestWindow=>"+e.getMessage());
            
        }
        
    }
    
    
    
    
}