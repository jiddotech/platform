package com.arcadesync.platform.clients;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.arcadesync.platform.PlatformConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.URL_CONFIG_PREFIX)
public class UrlConfig {
	private String baseUrl;
	private String websiteUrl;
}
