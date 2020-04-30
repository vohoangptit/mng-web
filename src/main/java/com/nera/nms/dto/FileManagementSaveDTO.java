package com.nera.nms.dto;

import lombok.Data;

@Data
public class FileManagementSaveDTO {
	private String name;
	private String file;
	private String status;
	private String description;
}
