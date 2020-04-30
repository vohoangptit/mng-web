package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobAssignment;
import lombok.Data;

import java.util.Date;

@Data
public class JobAssignmentDTO{

	private long no = 0;
	
    private long id;
    
    private long plannerId;

    private long assigneeId;
    
    private String startTime;
    
    private String endTime;
    
    private String status;

    private long jobId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date executionDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

	private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date modifiedDate;

    private String modifiedBy;
    
    private boolean isDeleted;

    private String jobName;

    private String assigneeName;

    private String assigneeEmail;

    private String plannerName;

    private String plannerEmail;

    private String workflowName;
	
	private  long workflowId;

    private String jobDescription;

    private String reason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm")
    private Date jobAssignmentAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date jobAssignmentDate;


    public JobAssignmentDTO(){
		
	}
	
	public JobAssignmentDTO(JobAssignment jobAssignment) {
		this.id = jobAssignment.getId();
        this.plannerId = jobAssignment.getPlanner().getId();
        this.assigneeId = jobAssignment.getAssignee().getId();
        this.jobId = jobAssignment.getJobManagement().getId();
		this.assigneeName = jobAssignment.getAssignee().getFullName();
		this.plannerName = jobAssignment.getPlanner().getFullName();
		this.executionDate = jobAssignment.getExecutionDate();
		this.startTime = jobAssignment.getStartTime();
		this.endTime = jobAssignment.getEndTime();
		this.status = jobAssignment.getStatus();
		this.isDeleted = jobAssignment.isDeleted();
		this.createdBy = jobAssignment.getCreatedBy();
		this.createdDate = jobAssignment.getCreatedDate();
		this.modifiedBy = jobAssignment.getModifiedBy();
		this.modifiedDate = jobAssignment.getModifiedDate();
		this.jobName = jobAssignment.getJobManagement().getName();
		this.workflowName = jobAssignment.getJobManagement().getWorkflowName();
		this.jobDescription = jobAssignment.getJobManagement().getDescription();
		this.workflowId = jobAssignment.getJobManagement().getWorkflowID();
		this.reason = jobAssignment.getReason();
	}

}
