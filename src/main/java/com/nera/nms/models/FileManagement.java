package com.nera.nms.models;

import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.thymeleaf.util.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="file_management", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
@Entity
public class FileManagement extends CommonColumn{
	
	
	
	public FileManagement() {
		super();
	}

	public FileManagement(String name, String description, String filename, String createdBy, Timestamp createdDate, boolean isActive,
			boolean isDeleted) {
		super();
		this.name = name;
		this.filename = filename;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.isActive = isActive;
		this.isDeleted = isDeleted;
	}

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="filename")
    private String filename;
    
    @Column(name="description")
    private String description;
    
    @Column(name="source_url")
    private String sourceUrl;
    
    @Column(name="created_by")
    private String createdBy;
    
    @Column(name="created_date")
    private Timestamp createdDate;
    
    @Column(name="is_active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;
    
    @Column(name="is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;
    
    @Transient
    @Getter @Setter
    private String status;
    
    @Transient
    @Getter @Setter
    private String createdByDate;
    
    @Transient
    @Getter @Setter
    private long no;
    
    @Column(name = "SEARCH_STRING", length = 1000)
    @Getter @Setter
    private String searchString;

    @Column(name = "version", nullable = false)
    private int version = 1;
    
    @PreUpdate
    @PrePersist
    void updateSearchString() {
    	if (isActive) {
    		status = CommonConstants.ACTIVE;
    	} else {
    		status = CommonConstants.INACTIVE;
    	}
    	
    	String createdDateFormatted = DateUtils.format(createdDate, "dd/MM/yyyy", new Locale("en"));

    	final String fullSearchString = StringUtils.join(Arrays.asList(
    			name,
    			status,
    			filename,
    			createdBy,
    			createdDateFormatted),
                " ");

    	this.searchString = StringUtils.substring(fullSearchString, 0, 999);

    }

    public int getVersion() {
        return version == 0 ? version + 1 : version;
    }
}
