/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import com.realcoderz.model.AppraisalSalary;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
public interface AppraisalSalaryService {
    
     public Map save(AppraisalSalary map);
     public Map getAppraisalData(LinkedCaseInsensitiveMap map);
     public ResponseEntity<byte[]> downloadAppraisalReport(Long organizationId,HttpServletRequest request); 

}
