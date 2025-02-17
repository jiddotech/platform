package com.arcadesync.platform.filters;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface AdvancedFilterRepository<T> {

	Page<T> filter(List<AbstractFilter> filters, Pageable page, String[] includedFields, Class<T> clazz);

	Page<T> filter(List<AbstractFilter> filter, Pageable page, Class<T> clazz);

	List<T> filter(List<AbstractFilter> filters, String[] includedFields, Class<T> clazz);

	List<T> filter(List<AbstractFilter> filters, String[] includedFields, Sort sort, Class<T> clazz);

	List<T> filter(List<AbstractFilter> filters, Class<T> clazz);

	List<T> filter(List<AbstractFilter> filters, Sort sort, Class<T> clazz);

	CustomPageResponse<T> filter(List<AbstractFilter> filter, int limit, long offset, Class<T> clazz);

	Optional<T> filterOne(List<AbstractFilter> filters, Class<T> clazz);

	Optional<T> filterOne(String key, String value, Class<T> clazz);

	<R> List<R> findDistinct(List<AbstractFilter> filters, String field, Class<T> clazz, Class<R> clazzResponse);

}
