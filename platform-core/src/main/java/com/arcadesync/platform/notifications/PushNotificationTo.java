package com.arcadesync.platform.notifications;

import lombok.Data;

@Data
public class PushNotificationTo implements NotificationTO {
	private String fcmToken;
}
