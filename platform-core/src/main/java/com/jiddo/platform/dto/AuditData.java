package com.jiddo.platform.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.Data;

@Data
public class AuditData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4394147471341786492L;
	private String createdBy;
	private String updatedBy;
	private Instant updatedAt;
	private Instant createdAt;

	public AuditData(String createdBy) {
		this.updatedBy = createdBy;
		this.updatedAt = Instant.now();
		this.createdAt = Instant.now();
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		this.updatedAt = Instant.now();
	}
}
