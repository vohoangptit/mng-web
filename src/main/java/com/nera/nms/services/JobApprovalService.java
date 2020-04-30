package com.nera.nms.services;

import com.nera.nms.dto.JobApprovalRequestDTO;
import com.nera.nms.models.JobOperatorExecutionApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobApprovalService {

    Page<JobApprovalRequestDTO> getMyJobApprovalList(String searchString, Pageable pageable, String email);

    JobApprovalRequestDTO getByJobApprovalId(long id);

    JobOperatorExecutionApproval getById(long id);

    JobOperatorExecutionApproval updateAfterRejectedOrApproved(long id, String action, String actionBy );
}
