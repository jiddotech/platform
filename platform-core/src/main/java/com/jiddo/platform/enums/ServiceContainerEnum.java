package com.jiddo.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceContainerEnum {

	AUTH_SERVICE("/auth"), BOOKING_SERVICE("/session"), USER_SERVICE("/user"), TICKET_SERVICE("/ticket"),
	HARDWARE_SERVICE("/hardware"), GAME_SERVICE("/game"), STORE_SERVICE("/store");

	private String contextPath;

}
