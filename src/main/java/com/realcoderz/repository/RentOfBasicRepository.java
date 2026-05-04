/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.RentOFBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;


/**
 *
 * @author lalit
 * edited By Astha
 */
@Repository
public interface RentOfBasicRepository extends JpaRepository<RentOFBasic, Long>{
    
    @Query(nativeQuery = true, value ="SELECT  r.basic_percentage FROM rentofbasic r")
     public LinkedCaseInsensitiveMap getBasicPercentageDataById();
     
     @Query(nativeQuery = true, value ="Select rent_Of_Basic_id,basic_percentage from rentofbasic")
     public LinkedCaseInsensitiveMap getBasicRent();
}
