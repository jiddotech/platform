package com.jiddo.platform.dto;

import java.io.Serializable;

import com.jiddo.platform.enums.ChargerUsageType;

import lombok.Data;

@Data
public class PublicChargerUsageConfiguration implements ChargerUsageTypeConfiguration, Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8621268082263284456L;
	private ChargerUsageType usageType = ChargerUsageType.PUBLIC;

}
