package com.jiddo.platform.security;

import java.time.ZoneId;
import java.util.List;

import com.jiddo.platform.PlatformConstants;
import com.jiddo.platform.enums.LogInFrom;
import com.jiddo.platform.enums.OperatingSystem;

import lombok.Data;

@Data
class LoggedInUser implements UserDTO {

	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;
	// this fields is equivalent to login from.
	private LogInFrom logInFrom;
	private String chargePointOperatorId;
	private OperatingSystem operatingSystem;

	@Override
	public ZoneId getZoneId() {
		return ZoneId.of(PlatformConstants.CURRENT_TIME_ZONE);
	}

}
