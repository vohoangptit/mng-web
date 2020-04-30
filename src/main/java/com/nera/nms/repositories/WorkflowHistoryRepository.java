package com.nera.nms.repositories;


import com.nera.nms.models.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {

    List<WorkflowHistory> findByWorkflowId(Long id);

    WorkflowHistory getById(Long id);
}
