package com.nera.nms.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "workflow_operator")
public class WorkflowOperator {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "operator_type")
	private Integer type;

	@Column(name = "connect_from")
	private String from;

	@Column(name = "connect_to")
	private String to;
	
	@Column(name = "properties")
	private String properties;
	
	@Column(name = "content_xml", length = 8000)
	private String contentXML;

	@Column(name = "operator_playbook")
	private Long playbook;
	
	@Column(name = "approved_person")
	private Long approved;

	@ManyToOne
	@JoinColumn(name = "workflow_id", insertable = false, updatable = false)
	@JsonBackReference
	private Workflow workflow;
}
