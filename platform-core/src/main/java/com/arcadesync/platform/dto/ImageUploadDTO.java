package com.arcadesync.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImageUploadDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7048392945376058432L;
	private String name;
	private String relativePath;
	private String uploadId;
}
