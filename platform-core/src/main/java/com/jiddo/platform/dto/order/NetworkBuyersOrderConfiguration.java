package com.jiddo.platform.dto.order;

import com.jiddo.platform.dto.AddressDTOV2;

import lombok.Data;

@Data
public class NetworkBuyersOrderConfiguration implements OrderConfiguration {
	private AddressDTOV2 address;
	private String bussinessName;
	private Double networkBuyerShare;
}
