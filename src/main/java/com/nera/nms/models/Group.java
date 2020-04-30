package com.nera.nms.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.nera.nms.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nera.nms.constants.UserConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Martin Do
 *
 */
@Entity
@Table(name="user_group")
public class Group extends CommonColumn {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Column(name="group_name")
    @Getter @Setter
    private String groupName;

    @Column(name="description")
    @Getter @Setter
    private String description;

    @Type(type = "yes_no")
    @Column(name="is_active")
    @Getter @Setter
    private boolean isActive;

    @Type(type = "yes_no")
    @Column(name="is_deleted")
    @Getter @Setter
    private boolean isDeleted;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = UserConstants.REFERENCE_GROUP_USER,
    joinColumns = {@JoinColumn(name = "user_group_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @JsonBackReference
    @Getter @Setter
    List<User> users = new ArrayList<>();
    
    @ManyToMany(cascade = {
            CascadeType.MERGE}, fetch= FetchType.LAZY)
    @JoinTable(name = UserConstants.REFERENCE_GROUP_ACCESS_PERMISSION ,
    joinColumns = {@JoinColumn(name = "user_group_id")},
    inverseJoinColumns = {@JoinColumn(name = "access_permission_name", referencedColumnName ="function_name")}
    )
    @JsonBackReference
    @Getter @Setter
    List<UserAccessPermission> userAccessPermissions = new ArrayList<>();

    @Column(name = "SEARCH_STRING", length = 1000)
    private String searchString;
    
    @PreUpdate
    @PrePersist
    void updateSearchString() {
    	String status = "";
    	if(isActive){
    		status = CommonConstants.ACTIVE;
    	} else{
    		status = "Inactive";
    	}
       final String fullSearchString = StringUtils.join(Arrays.asList(
               groupName,
               description,
               status,
               " "));
       this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    }
}
