package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nera.nms.models.InventoryHost;
import com.nera.nms.models.JobManagement;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

@Data
public class JobDTO {

	private long no = 0;

	private long id;

	private String name;

	private String description;

	private String workflowName;

    private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private Date createdDate;

	private boolean isActive;

	private List<Long> hostsId;

	private String modifiedBy;

	private Date modifiedDate;

	private boolean notifyJob;

	public JobDTO() {
	}

	public JobDTO(JobManagement jobManagement) {
		this.id = jobManagement.getId();
		this.name = jobManagement.getName();
		this.description = jobManagement.getDescription();
		this.workflowName = jobManagement.getWorkflowName();
		this.createdBy = jobManagement.getCreatedBy();
		this.createdDate = jobManagement.getCreatedDate();
		this.isActive = jobManagement.isActive();
		jobManagement.getHosts().stream().forEach(item -> {
			if(!item.isActive()) {
				this.notifyJob = true;
			}
		});
	}

	public JobManagement dtoToEntity(JobManagement jobManagement) {
		if (jobManagement != null) {
			jobManagement.setName(getName());
			jobManagement.setDescription(getDescription());
			jobManagement.setActive(isActive());
			if (StringUtils.isNotBlank(getCreatedBy())) {
				jobManagement.setCreatedDate(getCreatedDate());
				jobManagement.setCreatedBy(getCreatedBy());
			}
			jobManagement.setModifiedBy(getModifiedBy());
			jobManagement.setModifiedDate(getModifiedDate());
			jobManagement.setDeleted(false);
		}
		return jobManagement;
	}
}
