package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nera.nms.constants.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "workflow_history")
@Entity
public class WorkflowHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isActive;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowHistory")
	List<WorkflowOperatorHistory> workflowOperatorHistories;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowHistory")
	List<JobInputHistory> jobInputHistories;

	@Column(name = "version", nullable = false)
	private Integer version;

	@Column(name = "workflow_id", nullable = false)
	private Long workflowId;
	
	@Column(name = "SEARCH_STRING", length = 1000)
	private String searchString;

	@Column(name="create_date")
	private Date createdDate;

	@Column(name="created_by")
	private String createdBy;

	@JsonIgnore
	@PreUpdate
	@PrePersist
	void updateSearchString() {
		String status = "";
		if(isActive){
			status = CommonConstants.ACTIVE;
		} else{
			status = CommonConstants.INACTIVE;
		}
		final String fullSearchString = StringUtils
				.join(Arrays.asList(name, description, status, version, " "));
		this.searchString = StringUtils.substring(fullSearchString, 0, 999);
	}
}
