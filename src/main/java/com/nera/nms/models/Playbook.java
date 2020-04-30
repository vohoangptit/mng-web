package com.nera.nms.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import com.nera.nms.constants.CommonConstants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "playbook", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@Entity
public class Playbook extends CommonColumn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "note")
	@Type(type="text")
	private String note;

	@Column(name = "remark")
	@Type(type="text")
	private String remark;

	@Column(name = "status")
	private ENUM.StatusPlaybook status;

	@Enumerated(EnumType.STRING)
	public ENUM.StatusPlaybook getStatus() {
		return status;
	}

	@Column(name = "source_url")
	private String sourceUrl;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "approved_date")
	private Date approvedDate;

	@Column(name = "is_active", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isActive;	

	@Column(name = "is_deleted", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isDeleted;

	@Column(name = "SEARCH_STRING", length = 1000)
	private String searchString;

	@Column(name = "version", nullable = false)
	private Integer version = 1;

	@PreUpdate
	@PrePersist
	void updateSearchString() {
		SimpleDateFormat formatterPlaybook = new SimpleDateFormat(CommonConstants.DATE_FORMAT_MM_DD);
		final String fullSearchString = StringUtils
				.join(Arrays.asList(name, getCreatedBy(), formatterPlaybook.format(getCreatedDate()),
						approvedBy == null ? " " : approvedBy, 
						approvedDate == null ? " " : formatterPlaybook.format(approvedDate), status, " "));
		this.searchString = StringUtils.substring(fullSearchString, 0, 999);
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "playbook_id")
	List<PlaybookInput> playbookInput = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "playbook_id", nullable = false, referencedColumnName = "id")
	List<PlaybookOutput> playbookOutput = new ArrayList<>();
	
	@Column(name = "send_to_approved", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean sendToApproved;
}
