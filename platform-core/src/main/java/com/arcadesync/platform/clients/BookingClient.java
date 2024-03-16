package com.arcadesync.platform.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.notifications.GenericRabbitQueueConfiguration;
import com.arcadesync.platform.security.SecurityConfigProps;
import com.arcadesync.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingClient {

	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	@Autowired
	private RestTemplate template;
	@Autowired
	private SecurityConfigProps securityProps;
	@Autowired
	private UrlConfig urlConfig;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private PlatformCommonService commonService;
	@Autowired
	private CustomRabbitMQTemplate rabbitMqTemplate;
	@Autowired
	private GenericRabbitQueueConfiguration rabbitQueConfiguration;

}
