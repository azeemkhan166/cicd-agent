/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;


import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author tauseef
 */
public interface DetailsTaxSlipService {
    
     public @ResponseBody byte[] getDetailSalarySlip(HttpServletRequest request,String data); 

     public @ResponseBody byte[] generateGateSalaryReport(HttpServletRequest request,String data); 

     public @ResponseBody byte[] generateMonthlySlip(HttpServletRequest request,String data); 
     
     public @ResponseBody byte[] generateGateSalarySaralReport(HttpServletRequest request,String data); 
         
}
