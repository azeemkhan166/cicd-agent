/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.OrganizationIncentive;
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
public interface OrganizationIncentiveRepository extends JpaRepository<OrganizationIncentive, Long> {
    
    @Query(value="Select incentive_min, incentive_max from organization_incentive where organization_id=?1", nativeQuery=true)
    public List<LinkedCaseInsensitiveMap> getIncentive(Long organization_id);
    
    
}
