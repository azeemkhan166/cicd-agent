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

public interface DeductionLoanService {
    
     public Map saveDeductionLoan(Map map);
     
     public Map getAllDeductionLoan(Long org_id,String searchString);
     
     public Map getAllDeductionoanOfEmployee(Long empId);
     
     public Map approvedOrRejectDeductionLoan(Map map);
     
     public Map loanAdjustment(Map map);
     
     public ResponseEntity<byte[]> downloadDeductionLoanData(Long organizationId,HttpServletRequest request); 
            
}
