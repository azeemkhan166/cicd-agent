/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface SiteService {
    
     public Map save(Map map);
     
     public Map getAllSite(Map map);
     
     public Map findById(Map map);
     
     public Map deleteById(Map map);
     
     public Map uploadSite(MultipartFile file, String fileName, Long declarationId, Long organizationId,String siteName,String complianceName,String validTillDate);

     public Map fetchDocument(Map map);
  
    
}
