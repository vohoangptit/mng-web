package com.nera.nms.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nera.nms.models.EmailTemplateVariable;

import lombok.Data;

@Data
public class UserEmailTemplateDTO {
	
	private long no;

    private long id;

    private String templateName;
    
    private String subject;

    private String templateContent;

    private boolean isActive;

    private boolean isDeleted;

    private Date createdDate;

    private String createdBy;

    private Date modifiedDate;

    private String modifiedBy;
    
    private List<EmailTemplateVariable> emailTemplateVariable = new ArrayList<>();
    
}
