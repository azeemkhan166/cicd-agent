/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.PayrollSetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * @author Astha
 */
@Repository
public interface PayrollSettingRepository extends JpaRepository<PayrollSetting,Long> {

    @Query("SELECT payslip_generation_date FROM PayrollSetting where organization_id=:organizationId")
    public String findPayDayByOrgId(@Param("organizationId")Long organizationId);

    @Modifying
    @Query("Update PayrollSetting prs set prs.last_pay_run_month=?1,prs.last_pay_run_year=?2 WHERE prs.organization_id=?3")
    public void updateLastPayRun(int month, int year, long orgId);

    @Query("SELECT count(*) FROM PayrollSetting prs WHERE prs.last_pay_run_month=?1 and prs.last_pay_run_year=?2 and prs.organization_id=?3")
    public int checkPayRollExist(int month, int year, long orgId);
    
    @Query(nativeQuery = true, value="SELECT * FROM payroll_setting WHERE organization_id=?1")
    public List<PayrollSetting> getPayrollSetting(long orgId);
    
    @Query(nativeQuery=true, value="Select start_date, end_date from payroll_setting where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getSalaryDates(Long organization_id);
    
    @Query(nativeQuery=true, value="Select start_date, end_date,leave_encashment from payroll_setting where organization_id=?1")
    public LinkedCaseInsensitiveMap getSalaryDatesCycle(Long organization_id);
    
}
