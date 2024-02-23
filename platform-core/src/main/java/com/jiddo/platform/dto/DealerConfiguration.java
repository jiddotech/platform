package com.jiddo.platform.dto;

import com.jiddo.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class DealerConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.DEALER;
	private Double profitSharePercentageOnBooking;
}
