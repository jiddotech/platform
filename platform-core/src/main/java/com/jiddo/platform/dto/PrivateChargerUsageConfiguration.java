package com.jiddo.platform.dto;

import java.io.Serializable;

import com.jiddo.platform.enums.ChargerUsageType;

import lombok.Data;

@Data
public class PrivateChargerUsageConfiguration implements ChargerUsageTypeConfiguration, Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -867564263284456L;
	private ChargerUsageType usageType = ChargerUsageType.PRIVATE;

}
