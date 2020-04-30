package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.JobStatisticDTO;
import com.nera.nms.dto.TaskComingDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.dto.WorkflowStatisticDTO;
import com.nera.nms.models.User;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.PlaybookService;
import com.nera.nms.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("nera/dashboard/api")
public class DashboardController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PlaybookService playbookService;

    private JobAssignmentService jobAssignmentService;

    private UserService userService;

    public DashboardController(PlaybookService playbookService, JobAssignmentService jobAssignmentService, UserService userService) {
        this.playbookService = playbookService;
        this.jobAssignmentService = jobAssignmentService;
        this.userService = userService;
    }

    @GetMapping("/statistic-playbook-by-status")
    public ResponseEntity<Map<Integer, Integer>> getStatisticPlaybookByStatus() {
        Map<Integer, Integer> data = playbookService.countStatusPlaybook();
        logger.info("statistic playbook by status");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/statistic-job-by-status")
    public ResponseEntity<Map<String, Integer>> getStatisticJobByStatus() {
        Map<String, Integer> data = jobAssignmentService.countStatusJob();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @GetMapping("/get-job-exec-to-do")
    public ResponseEntity<List<TaskComingDTO>> getUpComingTask(HttpServletRequest request, @RequestParam int type) {
        List<TaskComingDTO> data = null;
        try {
            data = new ArrayList<>();
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            User user = userService.findUserByEmail(profile.getEmail());
            data = jobAssignmentService.findJobExec(user.getId(), profile.getEmail(), type);
        } catch (Exception e) {
            logger.error("Exception : DashboardController.getUpComingTask ", e);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    // dashboard engineer
    @GetMapping("/get-job-accepted")
    public ResponseEntity<List<TaskComingDTO>> getUpComingTaskEngineer(HttpServletRequest request, @RequestParam int type) {
        try {
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            User user = userService.findUserByEmail(profile.getEmail());
            return new ResponseEntity<>(jobAssignmentService.findJobToExec(user.getId(), type), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception : DashboardController.getUpComingTaskEngineer ", e);
        }
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/statistic-playbook-by-status-pie-chart")
    public ResponseEntity<Map<Integer, Integer>> getStatisticPlaybookByStatusPieChart(@RequestParam int type) {
        Map<Integer, Integer> data = playbookService.countStatusPlaybookPie(type);
        logger.info("statistic playbook by status");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/statistic-job-by-status-pie-chart")
    public ResponseEntity<Map<String, Integer>> getStatisticJobByStatusPieChart(@RequestParam int type) {
        Map<String, Integer> data = jobAssignmentService.countStatusJobPieChart(type);
        logger.info("statistic job by status");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/statistic-workflow")
    public ResponseEntity<List<WorkflowStatisticDTO>> getStatisticJobByStatusPieChart() {
        List<WorkflowStatisticDTO> data = jobAssignmentService.statisticWorkflow();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/list-job-planner")
    public ResponseEntity<List<JobStatisticDTO>> getStatisticJobByStatusPieChart(HttpServletRequest request) {
        try {
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            User user = userService.findUserByEmail(profile.getEmail());
            logger.info("statistic job by status");
            List<JobStatisticDTO> data = jobAssignmentService.findByPlanner(user.getId());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception : DashboardController.getStatisticJobByStatusPieChart ", e);
        }
		return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}