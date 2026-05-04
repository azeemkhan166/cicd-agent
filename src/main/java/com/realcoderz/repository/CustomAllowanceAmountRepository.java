/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.CustomAllowanceAmount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface CustomAllowanceAmountRepository extends JpaRepository<CustomAllowanceAmount, Long>{
    
    @Query(value = "select * from custom_allowance_amount where organization_id=?1 and month=?2 and year=?3 and site_id=?4",nativeQuery = true)
    public List<CustomAllowanceAmount> getAllowanceMonthWise(Long orgId,Integer month,Integer year,Long siteId  );
}
