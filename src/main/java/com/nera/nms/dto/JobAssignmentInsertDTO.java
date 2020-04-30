package com.nera.nms.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class JobAssignmentInsertDTO{

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
}
