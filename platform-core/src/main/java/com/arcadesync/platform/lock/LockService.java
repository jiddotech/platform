package com.arcadesync.platform.lock;

import com.arcadesync.platform.exception.LoggerType;

public interface LockService {

	public void getLock(String key, long leaseTimeInSeconds);

	public void getLock(String key, long leaseTimeInSeconds, String errorMessage, LoggerType loggerType);

	public void unlock(String key);
}
