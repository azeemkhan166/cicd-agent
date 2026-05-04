/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.model.NotificationByWebsocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class FCMCommonNotification {
    
    private static final Logger logger = LoggerFactory.getLogger(FCMCommonNotification.class);
    
    @Autowired
    private FCMCommonNotificationMethod notificationMethod;
    
    public void fcmcommonNotification(Long organizationId, Long recipientId, List<String> empMail, List<String> empName, String title, String body, String url, String serviceName,String employeeName) {

        try {
            List<String> listOfEmpCode = new ArrayList<>();
            List<String> RecipientEmails = new ArrayList<>();
            List<String> RecipientNames = new ArrayList<>();
            List<String> right_url = new ArrayList<>();
            right_url.add(url);
            System.out.println(right_url);
            RecipientNames.addAll(empName);
            RecipientEmails.addAll(empMail);
            List<Map> fcmcode = notificationMethod.getCommonFcmToken(recipientId);
           
            logger.info("Response from fcmcommonNotification class ,fcmcode and status->" + fcmcode );

            NotificationByWebsocket notification = new NotificationByWebsocket();
            notification.setPriority("MODERATE");
            notification.setType("INFO");
            notification.setPushNotify(1);
            notification.setMailNotify(0);
            notification.setModuleName("Payroll");
            notification.setOrganizationId(organizationId);
            notification.setSenderId(recipientId);
            notification.setSenderName("Admin");
            notification.setTitle(title);
            notification.setBody(body);
            notification.setRightUrl(right_url);
            notification.setNotify_action_type(serviceName);
            notification.setEmployeeId(recipientId);
            notification.setEmployeeName(employeeName);
          
            if (!listOfEmpCode.isEmpty()) {
                notification.setEmployeeCodes(listOfEmpCode);
            } else {
                notification.setRecipientEmails(RecipientEmails);
            }
            notification.setRecipientNames(RecipientNames);
          
                if (fcmcode.get(0).get("fcm_id") != null || fcmcode.get(0).get("fcm_id")!=" " ) {
                    notification.setFcmToken(fcmcode.get(0).get("fcm_id") != null ? fcmcode.get(0).get("fcm_id").toString() : null);
                } else {
                    notification.setFcmToken(null);
                }

            notificationMethod.sendCommonFCMNotification(notification);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
}
