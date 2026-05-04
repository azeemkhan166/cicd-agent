/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.ComplianceDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface ComplianceDocumentRepository extends  JpaRepository<ComplianceDocument, Long>{
    
         //    Fetch All Allowances By Org Id
    @Query(nativeQuery = true, value = "Select * from compliance_document where site_id=?1")
    public List<ComplianceDocument> findDocumentById(Long id);
    
    @Query(nativeQuery = true,value = "select url from compliance_document where id=?1")
    public String findUrl(Long id);
}
