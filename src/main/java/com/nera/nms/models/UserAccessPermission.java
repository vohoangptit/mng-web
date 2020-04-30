package com.nera.nms.models;

import java.util.ArrayList;
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
import javax.persistence.Table;

import com.nera.nms.constants.UserConstants;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Entity
@Table(name="user_access_permission")
@Data
public class UserAccessPermission {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name="function_name")
    private String functionName;

    @Column(name="is_enabled")
    private Boolean isEnabled;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = UserConstants.REFERENCE_GROUP_ACCESS_PERMISSION ,
    joinColumns = {@JoinColumn(name = "access_permission_name", referencedColumnName ="function_name")},
    inverseJoinColumns = {@JoinColumn(name = "user_group_id")}
    )
    List<Group> groups = new ArrayList<>();
}
