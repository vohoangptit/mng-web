package com.nera.nms.dto;

import com.nera.nms.models.WorkflowHistory;
import com.nera.nms.utils.BEDateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkflowHistoryDTO {

    private long id;
    private long no;
    private long workflowId;
    private int version;
    private String updateAt;
    private String updateBy;
    private String name;
    private String description;
    List<WorkflowOperatorHistoryDTO> workflowOperatorHistories;

    public WorkflowHistoryDTO(WorkflowHistory workflowHistory) {
        this.id = workflowHistory.getId();
        this.version = workflowHistory.getVersion() == 0 ? 1 : workflowHistory.getVersion();
        this.updateAt = BEDateUtils.convertDateToStringByFormatOn(workflowHistory.getCreatedDate());
        this.updateBy = workflowHistory.getCreatedBy();
        this.name = workflowHistory.getName();
        this.description = workflowHistory.getDescription();
        this.workflowId = workflowHistory.getWorkflowId();
    }
}
