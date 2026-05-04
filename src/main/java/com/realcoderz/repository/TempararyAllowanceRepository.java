/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.TempararyAllowance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface TempararyAllowanceRepository  extends JpaRepository<TempararyAllowance, Long>{
    
    @Query(value = "select * from temparary_allowance where organization_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<TempararyAllowance> getTempararyAllowanceMonthWise(Long orgId,Integer month,Integer year );

    @Query(value = "select * from temparary_allowance where employee_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<TempararyAllowance> getTempararyAllowanceOfEmployee(Long employee_id,Integer month,Integer year );

    @Query(value = "select allowance_id as allowanceId,amount as allowance_amount,amount as allowance_payable_amount,name as allowance_name from temparary_allowance where employee_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getTempararyAllowance(Long employee_id,Integer month,Integer year );
	
	 @Query(value = "SELECT * FROM temparary_allowance where organization_id=? and month=? and year=?",nativeQuery = true)
    public List<TempararyAllowance> getOrganizationMonthlyTempAllowance(Long organizationId,int month,int year);
    
    @Query(value = "SELECT * FROM temparary_allowance where employee_id=? and month=? and year=?",nativeQuery = true)
    public List<TempararyAllowance> getEmployeeMonthlyTempAllowance(Long employeeId,int month,int year);
    

}
