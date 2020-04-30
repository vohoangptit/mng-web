package com.nera.nms.models;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Entity
@Table(name="system_audit_log")
@Data
public class SystemAuditLog {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    
    @Transient
    @Column(name ="row_up")
    private long rowNunber;

    @Column(name="user_name")
    private String userName;
    
    @Column(name="user_group_name")
    private String userGroupName;
    
    @Column(name="description", length = 500)
    private String description;
    
    @Column(name="details")
    private String details;
    
    @Column(name="payload")
    private String payload;
    
    @Column(name="status")
    private String status;
    
    @Column(name="action_date")
    private Date actionDate;
    
    @Column(name = "SEARCH_STRING", length = 1000)
    private String searchString;
    
    @PreUpdate
    @PrePersist
    void updateSearchString() {
       final String fullSearchString = StringUtils.join(Arrays.asList(
               userName,
               userGroupName,
               description,
               details,
               status,
               actionDate,
               " "));
       this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    }
}
