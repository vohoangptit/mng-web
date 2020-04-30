package com.nera.nms.services;

import com.google.common.base.Strings;
import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.dto.*;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.JobManagement;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.JobApprovalRepository;
import com.nera.nms.repositories.JobManagementRepository;
import com.nera.nms.repositories.JobPlanRepository;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.BeanConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class JobAssignmentService {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobPlanRepository jobAssignmentRepository;

    private final JobManagementRepository jobManagementRepository;

    private final SystemSettingService systemSettingService;

    private final EmailService emailService;

    private final ISystemAuditLogRepository iSystemAuditLogRepository;

    private final JobApprovalRepository jobApprovalRepository;

    private static final String EXECUTE_JOB = "Execute Job";
    private static final String APPROVED_JOB_EXEC = "Approved Job Execution";

    public JobAssignmentService(JobApprovalRepository jobApprovalRepository, JobPlanRepository jobAssignmentRepository, JobManagementRepository jobManagementRepository, SystemSettingService systemSettingService, EmailService emailService, ISystemAuditLogRepository iSystemAuditLogRepository) {
        this.jobApprovalRepository = jobApprovalRepository;
        this.jobAssignmentRepository = jobAssignmentRepository;
        this.jobManagementRepository = jobManagementRepository;
        this.systemSettingService = systemSettingService;
        this.emailService = emailService;
        this.iSystemAuditLogRepository = iSystemAuditLogRepository;
    }

    public Page<JobAssignment> findAllWithSearchString(Pageable pageable, String searchString, String status,
                                                       String startDate, String endDate) throws ParseException {
        if (!Strings.isNullOrEmpty(status) && !Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return jobAssignmentRepository.findLikeSearchStringAndCondition(searchString, status, start, end, pageable);
        } else if (!Strings.isNullOrEmpty(status)) {
            return jobAssignmentRepository.findLikeSearchStringAndStatus(searchString, status, pageable);
        } else if (!Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return jobAssignmentRepository.findLikeSearchStringAndDate(searchString, start, end, pageable);
        } else {
            return jobAssignmentRepository.findLikeSearchString(searchString, pageable);
        }
    }

    public JobAssignment saveJobPlanning(JobAssignmentInsertDTO dto, User assignee, User planner) {
        JobManagement jobManagement = jobManagementRepository.findOneById(dto.getJobId());
        JobAssignment jobAssignment = BeanConvertUtils.createAndCopy(dto, JobAssignment.class);
        jobAssignment.setJobManagement(jobManagement);
        // set new field to sort
        jobAssignment.setPlannerName(planner.getFullName());
        jobAssignment.setAssigneeName(assignee.getFullName());
        jobAssignment.setJobName(jobManagement.getName());
        jobAssignment.setJobDescription(jobManagement.getDescription());
        jobAssignment.setWorkflowName(jobManagement.getWorkflowName());
        jobAssignment.setAssignee(assignee);
        jobAssignment.setPlanner(planner);
        jobAssignment.setCreatedDate(new Date());
        jobAssignment.setStatus(JobPlanStatus.PENDING);
        // end set new field
        emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 4L, false);
        return jobAssignmentRepository.save(jobAssignment);
    }

    public boolean updateJobPlanning(JobAssignmentInsertDTO dto, User assignee, User planner) {
        boolean checkAssignee = false;
        JobManagement jobManagement = jobManagementRepository.findOneById(dto.getJobId());
        JobAssignment jobAssignment = jobAssignmentRepository.getById(dto.getId());
        if (assignee.getId() != jobAssignment.getAssignee().getId()) {
            jobAssignment.setStatus(JobPlanStatus.PENDING);
        }
        jobAssignment.setExecutionDate(dto.getExecutionDate());
        jobAssignment.setStartTime(dto.getStartTime());
        jobAssignment.setEndTime(dto.getEndTime());
        jobAssignment.setJobManagement(jobManagement);
        if (!StringUtils.equalsIgnoreCase(jobAssignment.getAssignee().getEmail(), assignee.getEmail())) {
            emailService.sendJobAssignmentEmail(jobAssignment, assignee, planner, 4L, false);
            emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 6L, false);
            checkAssignee = true;
        } else {
            emailService.sendJobAssignmentEmail(jobAssignment, assignee, planner, 5L, false);
        }
        jobAssignment.setAssignee(assignee);
        jobAssignment.setPlanner(planner);
        // set new field to sort
        jobAssignment.setPlannerName(planner.getFullName());
        jobAssignment.setAssigneeName(assignee.getFullName());
        jobAssignment.setJobName(jobManagement.getName());
        jobAssignment.setJobDescription(jobManagement.getDescription());
        jobAssignment.setWorkflowName(jobManagement.getWorkflowName());
        // end set new field
        jobAssignment.setModifiedDate(new Date());
        jobAssignmentRepository.save(jobAssignment);
        return checkAssignee;
    }

    public void autoUpdateJobInfo(JobAssignment data) {
        jobAssignmentRepository.save(data);
    }

    public JobAssignmentDTO getById(long id) {
        JobAssignment entity = jobAssignmentRepository.getById(id);
        return new JobAssignmentDTO(entity);
    }

    public JobAssignment getJobAssignmentById(long id) {
        return jobAssignmentRepository.getById(id);
    }

    public List<JobAssignmentDTO> findAll() {
        List<JobAssignmentDTO> data = new ArrayList<>();
        jobAssignmentRepository.findAll().forEach(a ->
                data.add(new JobAssignmentDTO(a))
        );
        return data;
    }

    public List<JobCalendarViewDTO> getSourceForCalendar() {
        List<JobCalendarViewDTO> data = new ArrayList<>();
        jobAssignmentRepository.findAll().forEach(a ->
                data.add(new JobCalendarViewDTO(a))
        );
        return data;
    }

    public void deleteById(long id) {
        JobAssignment jobAssignment = jobAssignmentRepository.getById(id);
        emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 6L, false);
        jobAssignmentRepository.delete(jobAssignment);
    }

    public void findJobAssignPending() {
        List<JobAssignment> jobAssignments = jobAssignmentRepository.findAllJobByStatus(JobPlanStatus.PENDING);
        if (CollectionUtils.isNotEmpty(jobAssignments)) {
            List<JobAssignment> listJobReject = new ArrayList<>();
            Calendar calCurrentTime = Calendar.getInstance();
            calCurrentTime.setTime(new Date());
            logger.info("Current Time: {}", calCurrentTime.getTime());
            SystemSettingDto systemSettingDto = systemSettingService.findSystemSetting();
            Calendar calJobAssign = Calendar.getInstance();
            for (JobAssignment item : jobAssignments) {
                if (item.getModifiedDate() != null) {
                    calJobAssign.setTime(item.getModifiedDate());
                } else {
                    calJobAssign.setTime(item.getCreatedDate());
                }
                calJobAssign.add(Calendar.HOUR, systemSettingDto.getJobPlanningConfig());
                if (calCurrentTime.getTimeInMillis() >= calJobAssign.getTimeInMillis()) {
                    item.setStatus(JobPlanStatus.REJECTED);
                    listJobReject.add(item);
                    emailService.sendJobAssignmentEmail(item, item.getAssignee(), item.getPlanner(), 9L, true);
                    SystemAuditLog systemAuditLog = new SystemAuditLog();
                    CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                    commonMethodComponent.addAuditLogData(systemAuditLog,
                            "System",
                            "System",
                            "Job has been rejected by System",
                            "Rejected Job Planning Successful",
                            true);
                    iSystemAuditLogRepository.save(systemAuditLog);
                }
            }
            if (CollectionUtils.isNotEmpty(listJobReject)) {
                jobAssignmentRepository.saveAll(listJobReject);
            }
        }
    }

    public Page<JobAssignment> findMyJobWithSearchString(Pageable pageable, String searchString, String status,
                                                         String startDate, String endDate, User assignee) throws ParseException {
        if (!Strings.isNullOrEmpty(status) && !Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return jobAssignmentRepository.findMyJobLikeSearchStringAndCondition(searchString, status, start, end, assignee, pageable);
        } else if (!Strings.isNullOrEmpty(status)) {
            return jobAssignmentRepository.findMyJobLikeSearchStringAndStatus(searchString, status, assignee, pageable);
        } else if (!Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return jobAssignmentRepository.findMyJobLikeSearchStringAndDate(searchString, start, end, assignee, pageable);
        } else {
            return jobAssignmentRepository.findMyJobLikeSearchString(searchString, assignee, pageable);
        }
    }

    public List<JobCalendarViewDTO> findAllMyJob(User assignee) {
        List<JobCalendarViewDTO> data = new ArrayList<>();
        jobAssignmentRepository.findByAssignee(assignee).forEach(a ->
                data.add(new JobCalendarViewDTO(a))
        );
        return data;
    }

    public void updateStatus(long id, boolean status, String reason) {
        JobAssignment jobAssignment = jobAssignmentRepository.getById(id);
        jobAssignment.setStatus(status ? JobPlanStatus.ACCEPTED : JobPlanStatus.REJECTED);
        jobAssignment.setReason(reason);
        jobAssignmentRepository.save(jobAssignment);
        if (status) {
            emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 7L, true);
        } else {
            emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 8L, true);
        }
    }

    public void updateFinishStatusJob(long id, String status) {
        JobAssignment jobAssignment = jobAssignmentRepository.getByIdAndStatus(id, "Executing");
        jobAssignment.setStatus(status);
        jobAssignment.setReason(StringUtils.EMPTY);
        jobAssignmentRepository.save(jobAssignment);
        emailService.sendJobAssignmentEmail(jobAssignment, jobAssignment.getAssignee(), jobAssignment.getPlanner(), 7L, true);
    }

    public JobAssignment updateStatusJobExe(long id, String status, String reason) {
        JobAssignment jobAssignment = jobAssignmentRepository.getById(id);
        jobAssignment.setStatus(status);
        jobAssignment.setReason(reason);
        return jobAssignmentRepository.save(jobAssignment);
    }

    public boolean updateAfterRejectedOrApproved(long id, String status, String modifyBy) {
        try {
            JobAssignment jobAssignment = jobAssignmentRepository.getById(id);
            jobAssignment.setStatus(status);
            jobAssignment.setModifiedBy(modifyBy);
            jobAssignment.setModifiedDate(BEDateUtils.getCurrentDate());
            jobAssignmentRepository.save(jobAssignment);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    public List<JobAssignment> findByJobId(long id) {
        return jobAssignmentRepository.findByJobId(id);
    }

    public List<JobAssignment> findByAssigneeId(long id) {
        return jobAssignmentRepository.findJobByPlanner(id);
    }

    public List<JobAssignment> findByPlannerId(long id) {
        return jobAssignmentRepository.findJobByPlanner(id);
    }

    public List<JobAssignment> findJobAssigmentByJobIdAndStatus(long id) {
        return jobAssignmentRepository.findJobAssigmentByJobIdAndStatus(id);
    }

    public Map<String, Integer> countStatusJob() {
        List<Object[]> jobs = jobAssignmentRepository.countByStatus();
        Set<StatisticJobDTO> result = new HashSet<>();
        JobPlanStatus.getJobPlanningStatus().forEach(e -> {
            StatisticJobDTO record = new StatisticJobDTO();
            record.setStatus(e);
            record.setNum(0);
            result.add(record);
        });
        for (Object[] job : jobs) {
            StatisticJobDTO record = new StatisticJobDTO();
            String x = job[0].toString();
            int y = ((Number) job[1]).intValue();
            record.setStatus(x);
            record.setNum(y);
            result.add(record);
        }
        return result
                .stream()
                .collect(Collectors.groupingBy(StatisticJobDTO::getStatus,
                        Collectors.summingInt(StatisticJobDTO::getNum)));
    }
    @Transactional
    public List<JobStatisticDTO> findByPlanner(long id) {
        List<JobStatisticDTO> rs = new ArrayList<>();
        List<JobAssignment> data = jobAssignmentRepository.findAllJobByPlanner(id);
        for (JobAssignment e : data) {
            rs.add(new JobStatisticDTO(e));
        }
        return rs;
    }

    public List<TaskComingDTO> findJobToExec(long id, int type) {
        List<TaskComingDTO> result = new ArrayList<>();
        List<Object[]> jobs;
        if (type == 1) {
            jobs = jobAssignmentRepository.getJobToExecToday(id);
        } else if (type == 2) {
            jobs = jobAssignmentRepository.getJobToExecOnWeek(id);
        } else {
            jobs = jobAssignmentRepository.getJobToExecOnMonth(id);
        }
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (Object[] job : jobs) {
                TaskComingDTO record = new TaskComingDTO();
                long ids = Long.parseLong(job[0].toString());
                String jobName = job[1].toString();
                String date = job[2].toString().substring(0, 11) + job[3].toString();
                record.setId(ids);
                record.setDateExec(date);
                record.setJobName(jobName);
                record.setOperation(EXECUTE_JOB);
                record.setJobId(Long.parseLong(job[4].toString()));
                record.setWorkflowId(Long.parseLong(job[5].toString()));
                result.add(record);
            }
        }
        return result;
    }

    public List<TaskComingDTO> getJobExecToApprove(String email, int type) {
        List<TaskComingDTO> result = new ArrayList<>();
        List<Object[]> resultSet;
        if (type == 1) {
            resultSet = jobApprovalRepository.getJobApprovalToday(email);
        } else if (type == 2) {
            resultSet = jobApprovalRepository.getJobApprovalOnWeek(email);
        } else {
            resultSet = jobApprovalRepository.getJobApprovalOnMonth(email);
        }
        if (CollectionUtils.isNotEmpty(resultSet)) {
            for (Object[] data : resultSet) {
                TaskComingDTO record = new TaskComingDTO();
                String[] approves = data[3].toString().split(CommonConstants.COMMA);
                for (String approve : approves) {
                    if (approve.trim().equals(email)) {
                        record.setId(Long.parseLong(data[0].toString()));
                        record.setJobName(data[1].toString());
                        record.setOperation(APPROVED_JOB_EXEC);
                        record.setDateExec(data[2].toString().substring(0, 16));
                        result.add(record);
                    }
                }
            }
        }
        return result;
    }

    public List<TaskComingDTO> findJobExec(long id, String email, int type) {
        List<TaskComingDTO> result = new ArrayList<>();
        result.addAll(getJobExecToApprove(email, type));
        result.addAll(findJobToExec(id, type));
        return result;
    }

    public Map<String, Object> findJobForEngineerDashboard(User assignee) {
        Map<String, Object> mapAssignJob = new ConcurrentHashMap<>();
        List<JobAssignment> jobAssignments = jobAssignmentRepository.findByAssigneeAndStatusEquals(assignee, JobPlanStatus.PENDING);
        List<JobAssignmentDTO> jobAssignmentDTOS = jobAssignments.stream().map(JobAssignmentDTO::new).collect(Collectors.toList());
        jobAssignmentDTOS.stream().forEach(i -> {
            if (i.getModifiedDate() != null) {
                i.setJobAssignmentDate(i.getModifiedDate());
            } else {
                i.setJobAssignmentDate(i.getCreatedDate());
            }
        });

        mapAssignJob.put("myJobs", jobAssignmentDTOS);
        List<Object[]> countStatus = jobAssignmentRepository.countByStatusByUser(assignee.getId());
        for (Object[] item : countStatus) {
            switch (String.valueOf(item[0])) {
                case JobPlanStatus.ACCEPTED:
                    mapAssignJob.put("totalAccepted", item[1]);
                    break;
                case JobPlanStatus.REJECTED:
                    mapAssignJob.put("totalRejected", item[1]);
                    break;
                case JobPlanStatus.FINISHED_APPROVED:
                    mapAssignJob.put("totalCompleted", item[1]);
                    break;
                case JobPlanStatus.FINISHED_REJECTED:
                    mapAssignJob.put("totalFailed", item[1]);
                    break;
                default:
                    break;
            }
        }
        return mapAssignJob;
    }

    public Map<String, Integer> countStatusJobPieChart(int type) {
        List<Object[]> jobs;
        if (type == 1) {
            jobs = jobAssignmentRepository.countByStatusLastWeek();
        } else if(type == 2) {
            jobs = jobAssignmentRepository.countByStatusLastMonth();
        } else {
            jobs = jobAssignmentRepository.countByStatusThisYear();
        }
        Set<StatisticJobDTO> result = new HashSet<>();
        JobPlanStatus.getJobPlanningStatus().forEach(e -> {
            StatisticJobDTO record = new StatisticJobDTO();
            record.setStatus(e);
            record.setNum(0);
            result.add(record);
        });
        for (Object[] job : jobs) {
            StatisticJobDTO record = new StatisticJobDTO();
            String x = job[0].toString();
            int y = ((Number) job[1]).intValue();
            record.setStatus(x);
            record.setNum(y);
            result.add(record);
        }
        return result
                .stream()
                .collect(Collectors.groupingBy(StatisticJobDTO::getStatus,
                        Collectors.summingInt(StatisticJobDTO::getNum)));
    }

    public List<WorkflowStatisticDTO> statisticWorkflow() {
        List<WorkflowStatisticDTO> result = new ArrayList<>();
        List<Object[]> resultSet;
        resultSet = jobAssignmentRepository.statisticWorkflowTimeExec();
        if (CollectionUtils.isNotEmpty(resultSet)) {
            for (Object[] data : resultSet) {
				int x = ((Number) data[3]).intValue();
				int y = ((Number) data[2]).intValue();
				WorkflowStatisticDTO record = new WorkflowStatisticDTO();
				record.setName(data[1].toString());
                record.setId(Long.parseLong(data[0].toString()));
				record.setUsage(x);
				record.setSecondEve(y);
				record.setTimeView(BEDateUtils.formatSeconds(y));
				result.add(record);
            }
        }
        return result;
    }


}
