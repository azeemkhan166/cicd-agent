/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.CustomRunPayroll;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface CustomRunPayrollRepository extends JpaRepository<CustomRunPayroll, Long>{
    
    @Query(nativeQuery = true, value = "select * from custom_run_payroll where organization_id=?1 and month=?2 and year=?3 and site_id=?4")
    public List<CustomRunPayroll> alreadyRunpayroll(Long organizationId, int month, int year,long siteId);
    
    @Query(nativeQuery = true,value = "select * from custom_run_payroll where employee_id=?1 and month=?2 and year=?3 and site_id=?4")
    public CustomRunPayroll getPayrollDateByMonthAndYear(Long employee_id,int month,int year,Long siteId);
    
    @Query(nativeQuery = true,value = "select * from custom_run_payroll where employee_id=?1 and month=?2 and year=?3")
    public List<CustomRunPayroll> getPayrollMonthly(Long employee_id,int month,int year);
    
    @Query(nativeQuery = true, value = "select * from custom_run_payroll where organization_id=?1 and month=?2 and year=?3")
    public List<CustomRunPayroll> alreadyRunpayrollOrgWise(Long organizationId, int month, int year);
    
}
