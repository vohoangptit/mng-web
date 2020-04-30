package com.nera.nms.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobWorkflowDTO {

    private long jobId;

    private long workflowId;

    private String workflowName;

    private List<JobPayloadDTO> jobPayload;
}
