package com.nera.nms.repositories;

import com.nera.nms.models.WorkflowOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface WorkflowOperatorRepository extends JpaRepository<WorkflowOperator, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from nera.workflow_operator where workflow_id = :wfId", nativeQuery = true)
    void deleteByWorkflowId(long wfId);

    List<WorkflowOperator> findByContentXMLLike(String contentXml);
}
