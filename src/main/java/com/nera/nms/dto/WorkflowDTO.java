package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.JobInput;
import com.nera.nms.models.Workflow;
import com.nera.nms.models.WorkflowOperator;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkflowDTO{
    private long no = 0;
    private long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Boolean del;
    private String searchString;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date createdDate;
    private List<WorkflowOperator> workflowOperator;
    private List<JobInput> jobInput;
    private int version;

    public WorkflowDTO()
    {

    }

    public WorkflowDTO(Workflow workflow)
    {
        this.id = workflow.getId();
        this.name = workflow.getName();
        this.description = workflow.getDescription();
        this.isActive = workflow.isActive();
        this.del = workflow.isDeleted();
        this.searchString = workflow.getSearchString();
        this.createdBy = workflow.getCreatedBy();
        this.createdDate = workflow.getCreatedDate();
        if(workflow.getVersion() == 0) {
            this.version = 1;
        } else {
            this.version = workflow.getVersion();
        }
    }

    public Workflow dtoToEntitySave()
    {
        Workflow workflow = new Workflow();
        if(getId() > 0) {
            workflow.setId(getId());
        }
        workflow.setName(getName());
        workflow.setDescription(getDescription());
        workflow.setActive(getIsActive());
        if (getDel() == null) {
            workflow.setDeleted(false);
        } else {
            workflow.setDeleted(getDel());
        }
        workflow.setDeleted(getDel());
        workflow.setWorkflowOperator(getWorkflowOperator());
        workflow.setJobInput(getJobInput());
        workflow.setCreatedDate(new Date());
        workflow.setCreatedBy(getCreatedBy());
        workflow.setVersion(getVersion());
        return workflow;
    }
    
} 