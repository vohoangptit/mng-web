package com.nera.nms.dto;

import lombok.Data;

@Data
public class TaskComingDTO {

	private long id;

	private String dateExec;

	private String jobName;

	private String operation;

	private long jobId;

	private long workflowId;
}
