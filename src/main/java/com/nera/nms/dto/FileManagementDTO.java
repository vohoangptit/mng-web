package com.nera.nms.dto;

import com.nera.nms.constants.CommonConstants;
import lombok.Data;

@Data
public class FileManagementDTO {
	private String no;
	private String name;
	private String file;
	private String status;
	private String result;
	private String description;
	private int version;
	private String updatedBy;
	private String updatedAt;
	private boolean isActive;

	public String getStatus() {
		return this.isActive ? CommonConstants.ACTIVE : CommonConstants.INACTIVE;
	}
}
