package com.arcadesync.platform.dto;

import lombok.Data;

@Data
public class FailedResponseData implements IActionResponseData {
	private String reason;
}
