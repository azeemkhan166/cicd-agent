/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.DeductionAllowanceMapping;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */

@Repository
public interface DeductionAllowanceMappingRepository extends JpaRepository<DeductionAllowanceMapping, Long> {
    
    @Transactional
    @Modifying
    @Query(nativeQuery=true, value="Delete from deduction_allowance_mapping where deduction_id=?1")
    public void deleteDeduction(Long deduction_id);
    
    @Query(nativeQuery=true, value="select d.allowance_id, d.deduction_id, a.amount, a.percentage, a.allowance_name from deduction_allowance_mapping d, allowance a where deduction_id in ?1 and a.allowance_id=d.allowance_id")
    public List<LinkedCaseInsensitiveMap> getAllowancesForDeduction (List<Long> deduction_id);
    
}
