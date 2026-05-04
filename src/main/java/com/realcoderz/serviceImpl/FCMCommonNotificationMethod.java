/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Admin
 */
@Service
public class FCMCommonNotificationMethod {
    
     private static final Logger logger = LoggerFactory.getLogger(FCMCommonNotificationMethod.class);
     
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${notification_url}")
    private String notification_url;
    
    @Value("${apiGateWayUrl}")
    private String apiGateWayUrl;
    
      	public void sendCommonFCMNotification(NotificationByWebsocket map) {
    	System.out.println("in sendFCMNotification");
    	String recipientName = map.getRecipientNames() != null && map.getRecipientNames().get(0) != null ? map.getRecipientNames().get(0) : null;
        try {
              HttpHeaders headers = new HttpHeaders();
              headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
              //headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InF1YW50dXNwcm9AZ21haWwuY29tIiwiZW1wbG95ZWVJZCI6MzUyOSwib3JnYW5pemF0aW9uSWQiOjMwOCwiaWQiOjQ2NDMsIm9yZ2FuaXphdGlvbk5hbWUiOiJEZXZxdWFudHVzcHJvIiwib3JnTGluayI6ImRldnF1YW50dXNwcm8ucmN0ZWFtYnVpbGRlci5jb20iLCJpYXQiOjE3MDg1MDgxNzEsImV4cCI6MTcwOTM3MjE3MX0._9nAzsDbskzMMyzhaGsrQ-06xqI61Pzh79M4oN8fD3E");
              //headers.set("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InF1YW50dXNwcm9AZ21haWwuY29tIiwiZW1wbG95ZWVJZCI6MzUyOSwib3JnYW5pemF0aW9uSWQiOjMwOCwiaWQiOjQ2NDMsIm9yZ2FuaXphdGlvbk5hbWUiOiJEZXZxdWFudHVzcHJvIiwib3JnTGluayI6ImRldnF1YW50dXNwcm8ucmN0ZWFtYnVpbGRlci5jb20iLCJpYXQiOjE3MDg1MDgxNzEsImV4cCI6MTcwOTM3MjE3MX0._9nAzsDbskzMMyzhaGsrQ-06xqI61Pzh79M4oN8fD3E");
              JSONObject json = new JSONObject();
              json.put("priority",map.getPriority());
              json.put("type",map.getType());
              json.put("push_notify",String.valueOf(map.getPushNotify()));
              json.put("mail_notify",String.valueOf(map.getMailNotify()));
              json.put("module_name",map.getModuleName());
              json.put("organization_id",String.valueOf(map.getOrganizationId()));
              json.put("sender_name",map.getSenderName());
              json.put("fcm_token", "null");
              json.put("body", map.getBody());
              json.put("title", map.getTitle());
              json.put("recipient_email", map.getRecipientEmails().toString());
              json.put("recipient_name", map.getRecipientNames().toString());
              json.put("sender_id", String.valueOf(map.getSenderId()));
              json.put("right_url",map.getRightUrl().toString());
              json.put("notify_action_type",map.getNotify_action_type());
              json.put("employeeId",String.valueOf(map.getEmployeeId()));
              json.put("employeeName",map.getEmployeeName());
              
              logger.info("Payload -> "+json);
              System.out.println(json);
              
             String Payload= EncryptDecryptUtils.encrypt(json.toString());
             System.out.println("Payload");
             System.out.println(Payload);
             JSONObject payloads=new JSONObject();
             payloads.put("data", Payload);
             System.out.println(payloads);
             System.out.println(notification_url + "/notify_app");
             
              HttpEntity entity = new HttpEntity(payloads, headers);
                
             logger.info("notification json for " + recipientName + " is " + json);
             LinkedCaseInsensitiveMap body = restTemplate.exchange(notification_url + "/notify_app",HttpMethod.POST, entity, LinkedCaseInsensitiveMap.class).getBody();
             
              Map response = mapper.readValue(EncryptDecryptUtils.decrypt(body.get("data").toString()), Map.class);           
              logger.info("Response-> "+response);
              logger.info("response from notification api for" + recipientName + " is " +response);
              logger.info("Status -> "+response.get("status"));
   		} catch (Exception e) {
   			logger.info("Exception in sendFCMNotification -> " + e.getMessage());
   			e.printStackTrace();
   		}
    }
        
    
        public List<Map> getCommonFcmToken(Long empId) {
            
            Set<Long> employeeId = new HashSet<Long> ();
            employeeId.add(empId);
		logger.info("In getFcmToken API for employeeId - " + employeeId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		JSONObject json = new JSONObject();

		try {
			json.put("employeeId", employeeId);
			HttpEntity<String> entity = new HttpEntity(EncryptDecryptUtils.encrypt(json.toJSONString()), headers);
			LinkedCaseInsensitiveMap body = restTemplate.exchange(apiGateWayUrl+ "/user/getFcmid",
					HttpMethod.POST, entity, LinkedCaseInsensitiveMap.class).getBody();
			Map response = mapper.readValue(EncryptDecryptUtils.decrypt(body.get("data").toString()), HashMap.class);                        
                        logger.info("Response for employeeId - " + employeeId + " is -> " +  response);
			if (response.get("status").toString().equals("success")) {
				return (List<Map>) response.get("userData");
			}else{
				System.out.println("response of  getFcmToken API (Other than success)-> " + response);
			}

		} catch (Exception e) {
			logger.info("Exception in getFcmToken API "+e.getMessage());
			e.printStackTrace();
		}
		return null;
		
	}
    
}
