package com.nera.nms.repositories;

import com.nera.nms.models.JobManagement;
import com.nera.nms.models.JobPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface JobPayLoadRepository extends JpaRepository<JobPayload, Long> {

    JobPayload findOneById(Long id);

    List<JobPayload> findAllByJobManagementEquals(JobManagement jobManagement);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nera.job_payload where job_id =?1", nativeQuery = true)
    void deleteJobPayload(Long id);
}
