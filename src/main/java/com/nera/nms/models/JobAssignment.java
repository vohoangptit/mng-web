package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Arrays;
import java.util.Date;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity
public class JobAssignment extends CommonColumn {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="execution_date")
    private Date executionDate;

    @Column(name="start_time")
    private String startTime;

    @Column(name="end_time")
    private String endTime;

    @Column(name="status")
    private String status;

    @Column(name = "is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;

    @JsonIgnore
    @Column(name = "SEARCH_STRING", length = 1000)
    private String searchString;

    @PreUpdate
    @PrePersist
    void updateSearchString() {
        final String fullSearchString = StringUtils
                .join(Arrays.asList(assignee.getFullName(), jobManagement.getName(), " "));
        this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    }

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobManagement jobManagement;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "assignee")
    private User assignee;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "planner")
    private User planner;

    @Override
    public String toString() {
        return "JobAssignment{" +
                "id=" + id +
                ", executionDate=" + executionDate +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", reason=" + reason +
                '}';
    }

    @Column(name="reason")
    private String reason;

    @JsonIgnore
    private String jobName;

    @JsonIgnore
    private String workflowName;

    @JsonIgnore
    private String jobDescription;

    @JsonIgnore
    private String plannerName;

    @JsonIgnore
    private String assigneeName;
}
