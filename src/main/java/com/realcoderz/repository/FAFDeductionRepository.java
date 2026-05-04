/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.FAFDeduction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author sharm
 */
@Repository
public interface FAFDeductionRepository extends JpaRepository<FAFDeduction, Long>{
    
    
    @Query(nativeQuery = true,value = "SELECT deduction_name,deduction_payable_amount FROM fafdeduction where employee_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedDeduction(Long employee_id);
    
    @Query(nativeQuery = true,value = "select deduction_payable_amount from fafdeduction where employee_id=?1 and deduction_name='Professional Tax'")
    Double getPT(Long employee_id);
    
    @Query(nativeQuery = true,value = "select deduction_payable_amount from fafdeduction where employee_id=?1 and deduction_name='Income Tax'")
    Double getIncomeTax(Long employee_id);
    
    @Query(nativeQuery = true,value = "select hra_amount from fafnet_payable where employee_id=?1")
    Double getHraAmount(Long employee_id);
}
