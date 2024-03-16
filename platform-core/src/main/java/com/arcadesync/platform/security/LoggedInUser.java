package com.arcadesync.platform.security;

import java.time.ZoneId;
import java.util.List;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.enums.LogInFrom;

import lombok.Data;

@Data
class LoggedInUser implements UserDTO {

	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;
	// this fields is equivalent to login from.
	private LogInFrom logInFrom;

	@Override
	public ZoneId getZoneId() {
		return ZoneId.of(PlatformConstants.CURRENT_TIME_ZONE);
	}

}
