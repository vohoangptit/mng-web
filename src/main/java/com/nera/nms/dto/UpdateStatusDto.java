package com.nera.nms.dto;

import lombok.Data;

@Data
public class UpdateStatusDto {
	
	private String[] ids;
	
	private String activeOption;
}
