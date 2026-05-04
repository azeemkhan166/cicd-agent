/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.FAFNetPayable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author sharm
 */
@Repository
public interface FAFNetPayableRepository extends JpaRepository<FAFNetPayable, Long>{
    
    @Query(nativeQuery = true,value = "SELECT * FROM fafnet_payable where employee_id=?1")
    public LinkedCaseInsensitiveMap getSavedNetPay(Long employee_id);
}
