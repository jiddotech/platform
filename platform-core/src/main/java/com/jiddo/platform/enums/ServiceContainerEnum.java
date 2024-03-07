package com.jiddo.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceContainerEnum {

	AUTH_SERVICE("auth-svc"), BOOKING_SERVICE("session-svc"), USER_SERVICE("user-svc"),
	TICKET_SERVICE("ticket"), HARDWARE_SERVICE("hardware-svc"), ASSET_SERVICE("asset-svc"), GAME_SERVICE("game-svc"),
	CAFE_SERVICE("cafe-svc"), GAMER_SERVICE("gamer-svc"), PROMOTION_SERVICE("promotion-svc"),
	PAYMENT_SERVICE("payment-svc"), COMMON_SERVICE("common-svc"), NOTIFICATION_SERVICE("notification-svc"),
	UD_SERVICE("ud-svc");

	private String contextPath;

}
