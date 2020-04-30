package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobOperatorExecution;
import lombok.Data;

import java.util.Date;

@Data
public class JobExeOpsDTO {
    private long no = 0;
    private long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeEnd;
    private  String result ;
    private  Integer type;
    public  JobOperatorExecution dto2Entity()
    {
        JobOperatorExecution jobEntity  = new JobOperatorExecution();
        jobEntity.setId(getId());
        jobEntity.setExecuteEnd(getExecuteEnd());
        jobEntity.setExecuteStart(getExecuteStart());
        jobEntity.setResult(getResult());
        jobEntity.setType(getType());
        return jobEntity;
    }


}
