package com.nera.nms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Entity
@Table(name="user_job_title")
@Data
public class UserJobTitle {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name="job_title_name")
    private String jobTitleName;

    @Type(type = "yes_no")
    @Column(name="is_enabled")
    private boolean isEnabled;
}
