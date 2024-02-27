package com.jiddo.platform.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiddo.platform.PlatformConstants;
import com.jiddo.platform.notifications.GenericRabbitQueueConfiguration;
import com.jiddo.platform.security.SecurityConfigProps;
import com.jiddo.platform.utility.PlatformCommonService;

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
