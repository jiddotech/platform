package com.arcadesync.platform.notifications;

import lombok.Data;

@Data
public class SMSNotificationTo implements NotificationTO {
	private String mobileNumber;
	private Integer countryCode;
}
