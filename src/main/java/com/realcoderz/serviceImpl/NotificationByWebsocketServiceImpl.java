/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.service.NotificationByWebsocketService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
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
 * @author mohit
 */
@Service
public class NotificationByWebsocketServiceImpl implements NotificationByWebsocketService{
    
    public static  final Logger logger=LoggerFactory.getLogger(NotificationByWebsocketServiceImpl.class);

    
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${notification_url}")
    private String notification_url;
    
   @Override
	public Map sendNotification(NotificationByWebsocket map) {
       Map resultMap = new HashMap();
	try {
                        
            System.out.println("into send notification");
            System.out.println(map);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
           Map json = new HashMap();
           json.put("priority",map.getPriority());
           json.put("type",map.getType());
           json.put("push_notify",map.getPushNotify());
           json.put("mail_notify",map.getMailNotify());
           json.put("module_name",map.getModuleName());
           json.put("organization_id",map.getOrganizationId());
           json.put("sender_id",map.getSenderId());
           json.put("sender_name",map.getSenderName());
           json.put("title",map.getTitle());
           json.put("body",map.getBody());
           json.put("right_url",map.getRightUrl());
           if(map.getEmployeeCodes() !=null)
           {
               json.put("employee_codes", map.getEmployeeCodes());
           }else
           {
               json.put("recipient_emails",map.getRecipientEmails());
           }           
           json.put("recipient_names",map.getRecipientNames());
          logger.info("Json data for notification" +json);
          HttpEntity entity = new HttpEntity(json, headers);
          logger.info("NotificationByWebsocketServiceImpl :: sentNotify() => Request for Notification sent with data -> " + json);
          

            LinkedCaseInsensitiveMap body = restTemplate.exchange(notification_url +"/notify",
                                            HttpMethod.POST, entity, LinkedCaseInsensitiveMap.class).getBody();

              
              logger.info("response from notification api" +body);
              
        
            Map response1 = mapper.readValue(EncryptDecryptUtils.decrypt(body.get("data").toString()), Map.class);
            System.out.println("response" +response1);
            logger.info("NotificationByWebsocketServiceImpl :: sentNotify() => Response from Notification send API -> " + response1);
             if (response1.get("status").equals("success")) {
                resultMap.put("status", "success");
                resultMap.put("msg", "Notification sent -> ");
                logger.info("NotificationByWebsocketServiceImpl :: sentNotify() :: Notification sent");
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Something went wrong -> ");
                logger.info("Problem in NotificationByWebsocketServiceImpl :: sentNotify() :: Something went wrong " + response1);
            }

			
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", "exception");
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
    }
    
}
