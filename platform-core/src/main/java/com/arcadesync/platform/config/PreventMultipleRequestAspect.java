package com.arcadesync.platform.config;

import java.lang.reflect.Method;

import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arcadesync.platform.exception.LoggerType;
import com.arcadesync.platform.lock.LockService;
import com.arcadesync.platform.security.SecurityUtils;
import com.arcadesync.platform.security.UserDTO;

@Aspect
@Component
public class PreventMultipleRequestAspect {

	@Autowired
	private LockService lockService;

	@Around("@annotation(PreventMultipleRequest)")
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		PreventMultipleRequest annotations = method.getAnnotation(PreventMultipleRequest.class);
		StringBuilder sb = new StringBuilder();
		sb.append("PREVENT_MULTIPLE_REQUEST_").append(signature.getDeclaringTypeName()).append(method.getName());
		if (BooleanUtils.isTrue(annotations.isUserSpecific())) {
			UserDTO user = SecurityUtils.getLoggedInUser();
			sb.append("_").append(user.getUserId());
		}
		lockService.getLock(sb.toString(), annotations.ttlInSeconds(),
				"Request being processed please wait...", LoggerType.DO_NOT_LOG);
		try {
			Object result = joinPoint.proceed();
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			lockService.unlock(sb.toString());
		}
	}

}