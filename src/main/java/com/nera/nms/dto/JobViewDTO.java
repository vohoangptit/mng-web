package com.nera.nms.dto;

import lombok.Data;

@Data
public class JobViewDTO {

	private long id;

	private String name;

	private String description;

	private String workflowName;
}
