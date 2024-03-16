package com.arcadesync.platform.dto;

import lombok.Data;

@Data
public class ActionResponse {
	private boolean success;
	private IActionResponseData data;
}