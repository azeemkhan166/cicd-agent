/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.RentAmountApproved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Bipul Singh
 */
@Repository
public interface RentAmountApprovedRepository extends JpaRepository<RentAmountApproved, Long> {
    
    @Query(nativeQuery = true, value ="Select * from rent_amount_approved where rent_amount_id=?1")
    public LinkedCaseInsensitiveMap findRentAmountById(Long rent_amount_id);
    
}
