package com.arcadesync.platform.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AuditTimeDTO {
	private Instant createdAt;
	private Instant updatedAt;
}
