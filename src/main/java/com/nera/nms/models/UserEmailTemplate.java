package com.nera.nms.models;

import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "user_email_template")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserEmailTemplate extends CommonColumn {

    @Id
    private long id;

    @Column(name="template_name")
    private String templateName;
    
    @Column(name="subject")
    private String subject;

    @Column(name="template_content")
    @Type(type="text")
    private String templateContent;

    @Column(name = "is_active", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isActive;

	@Column(name = "is_deleted", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isDeleted;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "email_template_id", nullable = false, referencedColumnName = "id")
	private List<EmailTemplateVariable> emailTemplateVariable = new ArrayList<>();
	
	@Column(name = "SEARCH_STRING", length = 1000)
	private String searchString;

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
				.join(Arrays.asList(templateName, status, " "));
		this.searchString = StringUtils.substring(fullSearchString, 0, 999);
	}

	@Override
	public String toString() {
		return "UserEmailTemplate [id=" + id + ", templateName=" + templateName + ", subject=" + subject
				+ ", templateContent=" + templateContent + ", isActive=" + isActive + ", isDeleted=" + isDeleted
				+ ", emailTemplateVariable=" + emailTemplateVariable + ", searchString=" + searchString + "]";
	}

	public UserEmailTemplate(long id, String templateName, String subject, String templateContent, boolean isActive,
			boolean isDeleted, List<EmailTemplateVariable> emailTemplateVariable) {
		this.id = id;
		this.templateName = templateName;
		this.subject = subject;
		this.templateContent = templateContent;
		this.isActive = isActive;
		this.isDeleted = isDeleted;
		this.emailTemplateVariable = emailTemplateVariable;
	}

	public UserEmailTemplate() {
	}
	
}
