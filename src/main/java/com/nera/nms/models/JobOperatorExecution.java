package com.nera.nms.models;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="job_operator_execution")
@Entity
public class JobOperatorExecution {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "job_exe_id", insertable = false, updatable = false)
    @JsonBackReference(value="jobOps")
    private JobExecution jobOps;

    @Column(name="execute_start")
    private Date executeStart;

    @Column(name="execute_end")
    private Date executeEnd;

    @Column(name="result")
    private String result;

    @Column(name="type")
    private Integer type;
}
