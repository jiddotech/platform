package com.arcadesync.platform.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.enums.ServiceContainerEnum;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.SECURITY_CONFIG_PREFIX)
public class SecurityConfigProps {

	private String jwtSecretKey;
	private List<String> testingRoles = new ArrayList<>();
	private Map<ServiceContainerEnum, String> creds = new HashMap<>();

}
