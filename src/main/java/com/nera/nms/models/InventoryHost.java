package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false, exclude={"groups"})
@JsonIgnoreProperties("inventory")
@Table(name="inventory_host", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
@Entity
public class InventoryHost {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="ip_address")
    private String ipAddress;
    
    @Column(name="port")
    private int port;
    
    @Column(name="description")
    private String description;

    @JsonIgnore
    @Column(name="username")
    private String username;

    @JsonIgnore
    @Column(name="password")
    private String password;
    
    @Column(name="is_active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive = true;
    
    @Column(name="is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "host_group_host" ,
    joinColumns = {@JoinColumn(name = "host_id")},
    inverseJoinColumns = {@JoinColumn(name = "group_host_id")}
    )
    @JsonBackReference(value="groups")
    private Set<InventoryGroupHost> groups;
    
    @JsonIgnore
    @Column(name="create_date")
    private Date createdDate = new Date();

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "job_host" ,
            joinColumns = {@JoinColumn(name = "host_id")},
            inverseJoinColumns = {@JoinColumn(name = "job_id")}
    )
    @JsonBackReference(value="jobs")
    private Set<JobManagement> jobs;
    
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

	@Override
	public String toString() {
		return "InventoryHost [id=" + id + ", name=" + name + ", ipAddress=" + ipAddress + ", port=" + port
				+ ", description=" + description + ", isActive=" + isActive + ", isDeleted=" + isDeleted
				+ ", createdDate=" + createdDate + ", createdBy=" + createdBy + ", modifiedDate="
				+ modifiedDate + ", modifiedBy=" + modifiedBy + "]";
	}

	@Transient
	private long no;
}
