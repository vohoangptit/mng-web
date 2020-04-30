package com.nera.nms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "email_template_variable")
public class EmailTemplateVariable {
	@Id
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "email_template_id", insertable = false, updatable = false, referencedColumnName = "id")
	@JsonBackReference
	private UserEmailTemplate emailTemplate;

	@Override
	public String toString() {
		return "EmailTemplateVariable [id=" + id + ", name=" + name + ", description=" + description
				+ ", emailTemplate=" + emailTemplate + "]";
	}

	public EmailTemplateVariable(long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public EmailTemplateVariable() {
	}
}
