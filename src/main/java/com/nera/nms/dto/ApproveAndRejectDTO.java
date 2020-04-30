package com.nera.nms.dto;

import com.nera.nms.models.ENUM.StatusPlaybook;

import lombok.Data;

@Data
public class ApproveAndRejectDTO {

    private long id;
    
    private String remark;

    private StatusPlaybook status;
}
