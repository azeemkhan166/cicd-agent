/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.CustomAllowance;
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
public interface CustomAllowanceRepository extends JpaRepository<CustomAllowance, Long>{
    
    
    @Query(nativeQuery = true, value = "Select * from custom_allowance where organization_id=?1")
    public List<CustomAllowance> findAllowanceById(Long org_id);
    
    @Query(nativeQuery = true, value = "Select * from custom_allowance where organization_id=?1 and employee_type=?2")
    public List<CustomAllowance> findAllowanceByIdAndType(Long org_id,String empType);
    
    @Query(nativeQuery = true, value = "Select * from custom_allowance where organization_id=?1 and employee_type=?2")
    public List<CustomAllowance> findAllowanceByEmployeeType(Long org_id,String employeeType);
    
    @Query(nativeQuery = true,value = "select ea.type_of_allowance,ea.percentage,ea.allowance_amount,ea.allowance_id,ca.allowance_name from employee_allowance ea left join custom_allowance ca on ea.allowance_id=ca.id where salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getAllowanceData(Long ids);
}
