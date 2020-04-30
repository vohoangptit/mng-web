package com.nera.nms.repositories;

import com.nera.nms.models.JobOperatorExecutionApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobOpsExeApprovalRepository extends JpaRepository<JobOperatorExecutionApproval,Long> {
    @Query("Select id from JobOperatorExecutionApproval where jobExeId =:id")
    Long findJobExeAppByJobId(@Param("id") Long jobId);
}
