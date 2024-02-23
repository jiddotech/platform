package com.jiddo.platform.dto;

import com.jiddo.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class NetworkBuyerWithWhiteLabelAppConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.NETWORK_BUYER_WITH_WHITELABEL_APP_IP;
	private Double charzerShare;

	@Override
	public Double getProfitSharePercentageOnBooking() {
		return null;
	}
}
