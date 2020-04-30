package com.nera.nms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="job_input")
@Entity
public class JobInput{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    
    @Column(name="type")
    private Integer type;
    
    @Column(name="variable")
    private String variable;
    
    @Column(name="value")
    private String value;
    
    @ManyToOne
	@JoinColumn(name = "workflow_id", insertable = false, updatable = false, referencedColumnName = "id")
    @JsonBackReference
	private Workflow workflow;
}
