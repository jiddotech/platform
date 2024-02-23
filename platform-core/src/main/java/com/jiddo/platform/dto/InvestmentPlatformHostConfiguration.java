package com.jiddo.platform.dto;

import com.jiddo.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class InvestmentPlatformHostConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.INVESTMENT_PLATFORM;
	private Double profitSharePercentageOnBooking;
}
