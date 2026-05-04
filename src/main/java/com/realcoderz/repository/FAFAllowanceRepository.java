/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.FAFAllowance;
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
public interface FAFAllowanceRepository extends JpaRepository<FAFAllowance, Long>{
    
    
    @Query(nativeQuery = true,value = "SELECT allowance_amount,allowance_name,allowance_payable_amount FROM fafallowance where employee_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedAllowance(Long employee_id);
    
    @Query(nativeQuery = true,value = "select allowance_payable_amount from fafallowance where employee_id=?1 and allowance_name='Total'")
    Double getTotalAllowanceSum(Long employee_id);
    
}
