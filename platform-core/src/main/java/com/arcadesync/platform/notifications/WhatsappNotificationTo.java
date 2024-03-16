package com.arcadesync.platform.notifications;

import lombok.Data;

@Data
public class WhatsappNotificationTo implements NotificationTO {
	private String phone;
	private Integer countryCode;
}
