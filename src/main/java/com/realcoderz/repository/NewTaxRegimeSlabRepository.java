/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.NewTaxRegimeSlab;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import javax.transaction.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author lalit Raghav
 */
@Repository
public interface NewTaxRegimeSlabRepository extends JpaRepository<NewTaxRegimeSlab,Long>{
    
   @Query(nativeQuery = true, value = "Select * from new_tax_regime_slab")
    public List<NewTaxRegimeSlab> findNewTaxSlab(); 

    @Query(nativeQuery = true, value = "Select * from new_tax_regime_slab where start_age <=?1 and  end_age >=?2 and financial_year=?3  ORDER BY start")
    public List<NewTaxRegimeSlab> findNewTaxSlab_byAge(int start_age,int end_age ,String year);
    
    @Query(nativeQuery = true, value = "Select * from new_tax_regime_slab")
    public List<NewTaxRegimeSlab> newfindTaxSlab();
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from new_tax_regime_slab where new_tax_regime_slab_id=?")
    public void deleteNewTaxRegimeSlab(Long id);
    
    @Query(nativeQuery = true,value="Select rate,start_age,end_age,start,end from new_tax_regime_slab where financial_year=? and start_age = 1 and (end_age = 60 or end_age=100);")
    public List<LinkedCaseInsensitiveMap> newTaxSlab(int financialYear);
}
