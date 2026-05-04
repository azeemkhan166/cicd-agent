/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AuthorizatorySetup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface AuthorizatorySetupRepo extends JpaRepository<AuthorizatorySetup, Long>{
    
    @Query(nativeQuery = true,value = "Select * from authorizatory_setup where organization_id=?1")
    public LinkedCaseInsensitiveMap getAuthorizatoryDetails(Long organization_id);
    
    @Query(nativeQuery = true, value = "Select * from authorizatory_setup where organization_id=?1")
    public List<AuthorizatorySetup> findSetUpById(Long organization_id);
    
}
