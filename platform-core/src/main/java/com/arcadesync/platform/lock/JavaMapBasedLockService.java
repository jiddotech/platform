package com.arcadesync.platform.lock;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;

import com.arcadesync.platform.exception.LoggerType;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;

class JavaMapBasedLockService implements LockService {

	private static final ConcurrentHashMap<String, Instant> CONCURRENT_MAP = new ConcurrentHashMap<>();

	@Override
	public void getLock(String key, long leaseTimeInSeconds) {
		getLock(key, leaseTimeInSeconds, "Request being processed, please wait...", LoggerType.ERROR);
	}

	@Override
	public void getLock(String key, long leaseTimeInSeconds, String errorMessage, LoggerType loggerType) {
		Instant time = CONCURRENT_MAP.get(key);
		Instant currentTime = Instant.now();
		if (ObjectUtils.isNotEmpty(time) && time.plusSeconds(leaseTimeInSeconds).isAfter(currentTime)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), errorMessage, loggerType);
		}
		CONCURRENT_MAP.put(key, currentTime);
	}

	@Override
	public void unlock(String key) {
		CONCURRENT_MAP.remove(key);
	}

}
