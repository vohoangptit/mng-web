package com.nera.nms.services;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.JobExeDTO;
import com.nera.nms.models.JobExecution;
import com.nera.nms.models.JobOperatorExecution;
import com.nera.nms.models.JobOperatorExecutionApproval;
import com.nera.nms.repositories.JobExecutionRepository;
import com.nera.nms.repositories.JobManagementRepository;
import com.nera.nms.repositories.JobOpsExeApprovalRepository;
import com.nera.nms.repositories.JobOpsExeRepository;
import com.nera.nms.utils.BEDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobOpsExeApprovalService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JobOpsExeApprovalRepository jobOpsExeApprovalRepository;
    @Autowired
    private JobOpsExeRepository jobOpsExeRepository;
    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private JobManagementRepository jobManagementRepository;

    public JobExecution findById(long jobexeid){
        return jobExecutionRepository.findOneById(jobexeid);
    }

    public boolean saveJobOpsExeApproval(String jobExeId, String approveBy){
        String[] arr = StringUtils.split(approveBy, CommonConstants.COMMA);
        List<JobOperatorExecutionApproval> list = new ArrayList<>();
        JobOperatorExecutionApproval jobOpExApp;
        try {
            Long id =jobOpsExeApprovalRepository.findJobExeAppByJobId(Long.parseLong(jobExeId));
            for(String approver : arr) {
                jobOpExApp = new JobOperatorExecutionApproval();
                if(id != null) {
                    jobOpExApp.setId(id);
                }
                jobOpExApp.setRequestAt(BEDateUtils.getCurrentDate());
                jobOpExApp.setJobExeId(Long.parseLong(jobExeId));
                if (StringUtils.isNotBlank(approver)) {
                    jobOpExApp.setApproveBy(StringUtils.trim(approver));
                    list.add(jobOpExApp);
                }
            }
            list.forEach(jobOperatorExecutionApproval -> jobOpsExeApprovalRepository.save(jobOperatorExecutionApproval));
        }
        catch (Exception e)
        {
            logger.error("Exception : JobOpsExeApprovalService.saveJobOpsExeApproval ", e);
            return false;
        }
        return true;
    }

    public long saveJobExecution(JobExeDTO body){
        try {
            List<JobOperatorExecution> jobOperatorExecutionList = new ArrayList<>();
            JobExecution entity = jobExecutionRepository.findJobByJobManagement(body.getJobId());
            if(entity != null && entity.getJob() != null) {
                jobOperatorExecutionList.addAll(entity.getJobExeList());
            } else {
                entity = new JobExecution();
                entity.setJob(jobManagementRepository.findOneById(body.getJobId()));
            }
            entity.setExecuteEnd(body.getExecuteEnd());
            entity.setExecuteStart(body.getExecuteStart());
            entity.setLog(body.getLog());
            entity.setResult(body.getResult());
            body.getJobOperatorExecutions().forEach(item->jobOperatorExecutionList.add(item.dto2Entity()));
            entity.setJobExeList(jobOperatorExecutionList);
            entity = jobExecutionRepository.save(entity);
            return entity.getId();
        }
        catch (Exception e) {
            logger.error("Exception : JobOpsExeApprovalService.saveJobExecution ", e);
        }
        return 0;
    }

    public boolean deleteJobExecution(Long jobId) {
        try {
            JobExecution entity = jobExecutionRepository.findJobByJobManagement(jobId);
            if(entity != null) {
                jobOpsExeApprovalRepository.deleteInBatch(entity.getJobOperatorExecutionApprovalList());
                jobOpsExeRepository.deleteInBatch(entity.getJobExeList());
                jobExecutionRepository.deleteJobExecution(entity.getId());
                return true;
            }
        } catch(Exception e) {
            logger.error("Exception : JobOpsExeApprovalService.deleteJobExecution ", e);
        }
        return false;
    }
}
