package com.arcadesync.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.enums.ServiceContainerEnum;
import com.arcadesync.platform.exception.ApplicationException;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WalletClient {

	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	@Autowired
	private RestTemplate template;

	@Autowired
	private SecurityConfigProps securityProps;

	@Autowired
	private UrlConfig urlConfig;

	@Autowired
	private ObjectMapper mapper;
	
	private static final ServiceContainerEnum container = ServiceContainerEnum.PAYMENT_SERVICE;

	public WalletDTO getWalletDTO(String walletId) {
		if (ObjectUtils.isEmpty(walletId)) {
			return null;
		}
		Set<String> ids = new HashSet<>();
		ids.add(walletId);
		Map<String, WalletDTO> details = getWalletDTO(ids);
		if (details.containsKey(walletId)) {
			return details.get(walletId);
		}
		return null;
	}

	public Map<String, WalletDTO> getWalletDTO(Set<String> walletId) {
		log.debug("fetchig wallet :{}", walletId);
		if (ObjectUtils.isEmpty(walletId)) {
			return Collections.emptyMap();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get(container));
		HttpEntity<Set<String>> entity = new HttpEntity<>(walletId, headers);
		try {
			String url = MessageFormat.format("{0}/pw-service/secure/internal-call/wallet", urlConfig.getBaseUrl());
			log.debug("request for fetchig : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("api response : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, WalletDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to fetch the wallet info.");
		}
	}

	@Data
	private static class WalletRequest {
		private String userId;
		private Long credits;
		private String message;
		private String createdBy;
	}

	public WalletDTO createWallet(WalletOwnerDetails ownerDetails, String actionBy) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get(container));
		HttpEntity<WalletRequest> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/pw-service/secure/internal-call/wallet", urlConfig.getBaseUrl());
			log.debug("request for creating wallet : {} body and headers {}", url, entity);
			ResponseEntity<WalletDTO> response = template.exchange(url, HttpMethod.POST, entity, WalletDTO.class);
			log.info("api response : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to create the wallet.");
		}
	}

	@Data
	public static class WalletDTO {
		private String walletId;
		private Long credits;
	}

	@Data
	private static class CreateWalletRequest {
		private String actionBy;
		private WalletOwnerDetails ownerDetails;
	}

	@Data
	public static class WalletOwnerDetails {
		private String type;
		private String id;
	}

}
