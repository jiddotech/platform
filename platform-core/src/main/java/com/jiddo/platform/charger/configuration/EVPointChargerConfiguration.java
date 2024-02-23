package com.jiddo.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.jiddo.platform.exception.PlatformExceptionCodes;
import com.jiddo.platform.exception.ValidationException;

import lombok.Data;

@Data
public class EVPointChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * ccuId mqttBrokerKey
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String deviceId;
	private String bleMacAddress;

	@Override
	public String getChargerControlId() {
		return deviceId;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(deviceId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid deviceId");
		}
		if (ObjectUtils.isEmpty(bleMacAddress)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid bleMacAddress");
		}
	}
}
