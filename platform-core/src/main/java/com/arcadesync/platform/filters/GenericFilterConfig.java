package com.arcadesync.platform.filters;

import java.util.Map;
import java.util.Set;

public interface GenericFilterConfig {
	public Set<String> getExcludedParams();

	public Map<String, Set<String>> getFilterToBeServed();

	public Boolean getFailFast();
}
