/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Form16Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mohit
 */
@Repository
public interface Form16DocumentRepository extends JpaRepository<Form16Document, Long>{
    
    @Query(nativeQuery = true,value="Select * from form16document where employee_id=?1 and  financial_year=?2")
    public Optional<Form16Document> findFrom16Document(Long employeeId,Long financialYear);
    
     @Query(nativeQuery = true,value = "select file_url from form16document where employee_id=?1 and  financial_year=?2")
     public String findUrl(Long id,Long year);
}
