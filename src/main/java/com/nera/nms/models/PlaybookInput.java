package com.nera.nms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "playbook_input", uniqueConstraints = { @UniqueConstraint(columnNames = { "variable", "playbook_id"})})
public class PlaybookInput {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "type")
	private ENUM.PlaybookInputType type;

	@Enumerated(EnumType.STRING)
	public ENUM.PlaybookInputType getType() {
		return type;
	}

	@Column(name = "variable")
	private String variable;

	@Column(name = "value")
	private String value;

	@Column(name = "file_management_id")
	private Long fileManagementId;

    @Column(name="is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "playbook_id", insertable = false, updatable = false)
	@JsonBackReference
	private Playbook playbook;
	
	@Transient
	private FileManagement fileInfo;
	
	@Column(name="mandatory", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean mandatory;
}
