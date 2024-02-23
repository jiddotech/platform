package com.jiddo.platform.dto;

import com.jiddo.platform.enums.ChargerType;

import lombok.Data;

@Data
public class OrderChargerDetail {
	private ChargerType chargerType;
	private Integer noOfChargers;
	private ChargerAccessories accessories;

	@Data
	public static class ChargerAccessories {
		private SunboardTypeEnum sunboard;
		private Boolean stand;
		private Boolean canopy;
	}

	public enum SunboardTypeEnum {
		SQUARE_SUNBOARD, RECTANGLE_SUNBOARD, CUSTOM_SUNBOARD;
	}

}
