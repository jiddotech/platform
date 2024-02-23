package com.jiddo.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SocketDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 42362998463993412L;
	private String id;
	private String connectorId;
	private String socketType;
	private Long pricePerUnit;
	private Double power;
}
