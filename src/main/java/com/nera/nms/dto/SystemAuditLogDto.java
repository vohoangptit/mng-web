/**
 * 
 */
package com.nera.nms.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SystemAuditLogDto {

    private long no = 0;

    private String userName;
    
    private String userGroupName;
    
    private String description;
    
    private String details;
    
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date actionDate;

    private String strActionDate;
}
