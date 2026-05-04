/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeGratuity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tause
 */
@Repository
public interface EmployeeGratuityRepo extends JpaRepository<EmployeeGratuity, Long>
{
    @Query(value="Select * from employee_gratuity where salary_breakup_id=?;",nativeQuery = true)
    public EmployeeGratuity gratuityBySalaryBreakup(Long salaryBreaupId);
}
