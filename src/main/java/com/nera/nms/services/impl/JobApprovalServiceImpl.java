package com.nera.nms.services.impl;

import com.nera.nms.dto.JobApprovalRequestDTO;
import com.nera.nms.models.JobOperatorExecutionApproval;
import com.nera.nms.repositories.JobApprovalRepository;
import com.nera.nms.services.JobApprovalService;
import com.nera.nms.utils.BEDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobApprovalServiceImpl implements JobApprovalService {

    @Autowired
    private JobApprovalRepository jobApprovalRepository;

    @Override
    public Page<JobApprovalRequestDTO> getMyJobApprovalList(String searchString, Pageable pageable, String email) {
        return jobApprovalRepository.getJobApprovalRequestListWithConditions(searchString, pageable, email);
    }

    @Override
    public JobApprovalRequestDTO getByJobApprovalId(long id) {
        return jobApprovalRepository.getByJobApprovalId(id);
    }

    @Override
    public JobOperatorExecutionApproval getById(long id) {
        Optional<JobOperatorExecutionApproval> optional = jobApprovalRepository.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public JobOperatorExecutionApproval updateAfterRejectedOrApproved(long id, String action, String actionBy) {
        JobOperatorExecutionApproval jobApproval = getById(id);
        if (jobApproval == null) {
            return null;
        }
        jobApproval.setResult(StringUtils.upperCase(action));
        jobApproval.setApproveAt(BEDateUtils.getCurrentDate());

        return jobApprovalRepository.save(jobApproval);
    }
}
