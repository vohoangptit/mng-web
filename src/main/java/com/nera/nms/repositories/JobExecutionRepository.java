package com.nera.nms.repositories;

import com.nera.nms.models.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface JobExecutionRepository extends JpaRepository<JobExecution,Long> {
    JobExecution findOneById(Long id);

    @Query(value="select * from nera.job_execution j where j.job_id=:jobId", nativeQuery = true)
    JobExecution findJobByJobManagement(Long jobId);

    @Modifying
    @Transactional
    @Query(value = "delete from nera.job_execution where id =?1", nativeQuery = true)
    void deleteJobExecution(long jobExeId);
}
