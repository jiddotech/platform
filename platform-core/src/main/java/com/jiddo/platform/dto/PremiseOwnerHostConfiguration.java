package com.jiddo.platform.dto;

import com.jiddo.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class PremiseOwnerHostConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.PREMISE_OWNER;

	@Override
	public Double getProfitSharePercentageOnBooking() {
		return null;
	}
}
