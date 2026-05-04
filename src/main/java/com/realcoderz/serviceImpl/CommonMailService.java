/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.util.CommonMail;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author tause
 */
@Service
@RequiredArgsConstructor
public class CommonMailService {

    private final CommonMail commonmail;
     
        public void sendCommonMail(String OrganizationId,String token ,String email,String subject,String message) {
            Map maildata=new HashMap();
//        String token=BearerTokenUtil.getBearerTokenHeader();
        maildata.put("token", token);
         System.out.println(email+" inside CommonMailService"+" "+message);
        maildata.put("OrganizationId" ,OrganizationId);
        maildata.put("email", email);
        maildata.put("subject", subject);
        maildata.put("message", message);
        
         try {
             commonmail.commonMail(maildata);            
        } catch (Exception e) {
            System.out.println(e.getMessage());
             System.out.println("exception");
        }

    }
    
}