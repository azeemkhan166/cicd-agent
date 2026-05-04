/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.FAFOtherEarning;
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
public interface FAFOtherEarningRepository  extends JpaRepository<FAFOtherEarning, Long>{
    
    @Query(nativeQuery = true,value = "SELECT amount,days,name FROM fafother_earning where employee_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedOtherEarning(Long employee_id);
    
    @Query(nativeQuery = true,value = "select amount from fafother_earning where employee_id=?1 and name='Incentives if any'")
    Double getIncentive(Long employee_id);
    
}
