package com.nera.nms.dto;

import java.util.Date;

import com.nera.nms.models.Group;

import lombok.Data;

@Data
public class GroupDTO {

    private Long id;
    
    private String groupName;

    private String description;

    private boolean isActive;

    private boolean isDeleted;
    
    private long no = 0;
    
    private Date createdDate;

    private String createdBy;

    private Date modifiedDate;

    private String modifiedBy;

	public GroupDTO(Group group) {
		this.id = group.getId();
		this.groupName = group.getGroupName();
		this.description = group.getDescription();
		this.isActive = group.isActive();
		this.isDeleted = group.isDeleted();
	}
	
	public GroupDTO(){
		
	}
    
	public Group dtoToEntity (GroupDTO group) {
		Group result = new Group();
    	result.setGroupName(group.getGroupName());
    	result.setDescription(group.getDescription());
    	result.setActive(group.isActive());
    	result.setDeleted(false);
    	result.setCreatedDate(group.getCreatedDate()== null ? null : group.getCreatedDate());
    	result.setModifiedDate(group.getModifiedDate()== null ? null : group.getModifiedDate());
    	result.setCreatedBy(group.getCreatedBy()== null ? null : group.getCreatedBy());
    	result.setModifiedBy(group.getModifiedBy()== null ? null : group.getModifiedBy());
		return result;
	}
}
