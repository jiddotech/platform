package com.jiddo.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiddo.platform.PlatformConstants;
import com.jiddo.platform.dto.UserDetails;
import com.jiddo.platform.enums.ServiceContainerEnum;
import com.jiddo.platform.exception.ApplicationException;
import com.jiddo.platform.exception.PlatformExceptionCodes;
import com.jiddo.platform.exception.ValidationException;
import com.jiddo.platform.security.SecurityConfigProps;
import com.jiddo.platform.utility.PlatformCommonService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserClient {

	private RestTemplate template;
	private SecurityConfigProps securityProps;
	private PlatformCommonService commonService;
	private ObjectMapper mapper;

	private static final ServiceContainerEnum SERVICE = ServiceContainerEnum.USER_SERVICE;

	public UserDetails getUserById(String userId) {
		return getUserById(Collections.singleton(userId)).get(userId);
	}

	public UserDetails getUserByMobileNumber(String mobileNumber) {
		return getUserByMobileNumber(Collections.singleton(mobileNumber)).get(mobileNumber);
	}

	public Map<String, UserDetails> getUserByMobileNumber(Set<String> mobileNumbers) {
		if (ObjectUtils.isEmpty(mobileNumbers)) {
			return Collections.emptyMap();
		}
		log.debug("fetching user with mobileNumbers :{}", mobileNumbers);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get(SERVICE));
		HttpEntity<Set<String>> entity = new HttpEntity<>(mobileNumbers, headers);
		try {
			String url = commonService.getInternalCallUrl(SERVICE, "/user/mobile-number");
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UserDetails>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return Collections.emptyMap();
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");

		}
	}

	public Map<String, UserDetails> getUserById(Set<String> userIds) {
		if (ObjectUtils.isEmpty(userIds)) {
			return Collections.emptyMap();
		}
		log.debug("fetching user with userIds :{}", userIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get(SERVICE));
		HttpEntity<Set<String>> entity = new HttpEntity<>(userIds, headers);
		try {
			String url = commonService.getInternalCallUrl(SERVICE, "/user/ids");
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UserDetails>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return Collections.emptyMap();
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}

	}

	public UserGetOrCreateResponse getOrCreateUser(GetOrCreateUserRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		log.debug("fetching or creating user with mobile :{}", request);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get(SERVICE));
		HttpEntity<GetOrCreateUserRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = commonService.getInternalCallUrl(SERVICE, "/user/get-or-create");
			log.debug("request for fetchig/creating user details : {} body and headers {}", url, entity);
			ResponseEntity<UserGetOrCreateResponse> response = template.exchange(url, HttpMethod.POST, entity,
					UserGetOrCreateResponse.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	@Data
	public static class UserGetOrCreateResponse {
		private String userId;
		private String mobileNumber;
		private boolean isNewCustomer;
	}

	@Builder
	@Getter
	public static class GetOrCreateUserRequest {
		private String mobileNumber;
		private String email;
		private String name;
		private String actionBy;
	}

}
