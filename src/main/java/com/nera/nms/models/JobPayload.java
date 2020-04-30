package com.nera.nms.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="job_payload")
@Entity
public class JobPayload {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="job_input_id")
    private long jobInputId;

    @Column(name="value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobManagement jobManagement;
}
