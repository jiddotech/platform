package com.arcadesync.platform.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ObjectUtils;

import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;
import com.arcadesync.platform.functionalInterface.AnonymousMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ICacheCommonService {

	protected static final Map<String, AnonymousMethod> REFRESH_KEY_METHOD = new HashMap<>();
	
	@Autowired
	@Lazy
	private RedissonClient redissonClient;

	public void refreshCache(List<String> cacheIds) {
		if (ObjectUtils.isEmpty(cacheIds)) {
			return;
		}
		validateRequest(cacheIds);
		for (String keys : cacheIds) {
			log.info("clearing cache for key : {}", keys);
			REFRESH_KEY_METHOD.get(keys).execute();
		}
	}

	private void validateRequest(List<String> cacheIds) {
		for (String key : cacheIds) {
			if (!REFRESH_KEY_METHOD.containsKey(key)) {
				throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
						"Invalid cacheId : " + key);
			}
		}
	}

	public Set<String> refreshCacheKeys() {
		return REFRESH_KEY_METHOD.keySet();
	}
	
	public <K, V> V get(ICacheKey type, K entityId, Function<Set<K>, Map<K, V>> notFoundIdsResolver) {
		if (ObjectUtils.isEmpty(entityId)) {
			return null;
		}
		return get(type, Collections.singleton(entityId), notFoundIdsResolver).get(entityId);
	}

	public <K, V> Map<K, V> get(ICacheKey type, Set<K> entityIds, Function<Set<K>, Map<K, V>> notFoundIdsResolver) {
		if (ObjectUtils.isEmpty(entityIds)) {
			return Collections.emptyMap();
		}
		entityIds.remove(null);
		if (ObjectUtils.isEmpty(entityIds)) {
			return Collections.emptyMap();
		}
		RMap<K, V> ROLE_CACHE_MAP = redissonClient.getMap(type.getKey());
		Map<K, V> response = ROLE_CACHE_MAP.getAll(entityIds);
		Set<K> notFoundRoles = new HashSet<>();
		for (K roleId : entityIds) {
			if (!response.containsKey(roleId)) {
				notFoundRoles.add(roleId);
			}
		}
		Map<K, V> dbRecords = notFoundIdsResolver.apply(notFoundRoles);
		ROLE_CACHE_MAP.putAllAsync(dbRecords);
		response.putAll(dbRecords);
		return response;
	}

	public void refreshCache(ICacheKey type, String entityId) {
		redissonClient.getMap(type.getKey()).remove(entityId);
	}

	public void refreshCache(ICacheKey type) {
		redissonClient.getMap(type.getKey()).clear();
	}
	
	public interface ICacheKey {
		String getKey();
	}

}
