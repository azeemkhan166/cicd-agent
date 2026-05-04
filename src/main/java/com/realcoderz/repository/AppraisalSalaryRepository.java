/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AppraisalSalary;
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
public interface AppraisalSalaryRepository extends JpaRepository<AppraisalSalary, Long>{
    
    @Query(nativeQuery = true,value = "select ass.id,DATE_FORMAT(ass.created_date, '%Y-%m-%d') as created_date,ass.effective_date,ass.employer_cost,ass.increment_amount,ass.increment_percent,ass.monthly_gross,ass.previous_annual_ctc,ass.previous_monthly_gross,ass.revised_annual_ctc,ass.variable_part,ed.name,ed.employee_code from appraisal_salary ass Left JOIN employee_details ed ON ass.employee_id=ed.employee_id where ass.organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getAllData(Long orgId);
}
