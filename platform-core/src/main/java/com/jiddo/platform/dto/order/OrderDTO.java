package com.jiddo.platform.dto.order;

import java.time.Instant;
import java.util.List;

import com.jiddo.platform.dto.MarketingMaterial;
import com.jiddo.platform.dto.OrderChargerDetail;
import com.jiddo.platform.dto.UserDetails;
import com.jiddo.platform.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDTO {
	private String orderId;
	private UserDetails contactDetails;
	private OrderData orderData;
	private List<OrderChargerDetail> chargerDetails;
	private MarketingMaterial marketingMaterial;
	private OrderStatus status;
	private Instant createdAt;
}
