package com.nera.nms.rest.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.dto.JobAssignmentDTO;
import com.nera.nms.dto.JobAssignmentInsertDTO;
import com.nera.nms.dto.JobCalendarViewDTO;
import com.nera.nms.dto.JobPayloadDTO;
import com.nera.nms.dto.JobViewDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserAssignDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.JobManagement;
import com.nera.nms.models.JobPayload;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.JobService;
import com.nera.nms.services.UserService;
import com.nera.nms.utils.PageableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("nera/job-planning/api")
public class JobPlanRestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobAssignmentService jobAssignmentService;

    private UserService userService;

    private JobService jobService;

    private ISystemAuditLogRepository iSystemAuditLogRepository;

    public JobPlanRestController(JobAssignmentService jobAssignmentService, UserService userService, JobService jobService,
                                 ISystemAuditLogRepository iSystemAuditLogRepository) {
        this.jobAssignmentService = jobAssignmentService;
        this.userService = userService;
        this.jobService = jobService;
        this.iSystemAuditLogRepository = iSystemAuditLogRepository;
    }

    @GetMapping("/listing-and-filter")
    public ResponseEntity<PageableDTO> getList(
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "1") int page,
            @RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
            @RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate) {
        Page<JobAssignmentDTO> jobAssignDTO;
        Page<JobAssignment> jobAssign; // result search
        PageableDTO result = null;
        // create pageable with client parameter
        Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);
        try {
            jobAssign = jobAssignmentService.findAllWithSearchString(pageable, query, status, startDate, endDate);
            // map entity to dto
            jobAssignDTO = jobAssign.map(JobAssignmentDTO::new);

            /* identified number row and map to dto result */
            AtomicLong numIncrement = new AtomicLong(1);
            List<JobAssignmentDTO> data = jobAssignDTO.getContent();
            for (JobAssignmentDTO object : data) {
                object.setNo(numIncrement.getAndIncrement() + (page - 1) * 10);
            }
            result = PageableUtil.pageableMapper(jobAssignDTO, pageable, data);
        } catch (Exception e) {
            logger.error("JobPlanRestController.getList : ", e);
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-planner")
    public ResponseEntity<List<UserAssignDTO>> getListPlanner() {
        List<UserAssignDTO> data = userService.getListPlanner();
        data = data.stream().sorted(Comparator.comparing(UserAssignDTO::getFullName)).collect(Collectors.toList());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get-assignee")
    public ResponseEntity<List<UserAssignDTO>> getListAssignee() {
        List<UserAssignDTO> data = userService.getListAssignee();
        data = data.stream().sorted(Comparator.comparing(UserAssignDTO::getFullName)).collect(Collectors.toList());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get-job-management")
    public ResponseEntity<List<JobViewDTO>> getListJob() {
        List<JobViewDTO> data = jobService.getListJobForPlanning();
        data = data.stream().sorted(Comparator.comparing(JobViewDTO::getName)).collect(Collectors.toList());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get-job-view-by-id")
    public ResponseEntity<JobViewDTO> getJobDetailById(@RequestParam Long id) {
        JobViewDTO data = null;
        try {
            data = new JobViewDTO();
            data = jobService.findViewJobById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return new ResponseEntity<>(data, HttpStatus.OK);
        }

    }

    @PostMapping("/create-job-planning")
    public ResponseEntity<ResultDTO> createJobPlan(@RequestBody JobAssignmentInsertDTO body, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        result.setMess("failed");
        UserProfileDto data;
        try {
            data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            body.setCreatedBy(data.getFullName());
            JobAssignment rs = jobAssignmentService.saveJobPlanning(body, userService.findById(body.getAssigneeId()), userService.findById(body.getPlannerId()));
            result.setId(rs.getId());
            result.setFieldName(rs.getJobManagement().getName());
            result.setCode(200);
            result.setMess(CommonConstants.SUCCESS);
            String detailsCreate = "create new job planning by " + data.getEmail();
            String detailsAssign = rs.getPlannerName() + " assign new job to " + rs.getAssigneeName();
            saveAuditLog(detailsCreate, "Create Job Planning", data);
            saveAuditLog(detailsAssign, "Assign New Job", data);
        } catch (Exception e) {
            logger.error("JobPlanRestController.createJobPlan : ", e);
            result.setCode(500);
            result.setDetail("Internal Server Exception!");
            result.setDetail(e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/update-job-planning")
    public ResponseEntity<ResultDTO> updateJobPlan(@RequestBody JobAssignmentInsertDTO body, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        result.setMess("failed");
        UserProfileDto data;
        try {
            data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            body.setModifiedBy(data.getFullName());
            User assignee = userService.findById(body.getAssigneeId());
            User planner = userService.findById(body.getPlannerId());
            boolean check = jobAssignmentService.updateJobPlanning(body, assignee, planner);
            result.setCode(200);
            result.setMess(CommonConstants.SUCCESS);
            String detailsUpdate = "Update job planning by " + data.getEmail();
            saveAuditLog(detailsUpdate, "Update Job Planning", data);
            if(check) {
                String detailsAssign = planner.getFullName() + " assign new job to " + assignee.getFullName();
                saveAuditLog(detailsAssign, "Assign New Job", data);
            }
        } catch (Exception e) {
            logger.error("JobPlanRestController.updateJobPlan : ", e);
            result.setCode(500);
            result.setDetail("Internal Server Exception!");
            result.setDetail(e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void saveAuditLog(String details, String desc, UserProfileDto data) {
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
    }

    @GetMapping("/get-job-status")
    public ResponseEntity<Set<String>> getListStatus() {
        return new ResponseEntity<>(JobPlanStatus.getJobPlanningStatus(), HttpStatus.OK);
    }
    @GetMapping("/get-job-pay-load")
    public ResponseEntity<List<JobPayloadDTO>>getJobPayLoad (@RequestParam long id){
        List<JobPayloadDTO> data = new ArrayList<>();
        JobManagement jobManagement = jobService.findJobById(id);
        if(jobManagement != null) {
            List<JobPayload> payloadList = jobManagement.getJobPayload();
            for (JobPayload i : payloadList) {
                data.add(new JobPayloadDTO(i.getId(), i.getJobInputId(), i.getValue()));
            }
        }
        return new ResponseEntity<>(data,HttpStatus.OK);
    }
    @GetMapping("/get-source-calendar-view")
    public ResponseEntity<List<JobCalendarViewDTO>> getSourceCalendarView() {
        List<JobCalendarViewDTO> data = jobAssignmentService.getSourceForCalendar();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get-job-by-id")
    public ResponseEntity<JobAssignmentDTO> getJobById(@RequestParam long id) {
        JobAssignmentDTO data;
        data = jobAssignmentService.getById(id);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/delete-job-by-id")
    public void  deleteJobById(@RequestParam long id, HttpServletRequest request) {
        jobAssignmentService.deleteById(id);
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        String details = "Job planning has been deleted by " + data.getEmail();
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog,
                data.getFullName(),
                data.getGroups(),
                details,
                "Delete Job Planning Successful",
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
    }
}