package com.jiddo.platform.utility;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiddo.platform.PlatformConstants;
import com.jiddo.platform.clients.UrlConfig;
import com.jiddo.platform.enums.ServiceContainerEnum;
import com.jiddo.platform.exception.ApplicationException;
import com.jiddo.platform.exception.AuthenticationException;
import com.jiddo.platform.exception.ErrorField;
import com.jiddo.platform.exception.LoggerType;
import com.jiddo.platform.exception.PlatformExceptionCodes;
import com.jiddo.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public final class PlatformCommonService {

	private ObjectMapper mapper;
	private RedissonClient redissonClient;
	private UrlConfig urlConfig;

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

	public RLock takeLock(String key, long leaseTimeInSeconds) {
		return takeLock(key, leaseTimeInSeconds, "Request being processed, please wait...", LoggerType.ERROR);
	}

	public RLock takeLock(String key, long leaseTimeInSeconds, String errorMessage, LoggerType loggerType) {
		RLock lock = redissonClient.getLock(key);
		if (lock.isLocked()) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), errorMessage, loggerType);
		}
		lock.lock(leaseTimeInSeconds, TimeUnit.SECONDS);
		return lock;
	}

	public void unlock(String key) {
		RLock lock = redissonClient.getLock(key);
		lock.unlock();
	}

	public void unlock(RLock lock) {
		lock.unlock();
	}

	public void forceUnlock(String key) {
		RLock lock = redissonClient.getLock(key);
		lock.forceUnlock();
	}

	public void forceUnlock(RLock lock) {
		lock.forceUnlock();
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
		return MessageFormat.format("{0}/{1}/secure/internal-call/{2}", urlConfig.getBaseUrl(),
				container.getServiceName(), suffix);
	}

}
