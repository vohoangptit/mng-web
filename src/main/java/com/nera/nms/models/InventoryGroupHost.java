package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false, exclude={"hosts"})
@Table(name="inventory_group_host", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
@Entity
public class InventoryGroupHost{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="is_active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;
    
    @Column(name="is_deleted")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;

    @ManyToMany(mappedBy = "groups")
    private Set<InventoryHost> hosts;

    @JsonIgnore
    @Column(name="create_date")
    private Date createdDate;

    @JsonIgnore
    @Column(name="created_by")
    private String createdBy;

    @JsonIgnore
    @Column(name="modified_date")
    private Date modifiedDate;

    @JsonIgnore
    @Column(name="modified_by")
    private String modifiedBy;

    @JsonIgnore
    @Column(name = "SEARCH_STRING", length = 1000)
	private String searchString;
    
    @JsonIgnore
    @PreUpdate
	@PrePersist
	void updateSearchString() {
		String status;
    	if (isActive) {
    		status = CommonConstants.ACTIVE;
    	} else {
    		status = CommonConstants.INACTIVE;
    	}
		final String fullSearchString = StringUtils
				.join(Arrays.asList(name, status, " "));
		this.searchString = StringUtils.substring(fullSearchString, 0, 999);
	}
    
	@Transient
	private long no = 0;

	@Transient
	private int numberHost;
}
