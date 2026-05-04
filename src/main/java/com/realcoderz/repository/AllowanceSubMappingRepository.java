/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AllowanceSubMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */

@Repository
public interface AllowanceSubMappingRepository extends JpaRepository<AllowanceSubMapping, Long> {
    
    @Query(nativeQuery=true, value="select allowance_id, allowance_sub_mapping_id from allowance_sub_mapping where organization_id=?1")
     public List<LinkedCaseInsensitiveMap> getSubAllowances(Long organization_id);
    
    @Query(nativeQuery = true,value = "select * from allowance_sub_mapping where organization_id=?1 and allowance_name='overtime'")
    public AllowanceSubMapping getOvertimeOfWorker(Long organization_id); 
            
}
