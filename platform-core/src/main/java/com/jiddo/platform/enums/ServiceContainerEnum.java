package com.jiddo.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceContainerEnum {

	AUTH_SERVICE("auth"), BOOKING_SERVICE("session"), EMPLOYEE_SERVICE("employee"), TICKET_SERVICE("ticket"),
	HARDWARE_SERVICE("hardware"), ASSET_SERVICE("asset"), GAME_SERVICE("game"), STORE_SERVICE("store"),
	GAMER_SERVICE("gamer"), PROMOTION_SERVICE("promotion"), PAYMENT_SERVICE("payment"), COMMON_SERVICE("common"),
	NOTIFICATION_SERVICE("notification"), UD_SERVICE("ud");

	private String contextPath;

}
