package com.nera.nms.rest.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.InventoryHostDTO;
import com.nera.nms.dto.JobDTO;
import com.nera.nms.dto.JobInputDTO;
import com.nera.nms.dto.JobPayloadDTO;
import com.nera.nms.dto.JobWorkflowDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.InventoryGroupHost;
import com.nera.nms.models.InventoryHost;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.JobInput;
import com.nera.nms.models.JobManagement;
import com.nera.nms.models.JobPayload;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.InventoryService;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.JobInputService;
import com.nera.nms.services.JobService;
import com.nera.nms.services.UserService;
import com.nera.nms.utils.PageableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("nera/job/api")
public class JobManagementRestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobService jobService;

    @Autowired
    private JobAssignmentService jobAssignmentService;

    @Autowired
    private JobInputService jobInputService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ISystemAuditLogRepository iSystemAuditLogRepository;

    @Autowired
    private UserService userService;

    private static final String ADD_JOB_FAIL_MESS = "Add Job Fail";

    private static final String UPDATE_JOB_FAIL_MESS = "Update Job Fail";

    private static final String UPDATE_JOB_SUCCESS_MESS = "Update Job Successful";

    private static final String DELETE_JOB_SUCCESS_MESS = "Delete Job Successful";

    private static final String UPDATE_ACTION = "updated";

    private static final String CREATE_ACTION = "created";

    private static final String DELETE_ACTION = "deleted";

    private static final String LIST_HOST_ID = "listHostId";

    @GetMapping("/listing-and-filter")
    @PreAuthorize("hasAuthority('VIEW_JOB_LISTING')")
    public ResponseEntity<PageableDTO> getListGroup(
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "1") int page,
            @RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
            @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
            @RequestParam(value = "createdBy", required = false, defaultValue = "") String createdBy,
            @RequestParam(value = "dependency", required = false, defaultValue = "") String dependency,
            @RequestParam(value = "query[generalSearch]", defaultValue = "") String searchString) {
        PageableDTO result = new PageableDTO();
        try {
            Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);
            Page<JobManagement> jobManagementPage = getPageJobByConditions(searchString, createdBy, startDate, endDate, status, dependency, pageable);
            // map entity to dto
            Page<JobDTO> jobDTOPage = jobManagementPage.map(JobDTO::new);
            List<JobDTO> jobDTOs = jobDTOPage.getContent();
            // identified number row and map to dto result
            AtomicLong numIncrement = new AtomicLong();
            jobDTOs.forEach(object -> object.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage)));
            result = PageableUtil.pageableMapper(jobDTOPage, pageable, jobDTOs);
        } catch (Exception e) {
             return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Page<JobManagement> getPageJobByConditions(String searchString, String createdBy, String startDate, String endDate, String jobActive, String dependency, Pageable pageable) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date start = null;
        Date end = null;
        if (StringUtils.isNotBlank(startDate)) {
            start = formatter.parse(startDate);
        }
        if(StringUtils.isNotBlank(endDate)) {
            end = DateUtils.addDays(formatter.parse(endDate),1);
        }

        if(StringUtils.isNotBlank(dependency)) {
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(jobActive)) {
                return jobService.findByAllConditionDependency(pageable, searchString, start, end, createdBy, Boolean.parseBoolean(jobActive), Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(jobActive)) {
                return jobService.findByStartDateAndStatusDependency(pageable, searchString, start, createdBy, Boolean.parseBoolean(jobActive), Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(jobActive)) {
                return jobService.findByEndDateAndStatusDependency(pageable, searchString, end, createdBy, Boolean.parseBoolean(jobActive), Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                return jobService.findByStartEndDateDependency(pageable, searchString, start, end, createdBy, Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(startDate)) {
                return jobService.findByStartDateDependency(pageable, searchString, end, createdBy, Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(endDate)) {
                return jobService.findByEndDateDependency(pageable, searchString, end, createdBy, Boolean.parseBoolean(dependency));
            } else if (StringUtils.isNotBlank(jobActive)) {
                return jobService.findByStatusDependency(pageable, searchString, Boolean.parseBoolean(jobActive), createdBy, Boolean.parseBoolean(dependency));
            } else {
                return jobService.findJobByHostActive(pageable,Boolean.parseBoolean(dependency));
            }
        }


        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(jobActive)) {
            return jobService.findByAllCondition(pageable, searchString, start, end, createdBy, Boolean.parseBoolean(jobActive));
        } else if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(jobActive)) {
            return jobService.findByStartDateAndStatus(pageable, searchString, start, createdBy, Boolean.parseBoolean(jobActive));
        } else if (StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(jobActive)) {
            return jobService.findByEndDateAndStatus(pageable, searchString, end, createdBy, Boolean.parseBoolean(jobActive));
        } else if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            return jobService.findByStartEndDate(pageable, searchString, start, end, createdBy);
        } else if (StringUtils.isNotBlank(startDate)) {
            return jobService.findByStartDate(pageable, searchString, start, createdBy);
        } else if (StringUtils.isNotBlank(endDate)) {
            return jobService.findByEndDate(pageable, searchString, end, createdBy);
        } else if (StringUtils.isNotBlank(jobActive)) {
            return jobService.findByStatus(pageable, searchString, Boolean.parseBoolean(jobActive), createdBy);
        } else {
            return jobService.findAllRecord(pageable, searchString, createdBy);
        }
    }

    @GetMapping("/created-person")
    public Set<String> getListCreatedPerson() {
        Set<String> setCreatedPerson = new HashSet<>();
        try {
            setCreatedPerson = userService.getCreatedByJobManagement();
        } catch(Exception e) {
            logger.error("JobManagementRestController.getListCreatedPerson ", e);
        }
        return setCreatedPerson;
    }

    @GetMapping("/get-by-id")
    public Map<String, Object> getJobDetailById(@RequestParam Long id, @RequestParam String searchString) {
        List<InventoryHostDTO> inventoryHostDTOList = new ArrayList<>();
        Map<String, Object> hostMap = new HashMap<>();
        try {
            JobManagement jobManagement = jobService.findJobById(id);
            if(jobManagement != null) {
                hostMap.put("name", jobManagement.getName());
                hostMap.put("description", jobManagement.getDescription());
                hostMap.put("workflowId", jobManagement.getWorkflowID());
                hostMap.put("active", jobManagement.isActive());
                Iterator<InventoryHost> hosts =  jobManagement.getHosts().iterator();
                int no = 1;
                searchHostByNameOrGroupName(searchString, inventoryHostDTOList, hosts, no);
                hostMap.put("total", inventoryHostDTOList.size());
                List<JobPayloadDTO> jobPayloads = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(jobManagement.getJobPayload())) {
                    for(JobPayload item : jobManagement.getJobPayload()) {
                        JobPayloadDTO dto = new JobPayloadDTO();
                        dto.setJobInputId(item.getJobInputId());
                        dto.setValue(item.getValue());
                        jobPayloads.add(dto);
                    }
                }
                hostMap.put("listPayload", jobPayloads);
            }
            hostMap.put("listHost", inventoryHostDTOList);
        } catch(Exception e) {
            logger.error("JobManagementRestController.getJobDetailById ", e);
        }
        return hostMap;
    }


    private void searchHostByNameOrGroupName(String searchString, List<InventoryHostDTO> inventoryHostDTOList, Iterator<InventoryHost> hosts, int no) {
        while(hosts.hasNext()) {
            InventoryHost inventoryHost = hosts.next();
            InventoryHostDTO inventoryHostDTO = new InventoryHostDTO(inventoryHost);
            StringBuilder sb = new StringBuilder();
            Iterator<InventoryGroupHost> inventoryGroupHosts = inventoryHost.getGroups().iterator();
            while (inventoryGroupHosts.hasNext()) {
                sb.append(inventoryGroupHosts.next().getName());
                if(inventoryGroupHosts.hasNext()) {
                    sb.append(" - ");
                }
            }
            inventoryHostDTO.setNo(no);
            inventoryHostDTO.setGroupName(sb.toString());
            if(StringUtils.isNotBlank(searchString) && (!StringUtils.containsIgnoreCase(inventoryHostDTO.getName(), searchString) && !StringUtils.containsIgnoreCase(inventoryHostDTO.getGroupName(), searchString))) {
                continue;
            }
            inventoryHostDTOList.add(inventoryHostDTO);
            no++;
        }
    }

    @GetMapping("/get-workflow-by-id")
    public Map<String, Object> getWorkflowById(@RequestParam Long jobId) {
        Map<String, Object> workflowMap = new HashMap<>();
        try {
            JobManagement jobManagement = jobService.findJobById(jobId);
            workflowMap.put("workflowId", jobManagement.getWorkflowID());
        } catch(Exception e) {
            logger.error("JobManagementRestController.getWorkflowById ", e);
        }
        return workflowMap;
    }

    @GetMapping("/get-job-input-by-id")
    public List<JobInputDTO> getJobInputDetailById(@RequestParam Long id, @RequestParam Long idJob) {
        List<JobInputDTO> jobInputDTOS = new ArrayList<>();
        try {
            List<JobInput> jobInputList  = jobInputService.findJobByWorkflowId(id);
            JobManagement jobManagement = jobService.findJobById(idJob);
            if(CollectionUtils.isNotEmpty(jobInputList)) {
                int i = 1;
                for(JobInput item : jobInputList) {
                    JobInputDTO dto = new JobInputDTO();
                    dto.setId(item.getId());
                    jobManagement.getJobPayload().forEach(payLoad -> {
                        if(payLoad.getJobInputId() == item.getId()) {
                            dto.setValue(payLoad.getValue());
                        }
                    });
                    dto.setType(item.getType());
                    dto.setVariable(item.getVariable());
                    dto.setNo(i++);
                    jobInputDTOS.add(dto);
                }
            }
        } catch(Exception e) {
            logger.error("JobManagementRestController.getJobInputDetailById ", e);
        }
        return jobInputDTOS;
    }

    @GetMapping("/get-host-by-job")
    public ResponseEntity<Map<String, Object>> getHostByJob(@RequestParam long id,
                                                    @RequestParam(value = "hostSelected", required = false, defaultValue = "0") long hostSelected,
                                                    @RequestParam(value = "groupSelected", required = false, defaultValue = "0") long groupSelected) {

        Map<String, Object> mapInventoryDTO = new HashMap<>();
        try {
            List<InventoryHost> result = new ArrayList<>();
            boolean check = true;
            if(groupSelected > 0 && hostSelected > 0) {
                result = inventoryService.findByGroupHostId(groupSelected, hostSelected);
            } else if(groupSelected > 0) {
                result = inventoryService.findByGroupId(groupSelected);
            } else if(hostSelected > 0) {
                result.add(inventoryService.findByHostId(hostSelected));
            } else {
                result = inventoryService.listingHostActive(StringUtils.EMPTY);
                check = false;
            }
            List<InventoryHostDTO> listDTO = new ArrayList<>();
            List<Number> listHostId = jobService.getListJobHost(id);
            AtomicLong numIncrement = new AtomicLong(1);
            addHostList(result, listDTO, listHostId, numIncrement);
            mapInventoryDTO.put("listInventory", listDTO);
            if(check && !listHostId.isEmpty()) {
                compareListHost(listHostId, listDTO);
                mapInventoryDTO.put(LIST_HOST_ID, listHostId);
            } else {
                mapInventoryDTO.put(LIST_HOST_ID, Collections.emptyList());
            }
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.getHostByJob ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(mapInventoryDTO, HttpStatus.OK);
    }


    @GetMapping("/get-host-by-name")
    public ResponseEntity<Map<String, Object>> getHostJobByName(@RequestParam(value = "query[generalSearch]", defaultValue = "") String name) {
        Map<String, Object> mapInventoryDTO = new HashMap<>();
        try {
            List<InventoryHost> result = inventoryService.findHostByName(name);
            List<InventoryHostDTO> listDTO = new ArrayList<>();
            AtomicLong numIncrement = new AtomicLong(1);
            addHostList(result, listDTO, Collections.emptyList(), numIncrement);
            mapInventoryDTO.put("listInventory", listDTO);
            mapInventoryDTO.put(LIST_HOST_ID, Collections.emptyList());
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.getHostJobByName ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(mapInventoryDTO, HttpStatus.OK);
    }



    private void addHostList(List<InventoryHost> result, List<InventoryHostDTO> listDTO, List<Number> listHostId, AtomicLong numIncrement) {
        for(InventoryHost item : result) {
            InventoryHostDTO hostDTO = new InventoryHostDTO(item);
            StringBuilder sb = new StringBuilder();
            Iterator<InventoryGroupHost> inventoryGroupHosts = item.getGroups().iterator();
            while (inventoryGroupHosts.hasNext()) {
                sb.append(inventoryGroupHosts.next().getName());
                if(inventoryGroupHosts.hasNext()) {
                    sb.append(" - ");
                }
            }
            hostDTO.setActive(false);
            if(!listHostId.isEmpty()) {
                listHostId.forEach(i -> {
                    if(i.longValue() == item.getId()) {
                        hostDTO.setActive(true);
                    }
                });
            }
            hostDTO.setGroupName(sb.toString());
            hostDTO.setNo(numIncrement.getAndIncrement());
            listDTO.add(hostDTO);
        }
    }

    private void compareListHost(List<Number> allHost, List<InventoryHostDTO> listHostSearch) {
        for(int a = allHost.size() ; a > 0 ; a--) {
            for(InventoryHostDTO host : listHostSearch) {
                if(host.getId() == allHost.get(a-1).longValue()) {
                    allHost.remove(a-1);
                    break;
                }
            }
        }
    }

    @PostMapping("/add-job-input")
    public ResponseEntity<ResultDTO> addJobInput(@RequestBody JobWorkflowDTO jobWorkflowDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            String nameJob = jobService.updateWorkflowJob(jobWorkflowDTO, data.getFullName());
            if (StringUtils.isNotBlank(nameJob)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        getDetailsAudit(nameJob, jobWorkflowDTO.getJobId(), data.getEmail(), UPDATE_ACTION),
                        UPDATE_JOB_SUCCESS_MESS,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                List<JobAssignment> jobAssignments = jobAssignmentService.findByJobId(jobWorkflowDTO.getJobId());
                if(jobAssignments != null){
                    jobAssignments.forEach(e->{
                        e.setJobDescription(jobWorkflowDTO.getWorkflowName());
                        jobAssignmentService.autoUpdateJobInfo(e);
                    });
                }
                result.setMess(UPDATE_JOB_SUCCESS_MESS);
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.addJobInput ", e);
            result.setMess(ADD_JOB_FAIL_MESS);
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/check-duplicate")
    public boolean checkDuplicateJob(@RequestParam String jobName, @RequestParam Long jobId) {
        try {
            JobManagement jobManagement = new JobManagement();
            if(jobId > 0) {
                jobService.findOneByNameAndId(jobId, jobName);
            } else {
                jobManagement = jobService.findJobByName(jobName);
            }
            if(jobManagement != null) {
                return true;
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.checkDuplicateJob ", e);
        }
        return false;
    }

    @PostMapping("/add-job")
    public ResponseEntity<ResultDTO> addNewJob(@RequestBody JobDTO jobDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (jobService.findJobByName(jobDTO.getName()) != null) {
                result.setMess(ADD_JOB_FAIL_MESS);
                result.setCode(400);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            jobDTO.setCreatedBy(data.getFullName());
            jobDTO.setCreatedDate(new Date());
            JobManagement jobManagement = jobDTO.dtoToEntity(new JobManagement());

            if (jobService.addNewJob(jobManagement)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        getDetailsAudit(jobDTO.getName(), jobDTO.getId(), data.getEmail(), CREATE_ACTION),
                        "Create Job",
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess("Add Job Successful");
                result.setCode(200);
                result.setDetail(String.valueOf(jobManagement.getId()));
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.addNewJob ", e);
            result.setMess(ADD_JOB_FAIL_MESS);
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/update-job")
    public ResponseEntity<ResultDTO> updateJob(@RequestBody JobDTO jobDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (jobService.findOneByNameAndId(jobDTO.getId(), jobDTO.getName()) != null) {
                result.setMess(UPDATE_JOB_FAIL_MESS);
                result.setCode(400);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            jobDTO.setModifiedBy(data.getFullName());
            jobDTO.setModifiedDate(new Date());
            if (jobService.updateJob(jobDTO)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        getDetailsAudit(jobDTO.getName(), jobDTO.getId(), data.getEmail(), UPDATE_ACTION),
                        UPDATE_JOB_SUCCESS_MESS,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                List<JobAssignment> jobAssignments = jobAssignmentService.findByJobId(jobDTO.getId());
                if(jobAssignments != null){
                    jobAssignments.forEach(e->{
                        e.setJobDescription(jobDTO.getDescription());
                        e.setJobName(jobDTO.getName());
                        jobAssignmentService.autoUpdateJobInfo(e);
                    });
                }
                result.setMess(UPDATE_JOB_SUCCESS_MESS);
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.updateJob ", e);
            result.setMess(UPDATE_JOB_FAIL_MESS);
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add-host-job")
    public ResponseEntity<ResultDTO> addHostJob(@RequestBody JobDTO jobDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            String jobName = jobService.addHostJobDetail(jobDTO, data.getFullName());
            if (StringUtils.isNotBlank(jobName)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        getDetailsAudit(jobName, jobDTO.getId(), data.getEmail(), UPDATE_ACTION),
                        UPDATE_JOB_SUCCESS_MESS,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess(UPDATE_JOB_SUCCESS_MESS);
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.addHostJob ", e);
            result.setMess(UPDATE_JOB_FAIL_MESS);
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/remove-host-job")
    public ResponseEntity<ResultDTO> removeHostJob(@RequestBody JobDTO jobDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            String jobName = jobService.removeHostJobDetail(jobDTO, data.getFullName());
            if (StringUtils.isNotBlank(jobName)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        getDetailsAudit(jobName, jobDTO.getId(), data.getEmail(), UPDATE_ACTION),
                        UPDATE_JOB_SUCCESS_MESS,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess(UPDATE_JOB_SUCCESS_MESS);
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("JobManagementRestController.removeHostJob ", e);
            result.setMess(UPDATE_JOB_FAIL_MESS);
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/delete-job")
    public boolean deleteHostById(@RequestParam long jobId, HttpServletRequest request) {
        List<JobAssignment> jobAssignments = jobAssignmentService.findJobAssigmentByJobIdAndStatus(jobId);
        if(CollectionUtils.isEmpty(jobAssignments)) {
            jobService.deleteJob(jobId);
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            SystemAuditLog systemAuditLog = new SystemAuditLog();
            CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
            commonMethodComponent.addAuditLogData(systemAuditLog,
                    data.getFullName(),
                    data.getGroups(),
                    getDetailsAudit(StringUtils.EMPTY, jobId, data.getEmail(),DELETE_ACTION),
                    DELETE_JOB_SUCCESS_MESS,
                    true);
            iSystemAuditLogRepository.save(systemAuditLog);
            return true;
        }
        return false;
    }

    private String getDetailsAudit(String nameJob, Long idJob, String emailUser, String action) {
        if(StringUtils.isNotBlank(nameJob)) {
            return "Job: " + nameJob + "(Job Id: " + idJob + ") has been "+ action + " by " + emailUser;
        }
        return "Job Id: " + idJob + " has been " + action + " by " + emailUser;
    }

    @PostMapping("/check-used-by-workflow")
    public boolean isUsedByWorkflow(@RequestParam("workflowId") long workflowId) {
        return jobService.isUsedByWorkflow(workflowId);
    }
}
