package com.nera.nms.repositories;

import com.nera.nms.models.JobOperatorExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOpsExeRepository extends JpaRepository<JobOperatorExecution,Long> {

}
