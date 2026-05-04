/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Admin
 */
public interface AdvanceRcmService {
    
       public Map getAllAdvanceForSupervisor(Map map,HttpServletRequest request);
       public Map saveOrUpdateStatus(Map map);
       public Map getAllAdvanceForRcm(Map map);
       public Map rcmApprovedOrReject(Map map);
       public Map hoApprovedOrReject(Map map);
       public ResponseEntity<byte[]> downloadAdvanceDetailsInexcelFormate(String siteName,Long organizationId,String month,Long year, Long ids,HttpServletRequest request); 
       public Map getDateFormAdvance(Map map);
       public Map updateStatusInAdvance(Map map);
       
       
}
