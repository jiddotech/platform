package com.arcadesync.platform.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class GenericFilterService {

	private DynamicFilterService dynamicFilterService;

	private StaticFilterService staticFilterService;

	private GenericFilterConfig filterConfig;

	public List<FilterDTO<Object>> getFiltersViaConfigKey(String key) {
		if (!filterConfig.getFilterToBeServed().containsKey(key)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid filter code");
		}
		Set<String> filters = filterConfig.getFilterToBeServed().get(key);
		return getFilters(filters);
	}

	public List<FilterDTO<Object>> getFilters(Set<String> filters) {
		log.debug("get filter listOfFiltersTobeServed : {}", filters);
		List<FilterDTO<Object>> list = new ArrayList<>();
		for (String filterEnum : filters) {
			FilterDTO<Object> filter = getFilter(filterEnum);
			if (!ObjectUtils.isEmpty(filters)) {
				list.add(filter);
			} else {
				log.warn("filter not found : {}", filterEnum);
			}
		}
		return list;
	}

	public List<FilterDTO<Object>> getFilters(List<String> filters) {
		log.debug("get filter listOfFiltersTobeServed : {}", filters);
		List<FilterDTO<Object>> list = new ArrayList<>();
		for (String filterEnum : filters) {
			FilterDTO<Object> filter = getFilter(filterEnum);
			if (!ObjectUtils.isEmpty(filters)) {
				list.add(filter);
			} else {
				log.warn("filter not found : {}", filterEnum);
			}
		}
		return list;
	}

	public FilterDTO<Object> getFilter(String filterEnum) {
		FilterDTO<Object> filter = staticFilterService.getFilterData(filterEnum);
		if (ObjectUtils.isEmpty(filter)) {
			filter = dynamicFilterService.getFilterData(filterEnum);
		}
		return filter;
	}

}
