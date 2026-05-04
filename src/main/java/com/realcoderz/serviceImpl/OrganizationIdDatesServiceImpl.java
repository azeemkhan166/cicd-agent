/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.model.OrganizationIdDates;
import com.realcoderz.repository.OrganizationIdDatesRepo;
import com.realcoderz.service.EmployeeIdDatesService;
import com.realcoderz.service.OrganizationIdDatesService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tause
 */
@Service
public class OrganizationIdDatesServiceImpl implements OrganizationIdDatesService{


    @Autowired
    private OrganizationIdDatesRepo repo;

    @Autowired
    private EmployeeIdDatesService employeeIdService;

    static final Logger logger = LoggerFactory.getLogger(OrganizationIdDatesServiceImpl.class);

    @Override
    public Map saveOrganizationIdDeclerationDates(OrganizationIdDates dates,String token) {
        Map response=new HashMap();
        try{

            System.out.println("dates 25"+" "+dates.toString());
         OrganizationIdDates organizationIdDates= repo.findByOrganizationIdAndFinancialYear(dates.getOrganizationId(), dates.getFinancialYear());
            if(organizationIdDates!=null){
                organizationIdDates.setStartdate(dates.getStartdate());
                organizationIdDates.setEndDate(dates.getEndDate());
                repo.save(organizationIdDates);
                 response.put("status","success");
                 response.put("msg","Data saved");

            }else{
                 OrganizationIdDates organizationIdDatesNew=new OrganizationIdDates();
                 organizationIdDatesNew.setOrganizationId(dates.getOrganizationId());
                 organizationIdDatesNew.setEndDate(dates.getStartdate());
                 organizationIdDatesNew.setStartdate(dates.getEndDate());
                 organizationIdDatesNew.setFinancialYear(dates.getFinancialYear());
                repo.save(organizationIdDatesNew);
                response.put("status","success");
                response.put("msg","Data saved");

            }
           new Thread(()-> employeeIdService.saveEmployeesIdDeclerationDates(dates.getOrganizationId(), dates.getStartdate(), dates.getEndDate(), dates.getFinancialYear(),token)).start();



        }catch(Exception e){
            e.printStackTrace();
        }
        return response;

    }

    @Override
    public Map getOrganizationFyYearData(Integer fyYear,Long organizationId) {
        Map response=new HashMap();
         try{
         OrganizationIdDates organizationIdDates= repo.findByOrganizationIdAndFinancialYear(organizationId,fyYear);
         response.put("status","success");
         response.put("data",organizationIdDates);
         }catch(Exception e){
             e.printStackTrace();
             logger.info("exception in getOrganizationFyYearData=> "+e.getMessage());
         }
         return response;
    }

}
