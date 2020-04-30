package com.nera.nms.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, exclude = {"workflowHistory"})
@Entity
@Table(name = "workflow_operator_history")
public class WorkflowOperatorHistory {
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

	@Column(name = "playbook_version", nullable = false)
	private Integer playbookVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_history_id")
	private WorkflowHistory workflowHistory;
}
