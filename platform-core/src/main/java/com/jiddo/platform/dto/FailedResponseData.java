package com.jiddo.platform.dto;

import lombok.Data;

@Data
public class FailedResponseData implements IActionResponseData {
	private String reason;
}
