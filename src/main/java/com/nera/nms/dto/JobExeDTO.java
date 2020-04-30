package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobExecution;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobExeDTO {
    private long no = 0;
    private long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeEnd;
    private  String result ;
    private  String log;
    private long jobId;
    List<JobExeOpsDTO> jobOperatorExecutions ;
    public JobExeDTO(){

    }
    public JobExeDTO(JobExecution jobExecution){
        this.id = jobExecution.getId();
        this.executeEnd = jobExecution.getExecuteEnd();
        this.executeStart = jobExecution.getExecuteStart();
        this.log = jobExecution.getLog();
        this.result = jobExecution.getResult();
    }




}
