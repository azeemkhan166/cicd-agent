/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.StandardDeduction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Repository
public interface StandardDeductionRepository extends JpaRepository<StandardDeduction, Long>{
    
     
   @Query(nativeQuery = true, value = "Select * from standard_deduction")
    public List<StandardDeduction> getstandDeduction();   
    
    @Query(nativeQuery = true, value = "Select * from standard_deduction")
    public LinkedCaseInsensitiveMap getstandDeductionAmount();
    
    @Query(nativeQuery = true,value = "SELECT * FROM standard_deduction where financial_year=?")
    public List<StandardDeduction> getStandardDeductionFinancialYear(int financialYear);
    
    @Query(nativeQuery = true,value = "SELECT * FROM standard_deduction where financial_year=? and type_of_regime=?")
    public StandardDeduction getStandardDeductionAccordingtoType(int financialYear,String slabType);
    
     }

