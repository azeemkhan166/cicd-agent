/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import com.realcoderz.model.NotificationByWebsocket;
import java.util.Map;

/**
 *
 * @author mohit
 */
public interface NotificationByWebsocketService {
    
    Map sendNotification(NotificationByWebsocket notificationByWebsocket);
    
}
