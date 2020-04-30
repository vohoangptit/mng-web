package com.nera.nms.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="job_operator_execution_approval")
@Entity
public class JobOperatorExecutionApproval {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="job_exe_id")
    private long jobExeId;

    @ManyToOne
    @JoinColumn(name = "job_exe_id", insertable = false, updatable = false)
    private JobExecution jobExec;

    @Column(name="ops_id")
    private long opsId;

    @Column(name="request_at")
    private Date requestAt;

    @Column(name="approve_at")
    private Date approveAt;

    @Column(name="approve_by")
    private String approveBy;

    @Column(name="result")
    private String result;
}
