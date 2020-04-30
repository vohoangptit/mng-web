package com.nera.nms.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false, exclude = {"job"})
@Table(name="job_execution")
@Entity
public class JobExecution {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) private long id;

    @Column(name="execute_start")
    private Date executeStart;

    @Column(name="execute_end")
    private Date executeEnd;

    @Column(name="result")
    private String result;

    @Column(name="log", length = 8000)
    private String log;

    @OneToOne
    @JoinColumn(name = "job_id")
    private JobManagement job;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="job_exe_id")
    List<JobOperatorExecution> jobExeList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST,mappedBy= "jobExec")
    List<JobOperatorExecutionApproval> jobOperatorExecutionApprovalList = new ArrayList<>();
}
