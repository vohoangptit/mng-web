package com.nera.nms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Entity
@Table(name="system_setting")
@Data
public class SystemSetting {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name="email")
    private String email;

    @Column(name="smtp_host")
    private String smtpHost;
    
    @Column(name="smtp_port")
    private String smtpPort;
    
    @Column(name="smtp_security")
    @Type(type = "yes_no")
    private boolean sbSmtpSecurity;
    
    @Column(name="username")
    private String username;
    
    @Column(name="password")
    private String password;

    @Column(name="job_planning_config", nullable = false)
    private int jobPlanningConfig;

}
