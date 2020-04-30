package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nera.nms.constants.CommonConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false, exclude={"hosts"})
@Table(name="job", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
@Entity
public class JobManagement {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="name")
    private String name;

    @Column(name="description", length=800)
    private String description;

    @Column(name="workflow_name")
    private String workflowName;

    @Column(name="workflow_id")
    private long workflowID = 0;

    @Column(name = "is_active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean deleted;

    @JsonIgnore
    @Column(name = "SEARCH_STRING", length = 1000)
    private String searchString;

    @Column(name="create_date")
    private Date createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="modified_date")
    private Date modifiedDate;

    @Column(name="modified_by")
    private String modifiedBy;

    @ManyToMany(mappedBy = "jobs", cascade = CascadeType.ALL)
    private Set<InventoryHost> hosts = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobManagement")
    List<JobPayload> jobPayload = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "jobManagement")
    List<JobAssignment> jobAssignments = new ArrayList<>();

    @OneToOne(mappedBy= "job")
    private JobExecution jobExecution;

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
                .join(Arrays.asList(name, workflowName, description, status, " "));
        this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    }

    @Transient
    private long no;
}
