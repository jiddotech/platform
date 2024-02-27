package com.jiddo.platform.dto;

import com.jiddo.platform.enums.ActiveInactiveStatus;

import lombok.Data;

@Data
public class ActivateInactivateRequest {
	private String entityId;
	private ActiveInactiveStatus status;
	private String actionBy;
}