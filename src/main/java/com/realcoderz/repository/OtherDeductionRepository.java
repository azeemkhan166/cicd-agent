/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.OtherDeduction;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
public interface OtherDeductionRepository extends JpaRepository<OtherDeduction, Long> {

    @Query(nativeQuery = true, value = "select amount as amount, deduction_name as deduction_name from other_deduction  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> fetchDataInPdf(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = " select deduction_name as deduction_name, amount from other_deduction  where employee_id in (?1) and organization_id in (?2) and month in (?3) and year in (?4)")
    public List<LinkedCaseInsensitiveMap> fetchIncomeTax(Long employeeid, Long organization_id, int month, int year);
    
}
