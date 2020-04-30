package com.nera.nms.services;

import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.dto.JobDTO;
import com.nera.nms.dto.JobViewDTO;
import com.nera.nms.dto.JobWorkflowDTO;
import com.nera.nms.models.InventoryHost;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.JobManagement;
import com.nera.nms.models.JobPayload;
import com.nera.nms.repositories.JobExecutionRepository;
import com.nera.nms.repositories.JobManagementRepository;
import com.nera.nms.repositories.JobPayLoadRepository;
import com.nera.nms.repositories.JobPlanRepository;
import com.nera.nms.utils.BeanConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobManagementRepository jobManagementRepository;

    @Autowired
    private JobPayLoadRepository jobPayLoadRepository;

    @Autowired
    private JobPlanRepository jobAssignmentRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private JobOpsExeApprovalService jobOpsExeApprovalService;

    public JobManagement findJobById(Long id) {
        return jobManagementRepository.findOneById(id);
    }

    public JobManagement findJobByName(String jobName) {
        return jobManagementRepository.findOneByName(jobName);
    }

    public JobManagement findOneByNameAndId(Long id, String jobName) {
        return jobManagementRepository.findJobManagementByIdIsNotLikeAndNameEquals(id, jobName);
    }

    public boolean addNewJob(JobManagement jobManagement) {
        if(jobManagement != null) {
            jobManagementRepository.save(jobManagement);
            return true;
        }
        return false;
    }

    public boolean updateJob(JobDTO jobDTO) {
        JobManagement jobManagement = findJobById(jobDTO.getId());
        jobDTO.dtoToEntity(jobManagement);
        jobManagementRepository.save(jobManagement);
        return true;
    }

    public String updateWorkflowJob(JobWorkflowDTO jobWorkflowDTO, String nameUser) {
        JobManagement jobManagement = findJobById(jobWorkflowDTO.getJobId());
        if(jobManagement != null) {
            jobManagement.setWorkflowID(jobWorkflowDTO.getWorkflowId());
            jobManagement.setWorkflowName(jobWorkflowDTO.getWorkflowName());
            jobManagement.setModifiedBy(nameUser);
            jobManagement.setModifiedDate(new Date());
            jobManagementRepository.save(jobManagement);

            Iterable<JobPayload> payloadListDelete = jobPayLoadRepository.findAllByJobManagementEquals(jobManagement);
            jobPayLoadRepository.deleteInBatch(payloadListDelete);

            List<JobPayload> jobPayloadList = jobWorkflowDTO.getJobPayload().stream().map(payLoadItem -> {
                JobPayload jobPayload = new JobPayload();
                payLoadItem.dtoToEntity(jobPayload);
                jobPayload.setJobManagement(findJobById(payLoadItem.getJobId()));
                return jobPayload;
            }).collect(Collectors.toList());
            Iterable<JobPayload> iterable = jobPayloadList;
            jobPayLoadRepository.saveAll(iterable);
            return jobManagement.getName();
        }
        return StringUtils.EMPTY;
    }

    public String addHostJobDetail(JobDTO jobDTO, String userName) {
        try {
            if (!jobDTO.getHostsId().isEmpty()) {
                JobManagement jobManagement = findJobById(jobDTO.getId());
                Set<Long> listHostId = new HashSet<>(jobDTO.getHostsId());
                if(!jobManagement.getHosts().isEmpty()) {
                    jobManagementRepository.deleteJobHostDetailByJobID(jobManagement.getId());
                    for(InventoryHost item : jobManagement.getHosts()) {
                        listHostId.add(item.getId());
                    }
                }
                listHostId.forEach(n -> jobManagementRepository.saveJobHostDetail(jobManagement.getId(), n));
                jobManagement.setModifiedDate(new Date());
                jobManagement.setModifiedBy(userName);
                jobManagementRepository.save(jobManagement);
                return jobManagement.getName();
            }
        } catch(Exception e) {
            logger.error("Exception: JobService.addHostJobDetail ", e);
        }
        return StringUtils.EMPTY;
    }

    public String removeHostJobDetail(JobDTO jobDTO, String userName) {
        try {
            JobManagement jobManagement = findJobById(jobDTO.getId());
            jobDTO.getHostsId().forEach(j -> jobManagementRepository.deleteJobHostDetail(jobManagement.getId(), j));
            jobManagement.setModifiedDate(new Date());
            jobManagement.setModifiedBy(userName);
            jobManagementRepository.save(jobManagement);
            return jobManagement.getName();
        } catch(Exception e) {
            logger.error("Exception: JobService.removeHostJobDetail ", e);
        }
        return StringUtils.EMPTY;
    }

    public List<Number> getListJobHost(long jobId) {
        return jobManagementRepository.getListJobHost(jobId);
    }

    public List<JobManagement> getListJobByWorkflowId(long workflowId) {
        return jobManagementRepository.findAllByDeletedFalseAndWorkflowIDEquals(workflowId);
    }

    public void updateJobManagementByWorkflow(long workflowID , String jobName) {
        List<JobManagement> listJob = getListJobByWorkflowId(workflowID);
        if (!listJob.isEmpty()) {
            for(JobManagement item : listJob) {
                if(!StringUtils.equals(item.getName(), jobName)) {
                    item.setWorkflowName(jobName);
                }
            }
            jobManagementRepository.saveAll(listJob);
        }
    }

    public void deleteJob(long jobId) {
        try {
            JobManagement jobManagement = findJobById(jobId);
            if(!jobManagement.getJobAssignments().isEmpty()) {
                for (JobAssignment jobAssignment : jobManagement.getJobAssignments()) {
                    jobAssignmentRepository.deleteJobAssignById(jobAssignment.getId());
                }
            }
            if(jobManagement.getJobExecution() != null) {
                jobOpsExeApprovalService.deleteJobExecution(jobId);
            }
            if(CollectionUtils.isNotEmpty(jobManagement.getJobPayload())) {
                jobPayLoadRepository.deleteJobPayload(jobId);
            }
            jobManagementRepository.deleteJobHostDetailByJobID(jobId);
            jobManagementRepository.deleteJobCustom(jobManagement.getId());
        } catch(Exception e) {
            logger.error("JobService.deleteJob : ", e);
        }
    }

    public Set<String> findAllCreatedPerson() {
        return jobManagementRepository.findAllCreatedPerson();
    }

    public Page<JobManagement> findByStatusDependency(Pageable pageable, String searchString, Boolean jobActive, String createdBy, Boolean dependency) {
        return jobManagementRepository.findByStatusDependency(jobActive, createdBy, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByEndDateDependency(Pageable pageable, String searchString, Date endDate, String createdBy, Boolean dependency) {
        return jobManagementRepository.findByEndDateDependency(endDate, createdBy, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByStartDateDependency(Pageable pageable, String searchString, Date startDate, String createdBy, Boolean dependency) {
        return jobManagementRepository.findByStartDateDependency(startDate, createdBy, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByStartEndDateDependency(Pageable pageable, String searchString, Date startDate, Date endDate, String createdBy, Boolean dependency) {
        return jobManagementRepository.findByStartEndDateDependency(startDate, endDate, createdBy, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByEndDateAndStatusDependency(Pageable pageable, String searchString, Date endDate, String createdBy, Boolean jobActive, Boolean dependency) {
        return jobManagementRepository.findByEndDateAndStatusDependency(endDate, createdBy, jobActive, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByStartDateAndStatusDependency(Pageable pageable, String searchString, Date startDate, String createdBy, Boolean jobActive, Boolean dependency) {
        return jobManagementRepository.findByStartDateAndStatusDependency(startDate, createdBy, jobActive, searchString, dependency, pageable);
    }

    public Page<JobManagement> findByAllConditionDependency(Pageable pageable, String searchString, Date startDate, Date endDate, String createdBy, Boolean jobActive, Boolean dependency) {
        return jobManagementRepository.findByAllConditionDependency(startDate, endDate, createdBy, jobActive, searchString,  dependency, pageable);
    }

    public Page<JobManagement> findJobByHostActive(Pageable pageable, boolean active) {
        return jobManagementRepository.findJobByHostActive(pageable, active);
    }

    public Page<JobManagement> findAllRecord(Pageable pageable, String searchString, String createdBy) {
        return jobManagementRepository.findAllRecord(searchString,createdBy,pageable);
    }

    public Page<JobManagement> findByAllCondition(Pageable pageable, String searchString, Date startDate, Date endDate, String createdBy, Boolean jobActive) {
        return jobManagementRepository.findByAllCondition(startDate, endDate, createdBy, jobActive, searchString,  pageable);
    }

    public Page<JobManagement> findByStartDateAndStatus(Pageable pageable, String searchString, Date startDate, String createdBy, Boolean jobActive) {
        return jobManagementRepository.findByStartDateAndStatus(startDate, createdBy, jobActive, searchString, pageable);
    }

    public Page<JobManagement> findByEndDateAndStatus(Pageable pageable, String searchString, Date endDate, String createdBy, Boolean jobActive) {
        return jobManagementRepository.findByEndDateAndStatus(endDate, createdBy, jobActive, searchString, pageable);
    }

    public Page<JobManagement> findByStartEndDate(Pageable pageable, String searchString, Date startDate, Date endDate, String createdBy) {
        return jobManagementRepository.findByStartEndDate(startDate, endDate, createdBy, searchString, pageable);
    }

    public Page<JobManagement> findByStartDate(Pageable pageable, String searchString, Date startDate, String createdBy) {
        return jobManagementRepository.findByStartDate(startDate, createdBy, searchString, pageable);
    }

    public Page<JobManagement> findByEndDate(Pageable pageable, String searchString, Date endDate, String createdBy) {
        return jobManagementRepository.findByEndDate(endDate, createdBy, searchString, pageable);
    }

    public Page<JobManagement> findByStatus(Pageable pageable, String searchString, Boolean jobActive, String createdBy) {
        return jobManagementRepository.findByStatus(jobActive, createdBy, searchString, pageable);
    }
    
    public List<JobViewDTO> getListJobForPlanning() {
        List<JobManagement> tmp = jobManagementRepository.findAllNoPaging();
        List<JobManagement> result = new ArrayList<>();
        tmp.forEach(i-> {
            if(CollectionUtils.isEmpty(i.getJobAssignments())) {
                result.add(i);
            } else {
                i.getJobAssignments().forEach(jobAssignment -> {
                    if(!JobPlanStatus.EXECUTING.equals(jobAssignment.getStatus())
                            && !JobPlanStatus.FINISHED_REJECTED.equals(jobAssignment.getStatus())
                            && !JobPlanStatus.FINISHED_APPROVED.equals(jobAssignment.getStatus())
                            && !JobPlanStatus.REJECTED.equals(jobAssignment.getStatus())){
                        result.add(i);
                    }
                });
            }
        });
        return BeanConvertUtils.copyList(result,JobViewDTO.class);
	}
    
    public JobViewDTO findViewJobById(Long id) {
        return BeanConvertUtils.createAndCopy(jobManagementRepository.findOneById(id), JobViewDTO.class);
    }

    public boolean isUsedByWorkflow(long workflowId) {
        return jobManagementRepository.countJobByWorkflowId(workflowId) > 0;
    }
}
