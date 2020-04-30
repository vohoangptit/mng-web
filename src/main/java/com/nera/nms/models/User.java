package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nera.nms.constants.UserConstants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.TermVector;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "user_user")
@EqualsAndHashCode(callSuper=false)
@Indexed
public class User extends CommonColumn {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter @Setter
    private long id;

    @Transient
    @Getter @Setter
    private String recordId;
    
    @SortableField
    @Field(termVector = TermVector.YES)
    @Column(name="email")
    @Getter @Setter
    private String email;

    @Column(name="password")
    @Getter @Setter
    private String password;

    @SortableField
    @Field(termVector = TermVector.YES)
    @Column(name="fullname")
    @Getter @Setter
    private String fullName;

    @Transient
    @Getter @Setter
    private String departmentName;

    @Transient
    @Getter @Setter
    private String jobTitleName;

    @SortableField
    @Field(termVector = TermVector.YES)
    @Column(name="mobile_number")
    @Getter @Setter
    private String mobileNumber;

    @Column(name="last_login_date_time")
    @Getter @Setter
    private Timestamp lastLoginDateTime;
    
    @Transient
    @Getter @Setter
    private String formattedLastLogin;
    
    @Column(name = "image")
    @Type(type="text")
    @Getter @Setter
    private String image;

    @Column(name = "login_count", nullable = false)
    @Getter @Setter
    private long loginCount;

    @Type(type = "yes_no")
    @Column(name="is_active")
    @SortableField
    @Field(termVector = TermVector.YES)
    @Getter @Setter
    private boolean isActive;
    
    @Transient
    @Getter @Setter
    private String status;

    @Type(type = "yes_no")
    @Column(name="is_delete")
    @Getter @Setter
    private boolean isDeleted;

    @Transient
    @Getter @Setter
    private String groupNames;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = UserConstants.REFERENCE_GROUP_USER,
    joinColumns = {@JoinColumn(name = "user_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_group_id")}
    )
    @JsonBackReference
    @Getter @Setter
    List<Group> groups = new ArrayList<>();

    @Getter @Setter
    @Column(name="department_id")
    Long departmentId;

    @Getter @Setter
    @Column(name="job_title_id")
    Long jobTitleId;

    @Column
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Getter@Setter
    private Boolean firstLogin = true;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="assignee")
    List<JobAssignment> assignees = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="planner")
    List<JobAssignment> planners = new ArrayList<>();

    @Column(name = "SEARCH_STRING", length = 1000)
    @Getter @Setter
    private String searchString;

    @PreUpdate
    @PrePersist
    void updateSearchString() {
    	StringBuilder sbGroupName = new StringBuilder();
    	for (Group group: groups) {
            sbGroupName.append(group.getGroupName());
    	}

    	final String fullSearchString = StringUtils.join(Arrays.asList(
    			fullName,
    			email,
    			mobileNumber,
    			status,
    			departmentName,
    			jobTitleName,
                sbGroupName.toString(),
				lastLoginDateTime),
                " ");
    	this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    
    }

}
