package com.nera.nms.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Strings;
import com.nera.nms.models.ENUM;
import com.nera.nms.models.Playbook;
import com.nera.nms.models.PlaybookInput;
import com.nera.nms.models.PlaybookOutput;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class PlaybookDTO{

	private long no = 0;
	
    private long id;
    
    private String name;

    private String remark;
    
    private String note;
    
    private String fileName;
    
    private ENUM.StatusPlaybook status;

    private String sourceUrl;
    
    private String approvedBy;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date approvedDate;
    
    private boolean isActive;

    private boolean isDeleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date createdDate;

	private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date modifiedDate;

    private String modifiedBy;
    
    private boolean sendToApproved;

    private int version;

    public PlaybookDTO(){
		
	}
	
	public PlaybookDTO(Playbook playbook) {
		this.id = playbook.getId();
		this.name = playbook.getName();
		this.note = playbook.getNote();
		this.sourceUrl = playbook.getSourceUrl();
		this.approvedBy = playbook.getApprovedBy() == null ? StringUtils.EMPTY : playbook.getApprovedBy() ;
		this.approvedDate = playbook.getApprovedDate();
		this.createdBy = playbook.getCreatedBy();
		this.createdDate = playbook.getCreatedDate();
		this.modifiedBy = playbook.getModifiedBy();
		this.modifiedDate = playbook.getModifiedDate();
		this.isActive = playbook.isActive();
		this.isDeleted = playbook.isDeleted();
		this.remark = playbook.getRemark();
		this.status = playbook.getStatus();
		this.fileName = playbook.getFileName();
		this.sendToApproved = playbook.isSendToApproved();
		this.version = playbook.getVersion() == 0 ? 1 : playbook.getVersion();
	}
	
	List<PlaybookInput> playbookInput = new ArrayList<>();
	
	List<PlaybookOutput> playbookOutput = new ArrayList<>();
	
	public Playbook dtoToEntitySave (PlaybookDTO playbook) {
		Playbook result = new Playbook();
		result.setId(playbook.getId()== 0 ? playbook.id : playbook.getId());
		result.setName(playbook.getName());
    	result.setNote(Strings.isNullOrEmpty(playbook.getNote()) ? "" : playbook.getNote());
    	result.setRemark(Strings.isNullOrEmpty(playbook.getRemark()) ? "" : playbook.getRemark());
    	result.setActive(playbook.isActive());
    	result.setDeleted(playbook.isDeleted());
    	result.setCreatedDate(playbook.getCreatedDate() == null ? null : playbook.getCreatedDate());
    	result.setCreatedBy(playbook.getCreatedBy() == null ? null : playbook.getCreatedBy());
    	result.setModifiedDate(playbook.getModifiedDate() == null ? null : playbook.getModifiedDate());
    	result.setModifiedBy(playbook.getModifiedBy() == null ? null : playbook.getModifiedBy());
    	result.setPlaybookInput((playbook.getPlaybookInput().isEmpty() && playbook.getPlaybookInput() ==null) ? null : playbook.getPlaybookInput());
    	result.setPlaybookOutput((playbook.getPlaybookOutput().isEmpty() && playbook.getPlaybookOutput() ==null) ? null : playbook.getPlaybookOutput());
    	result.setSourceUrl(Strings.isNullOrEmpty(playbook.getSourceUrl()) ? "" : playbook.getSourceUrl());
    	result.setFileName(Strings.isNullOrEmpty(playbook.getFileName()) ? "" : playbook.getFileName());
    	result.setSendToApproved(playbook.isSendToApproved());
    	result.setStatus(playbook.getStatus());
		return result;
	}
	
}
