/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Relief87A;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 * edited By Astha
 */
public interface Relief87ARepository extends JpaRepository<Relief87A, Long> {

    @Query(nativeQuery = true, value = "Select * from relief_87a")
    public List<Relief87A> findRelief87A();
    
    @Query(nativeQuery = true, value = "Select income,rate,relief_87a_id from relief_87a")
    public LinkedCaseInsensitiveMap getIncomeOfRelief87A();
    
    @Query(nativeQuery = true, value = "Select * from relief_87a")
    public Relief87A findRelief87AOfOldRegime();

}
