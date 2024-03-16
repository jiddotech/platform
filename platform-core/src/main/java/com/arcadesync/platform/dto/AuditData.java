package com.arcadesync.platform.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuditData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4394147471341786492L;
	private String createdBy;
	private String updatedBy;
	private Instant updatedAt;
	private Instant createdAt;

	public static AuditData createdBy(String createdBy) {
		AuditData data = new AuditData();
		data.updatedBy = createdBy;
		data.updatedAt = Instant.now();
		data.createdAt = Instant.now();
		data.createdBy = createdBy;
		return data;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		this.updatedAt = Instant.now();
	}
}
