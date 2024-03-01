package com.jiddo.platform.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AuditDTO {
	private EmployeeDetails createdBy;
	private EmployeeDetails updatedBy;
	private Instant createdAt;
	private Instant updatedAt;
}
