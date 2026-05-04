/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.TempararyDeduction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface TempararyDeductionRepository extends JpaRepository<TempararyDeduction, Long>{
    
    @Query(value = "select * from temparary_deduction where organization_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<TempararyDeduction> getTempararyDeductionMonthWise(Long orgId,Integer month,Integer year );
    
     @Query(value = "select * from temparary_deduction where employee_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<TempararyDeduction> getTempararyDeductionOfEmployee(Long employee_id,Integer month,Integer year );
    
    @Query(value = "select deduction_id as deductionId,amount as deduction_amount,amount as deduction_payable_amount,name as deduction_name from temparary_deduction where employee_id=?1 and month=?2 and year=?3",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getTempararyDeduction(Long employee_id,Integer month,Integer year );
    
    @Query(value = "select * from temparary_deduction where employee_id=?1 and month=?2 and year=?3 and name='Income Tax'",nativeQuery = true)
    public Optional<TempararyDeduction> getIncomeTax(Long employee_id,Integer month,Integer year );
	
	  @Query(value = "SELECT amount,employee_id FROM temparary_deduction where organization_id=? and month=? and year=? and name='Income tax' and update_tds='Yes'",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getUpdatedMonthlyTdsOfOrg(Long organizationId,int month,int year);
    
    @Query(value = "SELECT * FROM temparary_deduction where employee_id=? and month=? and year=?",nativeQuery = true)
    public List<TempararyDeduction> getUpdatedDeductionsofEmployee(Long employee_id,int month,int year);
    
}
