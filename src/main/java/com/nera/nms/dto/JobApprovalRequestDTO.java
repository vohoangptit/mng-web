package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nera.nms.models.JobManagement;
import com.nera.nms.models.User;
import lombok.Data;

import java.util.Date;

@Data
public class JobApprovalRequestDTO {

	private long id;

	private long no;

	private String jobDescription;

	private String jobName;

	@JsonIgnore
	private User assignee;

	private String workflowName;

	@JsonIgnore
    private User planner;

    private String assigneeName;

    private String plannerName;

    private long jobAssignmentId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private Date requestAt;

	public JobApprovalRequestDTO(long id, String jobDescription, String jobName, User assignee, String workflowName, User planner, Date requestAt) {
		this.id = id;
		this.jobDescription = jobDescription;
		this.jobName = jobName;
		this.assignee = assignee;
		this.workflowName = workflowName;
		this.planner = planner;
		this.requestAt = requestAt;
	}

	public JobApprovalRequestDTO(long id, JobManagement jobManagement, User assignee, User planner, Date requestAt, long jobAssignmentId) {
		this.id = id;
		this.jobDescription = jobManagement.getDescription();
		this.jobName = jobManagement.getName();
		this.assignee = assignee;
		this.workflowName = jobManagement.getWorkflowName();
		this.planner = planner;
		this.requestAt = requestAt;
		this.jobAssignmentId = jobAssignmentId;
	}

	public JobApprovalRequestDTO() {
	}
}
