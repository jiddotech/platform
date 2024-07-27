package com.arcadesync.platform.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.arcadesync.platform.exception.LoggerType;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class RedissonLockService implements LockService {

	private RedissonClient redissonClient;

	@Override
	public void getLock(String key, long leaseTimeInSeconds) {
		getLock(key, leaseTimeInSeconds, "Request being processed, please wait...", LoggerType.ERROR);
	}

	@Override
	public void getLock(String key, long leaseTimeInSeconds, String errorMessage, LoggerType loggerType) {
		RLock lock = redissonClient.getLock(key);
		if (lock.isLocked()) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), errorMessage, loggerType);
		}
		lock.lock(leaseTimeInSeconds, TimeUnit.SECONDS);
	}

	public void unlock(String key) {
		RLock lock = redissonClient.getLock(key);
		lock.unlock();
	}

}
