package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobExecution;
import com.nera.nms.models.JobOperatorExecution;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobOpsExeApprivalDTO {
    private long no = 0;
    private long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date executeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date executeEnd;
    private  String result ;
    private  String log;
    List<JobOperatorExecution> jobOperatorExecutions ;
    public JobOpsExeApprivalDTO(){

    }
    public JobOpsExeApprivalDTO(JobExecution jobExecution){
        this.id = jobExecution.getId();
        this.executeEnd = jobExecution.getExecuteEnd();
        this.executeStart = jobExecution.getExecuteStart();
        this.log = jobExecution.getLog();
        this.result = jobExecution.getResult();

    }
    public JobExecution dto2Entity()
    {
        JobExecution jobexe = new JobExecution();
        jobexe.setId(getId());
        jobexe.setExecuteEnd(getExecuteEnd());
        jobexe.setExecuteStart(getExecuteStart());
        jobexe.setResult(getResult());
        jobexe.setLog(getLog());
        jobexe.setJobExeList(getJobOperatorExecutions());
        return jobexe;
    }


}
