package com.nera.nms.dto;

import com.nera.nms.models.WorkflowOperatorHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkflowOperatorHistoryDTO {

    private Long id;

    private Integer type;

    private String from;

    private String to;

    private String properties;

    private String contentXML;

    private Long playbook;

    private Long approved;

    private Integer playbookVersion;

    public WorkflowOperatorHistoryDTO(WorkflowOperatorHistory workflowOperatorHistory) {
        this.id = workflowOperatorHistory.getId();
        this.type = workflowOperatorHistory.getType();
        this.from = workflowOperatorHistory.getFrom();
        this.to = workflowOperatorHistory.getTo();
        this.properties = workflowOperatorHistory.getProperties();
        this.contentXML = workflowOperatorHistory.getContentXML();
        this.playbook = workflowOperatorHistory.getPlaybook();
        this.approved = workflowOperatorHistory.getApproved();
        this.playbookVersion = workflowOperatorHistory.getPlaybookVersion();
    }
}
