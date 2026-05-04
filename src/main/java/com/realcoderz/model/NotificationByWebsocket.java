/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author Admin
 */
@Data
public class NotificationByWebsocket {
    
      private String priority;
        private String type;
        private long push_notify;
        private long mail_notify;
        private String module_name;
        private long organization_id;
        private long sender_id;
        private String sender_name;
        private String title;
        private String body;
        private List<String> recipient_emails;
        private List<String> recipient_names;
        private List<String> employee_codes;
        private List<String> right_url;
        private String fcmToken;
        private List<Map> data;
        private String notify_action_type;
        private long employeeId;
        private String employeeName;
        
        
        public String getFcmToken() {
			return fcmToken;
		}
		public void setFcmToken(String fcmToken) {
			this.fcmToken = fcmToken;
		}
		public List<Map> getData() {
			return data;
		}
		public void setData(List<Map> data) {
			this.data = data;
		}
		public List<String> getRightUrl() {
		return right_url;
	}
	public void setRightUrl(List<String> right_url) {
		this.right_url = right_url;
        }
        public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
        public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
        public long getPushNotify() {
		return push_notify;
	}
	public void setPushNotify(long push_notify) {
		this.push_notify = push_notify;
	}
        public long getMailNotify() {
		return mail_notify;
	}
	public void setMailNotify(long mail_notify) {
		this.mail_notify = mail_notify;
	}
        public String getModuleName() {
		return module_name;
	}
	public void setModuleName(String module_name) {
		this.module_name = module_name;
	}
        public long getOrganizationId() {
		return organization_id;
	}
	public void setOrganizationId(long organization_id) {
		this.organization_id = organization_id;
	}
        public long getSenderId() {
		return sender_id;
	}
	public void setSenderId(long sender_id) {
		this.sender_id = sender_id;
	}
        public String getSenderName() {
		return sender_name;
	}
	public void setSenderName(String sender_name) {
		this.sender_name = sender_name;
	}
        public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
        public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
        public List<String> getRecipientEmails() {
		return recipient_emails;
	}
	public void setRecipientEmails(List<String> recipient_emails) {
		this.recipient_emails = recipient_emails;
	}
        public List<String> getRecipientNames() {
		return recipient_names;
	}
	public void setRecipientNames(List<String> recipient_names) {
		this.recipient_names = recipient_names;
	}
        public List<String> getEmployeeCodes() {
		return employee_codes;
	}
	public void setEmployeeCodes(List<String> employee_codes) {
		this.employee_codes = employee_codes;
	}
    
}
