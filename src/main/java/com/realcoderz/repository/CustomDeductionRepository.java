/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.CustomDeduction;
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
public interface CustomDeductionRepository extends JpaRepository<CustomDeduction, Long>{
    
    @Query(nativeQuery = true, value = "Select * from custom_deduction where organization_id=?1")
    public List<CustomDeduction> findDeductionById(Long org_id);
    
    @Query(nativeQuery = true, value = "Select * from custom_deduction where organization_id=?1 and employee_type=?2")
    public List<CustomDeduction> findDeductionByEmployeeType(Long org_id,String employeeType);
    
    @Query(nativeQuery = true,value = "select ed.deduction_amount,cd.deduction_name,ed.deduction_id from employee_deduction ed left join custom_deduction cd on ed.deduction_id=cd.id where salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getDeductionData(Long ids);
}
