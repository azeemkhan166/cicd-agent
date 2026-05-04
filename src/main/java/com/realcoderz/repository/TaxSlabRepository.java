/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.TaxSlab;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 * edited By Astha
 */
public interface TaxSlabRepository extends JpaRepository<TaxSlab, Long> {

//    @Query(nativeQuery = true, value = "Select * from tax_slab where organization_id=?1")
    
    @Query(nativeQuery = true, value = "Select * from tax_slab")
    public List<TaxSlab> findTaxSlab();
   
//     @Query(nativeQuery = true, value = "Select * from tax_slab where organization_id=?1 and start_age <=?2 and  end_age >=?3 ORDER BY start")
 
    @Query(nativeQuery = true, value = "Select * from tax_slab where start_age <=?1 and  end_age >=?2 ORDER BY start")
    public List<TaxSlab> findTaxSlab_byAge(int start_age,int end_age);
    
    @Query(nativeQuery = true, value ="select slab_id ,start,end,rate from tax_slab")
     public List<TaxSlab> getAllTaxSlabs();
     
     @Query(nativeQuery=true,value="SELECT rate,start,end_age,end,start_age FROM tax_slab where start_age=1 and end_age=60;")
     public List<LinkedCaseInsensitiveMap> oldTaxSlab();

}
