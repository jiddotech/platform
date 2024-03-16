package com.arcadesync.platform.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AuditDTO {
	private UserDetails createdBy;
	private UserDetails updatedBy;
	private Instant createdAt;
	private Instant updatedAt;
}
