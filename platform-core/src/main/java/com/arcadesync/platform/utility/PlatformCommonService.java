package com.arcadesync.platform.utility;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.clients.UrlConfig;
import com.arcadesync.platform.clients.UserClient;
import com.arcadesync.platform.dto.AuditDTO;
import com.arcadesync.platform.dto.AuditData;
import com.arcadesync.platform.dto.UserDetails;
import com.arcadesync.platform.enums.ServiceContainerEnum;
import com.arcadesync.platform.exception.ApplicationException;
import com.arcadesync.platform.exception.AuthenticationException;
import com.arcadesync.platform.exception.ErrorField;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class PlatformCommonService {

	@Autowired
	@Lazy
	private ObjectMapper mapper;

	@Autowired
	@Lazy
	private RedissonClient redissonClient;

	@Autowired
	@Lazy
	private UrlConfig urlConfig;

	@Autowired
	@Lazy
	private UserClient userClient;

	public ErrorField parseError(String errorResponse) {
		try {
			return mapper.readValue(errorResponse, ErrorField.class);
		} catch (JsonProcessingException e) {
			log.error("error occured while parsing the error message", errorResponse);
		}
		return null;
	}

	public boolean is404Error(String errorResponse) {
		ErrorField error = parseError(errorResponse);
		if (ObjectUtils.isEmpty(error)) {
			return false;
		}
		return StringUtils.equals(error.getCode(), PlatformConstants.CODE_404);
	}

	public void throwRespectiveError(String errorResponse) {
		ErrorField error = parseError(errorResponse);
		if (ObjectUtils.isEmpty(error)) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}
		switch (error.getErrorType()) {
		case APPLICATION_EXCEPTION:
			throw new ApplicationException(error.getCode(), error.getMessage());
		case VALIDATION_EXCEPTION:
			throw new ApplicationException(error.getCode(), error.getMessage());
		case AUTHENTICATION_EXCEPTION:
			throw new AuthenticationException(error.getCode(), error.getMessage());
		default:
			break;
		}
	}

	public String writeAsString(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to write as string", e);
		}
	}

	public String writeAsStringIgnoreError(Object obj) {
		try {
			return writeAsString(obj);
		} catch (ApplicationException e) {
			log.error("error occured while writing obj to json:{}", obj);
			return null;
		}
	}

	public String getInternalCallUrl(ServiceContainerEnum container, String suffix) {
		return MessageFormat.format("{0}/{1}/secure/internal-call{2}", urlConfig.getBaseUrl(),
				container.getContextPath(), suffix);
	}

	public AuditDTO getAuditDTO(AuditData data) {
		Set<String> userIds = new HashSet<>();
		userIds.add(data.getCreatedBy());
		userIds.add(data.getUpdatedBy());
		Map<String, UserDetails> users = userClient.getUserById(userIds);
		AuditDTO dto = new AuditDTO();
		dto.setCreatedBy(users.get(data.getCreatedBy()));
		dto.setUpdatedBy(users.get(data.getUpdatedBy()));
		dto.setCreatedAt(data.getCreatedAt());
		dto.setUpdatedAt(data.getUpdatedAt());
		return dto;
	}

	public AuditDTO getAuditDTO(AuditData data, Map<String, UserDetails> users) {
		Set<String> userIds = new HashSet<>();
		userIds.add(data.getCreatedBy());
		userIds.add(data.getUpdatedBy());
		AuditDTO dto = new AuditDTO();
		dto.setCreatedBy(users.get(data.getCreatedBy()));
		dto.setUpdatedBy(users.get(data.getUpdatedBy()));
		dto.setCreatedAt(data.getCreatedAt());
		dto.setUpdatedAt(data.getUpdatedAt());
		return dto;
	}

}
