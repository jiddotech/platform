package com.arcadesync.platform.dto;

import com.arcadesync.platform.PlatformConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessDTO {
	private String message;

	public static SuccessDTO of() {
		return new SuccessDTO(PlatformConstants.SUCCESS);
	}

	public static SuccessDTO requestSubmitted() {
		return new SuccessDTO(PlatformConstants.REQUEST_SUBMITTED);
	}

}
