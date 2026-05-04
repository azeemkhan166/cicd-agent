/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.CustomRunPayrollSheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface CustomRunPayrollSheetRepository extends JpaRepository<CustomRunPayrollSheet, Long>{
    
    @Query(nativeQuery = true, value = "select * from custom_run_payroll_sheet where organization_id=?1 and month=?2 and year=?3 and site_id=?4 and amount_depend=?5")
    public List<CustomRunPayrollSheet> runPayrollSheet(Long organizationId, int month, int year,long siteId,String netPayable);
    
    @Query(nativeQuery = true, value = "select * from custom_run_payroll_sheet where organization_id=?1 and month=?2 and year=?3")
    public List<CustomRunPayrollSheet> runPayrollSheetOrgWise(Long organizationId, int month, int year);
    
}
