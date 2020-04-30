package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.models.JobAssignment;
import lombok.Data;

import java.util.Date;

@Data
public class JobCalendarViewDTO {


    private long id;

    @JsonProperty(value="title")
    private String jobName;

    @JsonProperty(value="start")
    private String startTime;

    @JsonProperty(value="end")
    private String endTime;

    @JsonProperty(value="color")
    private String color;

    @JsonProperty(value="textColor")
    private String textColor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY/MM/dd")
    private Date executionDate;

	public JobCalendarViewDTO(JobAssignment jobAssignment) {
		this.id = jobAssignment.getId();
        this.jobName = jobAssignment.getJobManagement().getName();
		this.executionDate = jobAssignment.getExecutionDate();
		this.startTime = this.executionDate.toString().substring(0, 11) + jobAssignment.getStartTime();
		this.endTime = this.executionDate.toString().substring(0, 11) + jobAssignment.getEndTime();
		this.color = setColor(jobAssignment.getStatus());
		this.textColor = "white";
	}

	String setColor(String status){
	    if(status.equals(JobPlanStatus.PENDING)){
	         return "#2E64FE";
        } else if(status.equals(JobPlanStatus.ACCEPTED)){
            return "#088A4B";
        }else if (status.equals(JobPlanStatus.EXECUTING))
        {
            return "#dfb24a";
        } else if (status.equals(JobPlanStatus.FINISHED_APPROVED))
        {
            return "#4fdfc3";
        } else if (status.equals(JobPlanStatus.FINISHED_REJECTED))
        {
            return "#d66262";
        } else if (status.equals(JobPlanStatus.FINISHED_STOPPED))
        {
            return "#949483";
        } else if (status.equals(JobPlanStatus.FINISHED_FAILED))
        {
            return "#131215";
        }
	    else {
            return "#DF0101";
        }
    }
}
