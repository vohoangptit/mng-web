package com.nera.nms.dto;

import com.nera.nms.models.JobPayload;
import lombok.Data;

@Data
public class JobPayloadDTO {

    private long jobId;

    private long jobInputId;

    private String value;

    public JobPayloadDTO(){}

    public JobPayloadDTO(long jobId, long jobInputId, String value) {
        this.jobId = jobId;
        this.jobInputId = jobInputId;
        this.value = value;
    }

    public void dtoToEntity(JobPayload jobPayload) {
        if (jobPayload != null) {
            jobPayload.setJobInputId(getJobInputId());
            jobPayload.setValue(getValue());
        }
    }
}
