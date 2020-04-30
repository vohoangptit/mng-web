package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.dto.JobApprovalRequestDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.services.AuditLogService;
import com.nera.nms.services.EmailService;
import com.nera.nms.services.JobApprovalService;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.utils.AuditLogUtil;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.PageableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("nera/my-job-approval-request/api")
public class MyJobApprovalRequestRestController {

   private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobApprovalService jobApprovalService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JobAssignmentService jobAssignmentService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<PageableDTO> getListJobApprovalRequest (
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "1") int page,
            @RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
            @RequestParam(value = "sort[field]", defaultValue = "requestAt") String sortBy,
            @RequestParam(value = "query[generalSearch]", defaultValue = "") String searchString,
            HttpServletRequest request) {

        logger.info("Calling MyJobApprovalRequestRestController::getListJobApprovalRequest");
        PageableDTO response = new PageableDTO();
        try {
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            String sortByWithPrefix = getSortByWithPrefix(sortBy);
            Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortByWithPrefix, orderBy);
            Page<JobApprovalRequestDTO> pageDto = jobApprovalService.getMyJobApprovalList(searchString, pageable, profile.getEmail());

            if (pageDto == null) {
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            List<JobApprovalRequestDTO> jobApprovalList = pageDto.getContent();
            AtomicLong numIncrement = new AtomicLong();
            if (CollectionUtils.isNotEmpty(jobApprovalList)) {
                jobApprovalList.forEach(dto -> {
                    dto.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage));
                    dto.setAssigneeName(dto.getAssignee().getFullName());
                    dto.setPlannerName(dto.getPlanner().getFullName());
                });
            }
            response = PageableUtil.pageableMapper(pageDto, pageable, jobApprovalList);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reject-approve-job")
    public ResponseEntity<ResultDTO> rejectApproveJob(@RequestParam("id") long id,
                                                @RequestParam("actionType") String actionType,
                                                HttpServletRequest request) {

        logger.info("Calling MyJobApprovalRequestRestController::rejectApproveJob");
        ResultDTO result = new ResultDTO();
        try {
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            JobApprovalRequestDTO pageDto = jobApprovalService.getByJobApprovalId(id);
            if (pageDto == null || profile == null) {
                result.setCode(HttpStatus.NO_CONTENT.value());
                return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
            }

            jobApprovalService.updateAfterRejectedOrApproved(id, actionType, profile.getFullName());
            SystemAuditLog systemAuditLog;
            if (StringUtils.equals(actionType, "Approve")) {
                if (jobAssignmentService.updateAfterRejectedOrApproved(pageDto.getJobAssignmentId(), JobPlanStatus.FINISHED_APPROVED, profile.getFullName())) {
                    emailService.sendApproveOrRejectJobEmail(pageDto.getJobName(),
                            pageDto.getAssignee().getFullName(),
                            profile.getFullName(),
                            StringUtils.EMPTY,
                            BEDateUtils.convertDateToString(BEDateUtils.getCurrentDate(),
                                    BEDateUtils.MM_DD_YYYY_HH_MM_SS_WITH_HYPHEN),
                            pageDto.getAssignee().getEmail());
                    String details = AuditLogUtil.getDetailsAudit(pageDto.getJobName(), profile.getEmail(), "approved");
                    systemAuditLog = AuditLogUtil.getSystemAuditLog(profile.getFullName(), profile.getGroups(), details, "Approve the job execution");
                    auditLogService.createAuditLog(systemAuditLog);
                }
            } else {
                if (jobAssignmentService.updateAfterRejectedOrApproved(pageDto.getJobAssignmentId(), JobPlanStatus.FINISHED_REJECTED, profile.getFullName())) {
                    emailService.sendApproveOrRejectJobEmail(pageDto.getJobName(),
                            pageDto.getAssignee().getFullName(),
                            profile.getFullName(),
                            BEDateUtils.convertDateToString(BEDateUtils.getCurrentDate(),
                                    BEDateUtils.MM_DD_YYYY_HH_MM_SS_WITH_HYPHEN),
                            StringUtils.EMPTY,
                            pageDto.getAssignee().getEmail());
                    String details = AuditLogUtil.getDetailsAudit(pageDto.getJobName(), profile.getEmail(), "rejected");
                    systemAuditLog = AuditLogUtil.getSystemAuditLog(profile.getFullName(), profile.getGroups(), details, "Reject the job execution");
                    auditLogService.createAuditLog(systemAuditLog);
                }
            }
            result.setCode(HttpStatus.OK.value());
            systemAuditLog = AuditLogUtil.getSystemAuditLog(profile.getFullName(), profile.getGroups(), "The job execution is finish", "The job execution is finish");
            auditLogService.createAuditLog(systemAuditLog);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private String getSortByWithPrefix(String sortBy) {
        switch (sortBy) {
            case "workflowName":
                return "job.workflowName";
            case "jobName":
                return "job.name";
            case "jobDescription":
                return "job.description";
            case "plannerName":
                return "ja.planner";
            case "assigneeName":
                return "ja.assignee";
            default:
                 return "requestAt";
        }
    }
}