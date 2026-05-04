/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author tause
 */
@Service
public class CommonMail {
    
     public static  final Logger logger=LoggerFactory.getLogger(CommonMail.class);

    @Value("${recruit_base_url}")
    private String recruit_base_url;

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    public CommonMail(ObjectMapper mapper, RestTemplate restTemplate,
                      @Value("${recruit_base_url}") String recruit_base_url) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.recruit_base_url = recruit_base_url;
    }

    public Map commonMail(Map mp){
        Map resultMap= new HashMap();
        try{
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", mp.get("token").toString());
                headers.setContentType(MediaType.TEXT_PLAIN);

                 JSONObject map = new JSONObject();
                map.put("organizationId",mp.get("OrganizationId").toString());
                map.put("sendTo",mp.get("email"));
                map.put("subject", mp.get("subject"));
                map.put("message", mp.get("message"));
                System.out.println(map);
                System.out.println(mp.get("token").toString());
                HttpEntity entity = new HttpEntity(EncryptDecryptUtils.encrypt(map.toString()),headers);
                Map postForObject = restTemplate.exchange(recruit_base_url + "/orgMailer/send", HttpMethod.POST, entity, HashMap.class).getBody();
                System.out.println("postFoObject"+" "+postForObject.toString());
                Map emails = mapper.readValue(EncryptDecryptUtils.decrypt(postForObject.get("data").toString()), Map.class);
                if(emails.get("status").toString().equalsIgnoreCase("success")){
                    logger.info("Mail sent successfully to "+mp.get("email"));
                    resultMap.put("message", "Mail sent successfully!");
                    resultMap.put("status","success" );
                    System.out.println("mail send successfully");
                }
                else{
                    logger.info("Mail not sent to "+mp.get("email"));
                    resultMap.put("message", "Mail not sent successfully!");
                    resultMap.put("status","error" );
                     System.out.println("mail not send successfully");
                }
                }catch(Exception e){   
                    resultMap.put("status","exception" );
                    logger.error("Exception occured while sending thank you mail with message {} ::",e);
                     System.out.println("Exception");
                }  
        
        return resultMap;
    }
    
}
