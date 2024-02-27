package com.jiddo.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceContainerEnum {

	AUTH_SERVICE("auth_service", "/auth"), BOOKING_SERVICE("session_service", "/session"),
	USER_SERVICE("user_service", "/user"), TICKET_SERVICE("ticket_service", "/ticket"),
	HARDWARE_SERVICE("hardware_service", "/hardware"), GAME("game_service", "/game"),
	STORE_SERVICE("store_service", "/store");

	private String serviceName;
	private String contextPath;

}
