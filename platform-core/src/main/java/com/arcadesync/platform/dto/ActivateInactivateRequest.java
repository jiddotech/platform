package com.arcadesync.platform.dto;

import com.arcadesync.platform.enums.ActiveInactiveStatus;

import lombok.Data;

@Data
public class ActivateInactivateRequest {
	private String entityId;
	private ActiveInactiveStatus status;
	private String actionBy;
}