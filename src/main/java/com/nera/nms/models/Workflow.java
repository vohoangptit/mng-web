package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "workflow", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@Entity
public class Workflow extends CommonColumn {
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

	@Column(name = "is_deleted", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isDeleted;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_id")
	List<WorkflowOperator> workflowOperator = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_id", nullable = false, referencedColumnName = "id")
	List<JobInput> jobInput = new ArrayList<>();

	@Column(name = "version", nullable = false)
	private Integer version = 1;
	
	@Column(name = "SEARCH_STRING", length = 1000)
	private String searchString;

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
