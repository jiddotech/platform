package com.jiddo.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmployeeDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8315971748398515838L;
	private String employeeId;
	private String mobileNumber;
	private String name;
	private String email;
}
