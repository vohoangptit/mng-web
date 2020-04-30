package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobAssignment;
import lombok.Data;

@Data
public class JobStatisticDTO {

	private String jobName;

	private String workflowName;

	private String status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private String executionTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private String assignTime;

	public JobStatisticDTO(JobAssignment jobAssignment) {
		this.status = jobAssignment.getStatus();
		this.jobName = jobAssignment.getJobName();
		this.executionTime = jobAssignment.getExecutionDate().toString().substring(0,11) + jobAssignment.getStartTime();
		this.workflowName = jobAssignment.getWorkflowName();
		this.assignTime = jobAssignment.getCreatedDate().toString().substring(0,11);
	}

}
