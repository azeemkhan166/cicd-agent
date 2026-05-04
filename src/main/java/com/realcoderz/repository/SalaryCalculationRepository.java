/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.SalaryCalculation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mayank
 */
@Repository
public interface SalaryCalculationRepository extends JpaRepository<SalaryCalculation, Long> {

    //    Fetch SalaryCalculation By Employee and Org Id
    @Query(nativeQuery = true, value = "Select * from salarycalculation where organization_id=?1 and employee_id=?2")
    public List<SalaryCalculation> findSalaryCalcutaion(Long emp_id, Long org_id);

}
