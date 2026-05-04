/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Skilled;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface SkilledRepository extends JpaRepository<Skilled, Long>{
    
    @Query(nativeQuery = true, value = "Select * from skilled where organization_id=?1")
    public List<Skilled> findSkilledById(Long org_id);
}
