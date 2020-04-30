package com.nera.nms.rest.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.JobExeConstants;
import com.nera.nms.constants.JobPlanStatus;
import com.nera.nms.dto.AnsibleFileDTO;
import com.nera.nms.dto.JobAssignmentDTO;
import com.nera.nms.dto.JobAssignmentInsertDTO;
import com.nera.nms.dto.JobCalendarViewDTO;
import com.nera.nms.dto.JobExeDTO;
import com.nera.nms.dto.JobPayloadDTO;
import com.nera.nms.dto.JobViewDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserAssignDTO;
import com.nera.nms.dto.UserDefaultDto;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.JobExecution;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.EmailService;
import com.nera.nms.services.FileManagementService;
import com.nera.nms.services.FileStorageService;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.JobInputService;
import com.nera.nms.services.JobOpsExeApprovalService;
import com.nera.nms.services.JobService;
import com.nera.nms.services.UserService;
import com.nera.nms.utils.AuditLogUtil;
import com.nera.nms.utils.FactorialRunPLaybook;
import com.nera.nms.utils.OperatorUtil;
import com.nera.nms.utils.PageableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("nera/my-job/api")
public class MyJobRestController {
    private static final String CONST_JOBEXE = "(JobExecution: ";

    private static final String CONST_VALUE = "value";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileManagementService fileManagementService;

    @Autowired
    private JobAssignmentService jobAssignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobInputService jobInputService;

    @Autowired
    private  EmailService emailService;

    @Value("${file.ansible-dir}")
    private String ansibleDir;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${ansible.ansiblePlaybook}")
    private String ansiblePlaybook;

    @Value("${ansible.ip}")
    private String ansibleIP;

    @Value("${ansible.user}")
    private String ansibleUser;

    @Value("${rabbit.host}")
    private String rabbitHost;

    @Value("${rabbit.username}")
    private String rabbitUsername;

    @Value("${rabbit.password}")
    private String rabbitPassword;

    @Value("${rabbit.port}")
    private int rabbitPort;

    @Value("${rabbit.queueName}")
    private String queueName;

    @Value("${cmd.scp}")
    private String cmdScp;

    @Value("${file.scp-dir}")
    private String scpDir;

    @Value("${cmd.scp-ppkey}")
    private String ppkey;

    @Autowired
    private JobOpsExeApprovalService jobOpsExeApprovalService;

    @Autowired
    private ISystemAuditLogRepository iSystemAuditLogRepository;

    public MyJobRestController(JobAssignmentService jobAssignmentService, UserService userService, JobService jobService,
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
            @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate, HttpServletRequest request) {
        Page<JobAssignmentDTO> jobAssignDTO;
        Page<JobAssignment> jobAssign; // result search
        PageableDTO result = null;
        // create pageable with client parameter
        Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);
        try {
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            User user = userService.findUserByEmail(profile.getEmail());
            jobAssign = jobAssignmentService.findMyJobWithSearchString(pageable, query, status, startDate, endDate, user);
            // map entity to dto
            jobAssignDTO = jobAssign.map(JobAssignmentDTO::new);

            /* identified number row and map to dto result */
            AtomicLong numIncrement = new AtomicLong(1);
            List<JobAssignmentDTO> data = jobAssignDTO.getContent();
            for (JobAssignmentDTO object : data) {
                object.setNo(numIncrement.getAndIncrement() + (page - 1) * 10);
            }
            result = PageableUtil.pageableMapper(jobAssignDTO, pageable, data);
        } catch (ParseException e) {
            logger.error("MyJobRestController.getList : ", e);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/runplaybook")
    public ResponseEntity<String> runasibleonRabbit( @RequestParam String foldername)  {
        String result = "";
        try{
            ExecutorService executor = Executors.newFixedThreadPool(2);
            String cmd = ansiblePlaybook + " -i " + scpDir +foldername+"/inventories/ " +scpDir+foldername+"/juniper.yml";
            String scp = cmdScp + " -i " + ppkey  + " -r "+ ansibleDir+CommonConstants.PATH_DELIMITER+foldername+" "+ ansibleUser +"@" + ansibleIP +":" + scpDir;
            logger.info("SCP : {}", scp);
            Process p = Runtime.getRuntime().exec(scp);

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = "";
            StringBuilder message = new StringBuilder();
            while ((line = bri.readLine()) != null) {
                message.append(line);
                message.append("\n");
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                message.append(line);
                message.append("\n");
            }
            bre.close();

            FactorialRunPLaybook task2 = new FactorialRunPLaybook(cmd,rabbitHost,rabbitUsername,rabbitPassword,rabbitPort,queueName);
            Future<String> future = executor.submit(task2);
            result = future.get();
            if(StringUtils.isNotBlank(result)) {
                result = result.replaceAll("\n", "<br/>");
            }
            executor.shutdown();
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/delete-job-exe")
    public boolean deleteJobExeDemo(@RequestParam Long jobId) {
        return jobOpsExeApprovalService.deleteJobExecution(jobId);
    }

    @PostMapping("/send-request-email")
    public ResponseEntity<String>  sendEmailRequest(
            @RequestParam(value = "jobName", defaultValue = "") String jobName,
            @RequestParam(value = "requestAt", defaultValue = "") String requestAt,
            @RequestParam(value = "approvalUrl", defaultValue = "") String approvalUrl,
            @RequestParam(value = "email", defaultValue = "") String email) {

        emailService.sendRequestJobExecutionEmail(jobName,requestAt,approvalUrl,email);
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }

    @GetMapping("/send-reject-email")
    public ResponseEntity<String>  sendRejectEmail(
            @RequestParam(value = "jobName", defaultValue = "") String jobName,
            @RequestParam(value = "approvalName", defaultValue = "") String approvalName,
            @RequestParam(value = "requestAt", defaultValue = "") String requestAt,
            @RequestParam(value = "approvalUrl", defaultValue = "") String approvalUrl,
            @RequestParam(value = "email", defaultValue = "") String email) {
        emailService.sendRejectJobEmail(jobName,approvalName,requestAt,approvalUrl,email);
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }

    @GetMapping("/send-approval-email")
    public ResponseEntity<String>  sendApprovalEmail(
            @RequestParam(value = "jobName", defaultValue = "") String jobName,
            @RequestParam(value = "approvalName", defaultValue = "") String approvalName,
            @RequestParam(value = "requestAt", defaultValue = "") String requestAt,
            @RequestParam(value = "approvalUrl", defaultValue = "") String approvalUrl,
            @RequestParam(value = "email", defaultValue = "") String email) {

        emailService.sendApproveJobEmail(jobName,approvalName,requestAt,approvalUrl,email);
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }
    @PostMapping ("/get-compare-result")
    public ResponseEntity<Boolean> getCompareResult(
            @RequestParam(value = "src", defaultValue = "") String src,
            @RequestParam(value = "target", defaultValue = "") String target,
            @RequestParam(value = "key", defaultValue = "=") String key,
            @RequestParam(value = "workflowId", defaultValue = "0") long workflowId) {
        Boolean result;
        if(StringUtils.isNotBlank(src) && src.startsWith("Jobinput.")) {
            src = src.split("\\.")[1];
        }
        if(StringUtils.isNotBlank(jobInputService.findValueByVariableAndWorkflowId(workflowId, src))) {
            src = jobInputService.findValueByVariableAndWorkflowId(workflowId, src);
        }

        if(key.toLowerCase().contains("like")|| key.toLowerCase().matches("=")) {
            result = OperatorUtil.compareStr(key.toLowerCase(),src,target);
        }
        else {
            result = OperatorUtil.compareOps(key,Integer.parseInt(src),Integer.parseInt(target));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping ("/save-jobopsexeapproval")
    public ResponseEntity<String> saveJobOpsExeApproval(@RequestParam String jobExeId,@RequestParam String approvedBy)
    {
        jobOpsExeApprovalService.saveJobOpsExeApproval(jobExeId, approvedBy);
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }

    @PostMapping ("/export-log-execute")
    public ResponseEntity<String> exportLogExecute(
            @RequestParam(value = "txt") String txt,@RequestParam(value = "path") String path) {

        String newLine = System.getProperty("line.separator");
        try {
            FileWriter myWriter = fileStorageService.getWriter(path);
            String[] txts = txt.split("<br>");
            for(int i =0;i<txts.length;i++)
            {
                myWriter.write(txts[i]+newLine);
            }
            myWriter.close();
        } catch (IOException e) {
            logger.error("Exception: MyJobRestController.exportLogExecute ", e);
        }
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }

    @PostMapping ("/send-mail-log-execute")
    public ResponseEntity<String> sendMailLogExecute(
            @RequestParam String contentFile, @RequestParam String fileName, @RequestParam Long jobAssignId) {
        JobAssignment jobAssignment = jobAssignmentService.getJobAssignmentById(jobAssignId);
        String newLine = System.getProperty("line.separator");
        try {
            FileWriter myWriter = fileStorageService.getWriter(fileName);
            String[] content = contentFile.split("<br>");
            for(int i =0;i<content.length;i++)
            {
                myWriter.write(content[i]+newLine);
            }
            myWriter.close();
            String pathFile = uploadDir+CommonConstants.PATH_DELIMITER+fileName;
            emailService.executionFinishSendEmail(jobAssignment, pathFile, fileName, true);
            emailService.executionFinishSendEmail(jobAssignment, pathFile, fileName, false);
        } catch (IOException e) {
            logger.error("Exception: MyJobRestController.sendMailLogExecute ", e);
        }
        return new ResponseEntity<>(CommonConstants.SUCCESS, HttpStatus.OK);
    }

    @PostMapping("/create-playbook-exe-folder")
    public  ResponseEntity<String> createPlaybookExeFolder(@RequestBody AnsibleFileDTO  body, HttpServletRequest request)
    {
        ResultDTO result = new ResultDTO();
        try{
//                create ansible playbook
            Map<String, String> substitutesf = new HashMap<>();
            substitutesf.put("foldername",body.getFoldername());
            StrSubstitutor subf = new StrSubstitutor(substitutesf);
            String folderName = subf.replace(CommonConstants.PATH_DELIMITER + JobExeConstants.FOLDER);
            String path1 = ansibleDir+ folderName + CommonConstants.PATH_DELIMITER + JobExeConstants.PATH_1;
            String path2 = ansibleDir+ folderName + CommonConstants.PATH_DELIMITER + JobExeConstants.PATH_2;
            String path3 = ansibleDir+ folderName + CommonConstants.PATH_DELIMITER + JobExeConstants.PATH_3;
            String path4 = ansibleDir+ folderName + CommonConstants.PATH_DELIMITER + JobExeConstants.PATH_4;
            List<String> folders = new ArrayList<>();
            folders.add(path1);
            folders.add(path2);
            folders.add(path3);
            folders.add(path4);


            Map<String, String> substitutes = new HashMap<>();
            substitutes.put("role",body.getRole());
            substitutes.put("host",body.getRole());
            substitutes.put("title",body.getJobName());
            StrSubstitutor sub = new StrSubstitutor(substitutes);

            folders.forEach(item->new File(sub.replace(item)).mkdirs());
            createFileRoleYML(folderName, sub);
            createFileMainYML(body, path2, sub);
            createFileHost(body, path1, path3, path4, sub);

        } catch(Exception e) {
            logger.error("Exception: MyJobRestController.createPlaybookExeFolder ", e);
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMess("Create create-playbook-exe-folder false");
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body.getFoldername(), HttpStatus.OK);
    }

    private void createFileHost(AnsibleFileDTO body, String path1, String path3, String path4, StrSubstitutor sub) throws IOException {
        FileWriter myWriter = new FileWriter(path1 + CommonConstants.PATH_DELIMITER + JobExeConstants.PATH_HOST);
        try {
            myWriter.write(sub.replace(JobExeConstants.HEADER_HOST));
            for(int i =0;i<body.getHostname().size();i++)
            {
                Map<String, String> substituteshost = new HashMap<>();
                substituteshost.put("hostname", body.getHostname().get(i));
                substituteshost.put("user", body.getUser().get(i));
                substituteshost.put("pass", body.getPass().get(i));
                substituteshost.put("port", body.getPort().get(i));
                substituteshost.put("address_ip",body.getAddressIps().get(i));

                StrSubstitutor subhost = new StrSubstitutor(substituteshost);

                myWriter.write(subhost.replace(JobExeConstants.HOST_CONTENT));

            }
            for (String s:body.getYml()) {

                Path sourcePath = Paths.get(uploadDir+CommonConstants.PATH_DELIMITER+s);
                Path destinationPath =  Paths.get(sub.replace(path3)+CommonConstants.PATH_DELIMITER+"main.yml");
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            }
            for (String s:body.getFiles())
            {
                Path sourcePath = Paths.get(uploadDir+CommonConstants.PATH_DELIMITER+s);
                Path destinationPath = Paths.get(sub.replace(path4)+CommonConstants.PATH_DELIMITER+s);
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            logger.error("Exception: MyJobResController.createFileHost ", e);
        } finally {
            myWriter.close();
        }
    }

    private void createFileMainYML(@RequestBody AnsibleFileDTO body, String path2, StrSubstitutor sub) throws IOException {
        FileWriter defaultWriter = new FileWriter(sub.replace(path2)+"main.yml");
        try {
            defaultWriter.write(JobExeConstants.HEADER_ANSIBLE);
            Map<String, String> mapPlaybook = new ConcurrentHashMap<>();
            if(CollectionUtils.isNotEmpty(body.getResultPlaybooks())) {
                String resultPlaybookExe = "";
                String[] arrPlaybook = body.getResultPlaybooks().get(0).split("-==-");
                Pattern pattern = Pattern.compile("msg\": \"(.*?)\"");
                Matcher matcher = pattern.matcher(arrPlaybook[1]);
                if(matcher.find()) {
                    if(matcher.group(1).indexOf('=') > -1) {
                        resultPlaybookExe = matcher.group(1).split("=")[1].replace("\"", "");
                    } else {
                        resultPlaybookExe = matcher.group(1).replace("\"", "");
                    }
                }
                mapPlaybook.put(arrPlaybook[0], resultPlaybookExe);
            }

            setDefaultContent(body, defaultWriter, mapPlaybook);
        } catch (Exception e) {
            logger.error("Exception: MyJobRestController.createFileMainYML ", e);
        } finally {
            defaultWriter.close();
        }
    }

    private void setDefaultContent(@RequestBody AnsibleFileDTO body, FileWriter defaultWriter, Map<String, String> mapPlaybook) throws IOException {
        for(int i =0 ;i<body.getVariable().size();i++)
        {
            Map<String, String> substitutesVariable = new HashMap<>();
            substitutesVariable.put("variable",body.getVariable().get(i));
            String fileName = fileManagementService.findFileNameByName(body.getValue().get(i));

            if(StringUtils.isNotBlank(fileName)) {
                substitutesVariable.put(CONST_VALUE,fileName);
            } else {
                if(!checkAllCase(body, mapPlaybook, i, false, substitutesVariable)){
                    substitutesVariable.put(CONST_VALUE,body.getValue().get(i));
                }
            }

            StrSubstitutor subVariable = new StrSubstitutor(substitutesVariable);
            defaultWriter.write(subVariable.replace(JobExeConstants.DEFAULT_CONTENT));
        }
    }

    private boolean checkAllCase(@RequestBody AnsibleFileDTO body, Map<String, String> mapPlaybook, int i, boolean flag, Map<String, String> substitutesVariable) {
        if(body.getValue().get(i).indexOf('.') > -1) {
            String[] infoPlaybook = body.getValue().get(i).split("\\.");

            String valuePlaybook = infoPlaybook[1];
            String namePlaybook = infoPlaybook[0];
            for(JobPayloadDTO jobPayloadDTO : body.getJobPayloads()) {
                if(StringUtils.equals(jobInputService.findVariableById(jobPayloadDTO.getJobInputId()), valuePlaybook)) {
                    substitutesVariable.put(CONST_VALUE, jobPayloadDTO.getValue());
                    flag = true;
                    break;
                }
            }
            if(!flag && !mapPlaybook.isEmpty()) {
                String value = mapPlaybook.get(namePlaybook);
                if(StringUtils.isNotBlank(value)) {
                    flag = true;
                    substitutesVariable.put(CONST_VALUE,value);
                }
            }
        }
        return flag;
    }

    private void createFileRoleYML(String folderName, StrSubstitutor sub) throws IOException {
        FileWriter mainWriter = new FileWriter(ansibleDir+folderName+sub.replace(CommonConstants.PATH_DELIMITER+JobExeConstants.PATH_MAIN));
        try {
            mainWriter.write(JobExeConstants.HEADER_ANSIBLE);
            mainWriter.write(sub.replace(JobExeConstants.MAINTITLE));
            mainWriter.write(sub.replace(JobExeConstants.MAINCONTENT));
            mainWriter.close();
        } catch (Exception e) {
            logger.error("Exception: MyJobRestController.createFileRoleYML ", e);
        } finally {
            mainWriter.close();
        }
    }

    @GetMapping("/get-job-exe")
    public  ResponseEntity<JobExecution> getJobExe (@RequestParam long id,HttpServletRequest request) {
        return new ResponseEntity<>(jobOpsExeApprovalService.findById(id), HttpStatus.OK);
    }
    @PostMapping("/save-job-exe")
    public ResponseEntity<Long> saveJobExe(@RequestBody JobExeDTO  body, HttpServletRequest request)
    {
        ResultDTO result = new ResultDTO();
        long jobexeid = 0;
        try {
            jobexeid = jobOpsExeApprovalService.saveJobExecution(body);
            UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            if(userProfileDto != null) {
                String nameCreatedBy =  userProfileDto.getFullName();
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = CONST_JOBEXE + body.getId() + ") has been created by " + nameCreatedBy;
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        nameCreatedBy,
                        userProfileDto.getGroups(),
                        details,
                        "Create Job Execution Successful",
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setCode(HttpStatus.OK.value());
                result.setMess("Create job exe success");
            }
        } catch(Exception e) {
            logger.error("Exception: MyJobRestController.saveJobExe ", e);
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMess("Create job exe false");

        }
        return new ResponseEntity<>(jobexeid, HttpStatus.OK);
    }

    @GetMapping("/get-planner")
    public ResponseEntity<List<UserAssignDTO>> getListPlanner() {
        return new ResponseEntity<>(userService.getListPlanner(), HttpStatus.OK);
    }

    @GetMapping("/get-assignee")
    public ResponseEntity<List<UserAssignDTO>> getListAssignee() {
        return new ResponseEntity<>(userService.getListAssignee(), HttpStatus.OK);
    }

    @GetMapping("/get-job-management")
    public ResponseEntity<List<JobViewDTO>> getListJob() {
        return new ResponseEntity<>(jobService.getListJobForPlanning(), HttpStatus.OK);
    }

    @GetMapping("/get-job-view-by-id")
    public ResponseEntity<JobViewDTO> getJobDetailById(@RequestParam Long id) {
        JobViewDTO data = new JobViewDTO();
        try {
            data = jobService.findViewJobById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception: MyJobRestController.getJobDetailById ", e);
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
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        String details = "create new job planning by " + data.getEmail();
        String desc = "Create Job Planning";
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
        } catch (Exception e) {
            logger.error("Exception: MyJobRestController.createJobPlan ", e);
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
            data = (UserProfileDto) request.getSession().getAttribute("userProfile");
            body.setModifiedBy(data.getFullName());
            jobAssignmentService.updateJobPlanning(body, userService.findById(body.getAssigneeId()), userService.findById(body.getPlannerId()));
            result.setCode(200);
            result.setMess("success");
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        String details = "update job planning by " + data.getEmail();
        String desc = "update Job Planning";
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
        } catch (Exception e) {
            logger.error("Exception: MyJobRestController.updateJobPlan ", e);
            result.setCode(500);
            result.setDetail("Internal Server Exception!");
            result.setDetail(e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-job-status")
    public ResponseEntity<Set<String>> getListStatus() {
        return new ResponseEntity<>(JobPlanStatus.getJobPlanningStatus(), HttpStatus.OK);
    }

    @GetMapping("/get-job-calendar-view")
    public ResponseEntity<List<JobCalendarViewDTO>> getJobForCalendarView(HttpServletRequest request) {
        List<JobCalendarViewDTO> data = new ArrayList<>();
        try{
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            User user = userService.findUserByEmail(profile.getEmail());
            data = jobAssignmentService.findAllMyJob(user);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    @PostMapping("/waiting-approve-update")
    public  void waitingapprovaupdate(@RequestParam long id, HttpServletRequest request)
    {
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        SystemAuditLog systemAuditLog;
        CommonMethodComponent commonMethodComponent;
        JobAssignment jobAssignment = jobAssignmentService.updateStatusJobExe(id, JobPlanStatus.EXECUTING, "Start running");
        String detailsLog = AuditLogUtil.getDetailsAudit(jobAssignment.getJobName(), data.getEmail(), "executed");
        systemAuditLog = AuditLogUtil.getSystemAuditLog(data.getFullName(), data.getGroups(), detailsLog, "The job execution has been executed");
        iSystemAuditLogRepository.save(systemAuditLog);

        String details = "Job has been waiting for approve ";
        String desc = "Job has been waiting for approve";
        systemAuditLog = new SystemAuditLog();
        commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog,
                data.getFullName(),
                data.getGroups(),
                details,
                desc,
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
    }
    @PostMapping("/accept-or-reject-job")
    public void  rejectOrAcceptJob(@RequestParam long id, @RequestParam boolean status, @RequestParam String reason, HttpServletRequest request) {
        jobAssignmentService.updateStatus(id, status, reason);
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        String details = "Job has been rejected by " + data.getEmail();
        String desc = "Rejected Job Planning Successful";
        if(status) {
            details = "Job has been accepted by " + data.getEmail();
            desc = "Accepted Job Planning Successful";
        }
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog,
                data.getFullName(),
                data.getGroups(),
                details,
                desc,
                true);
        iSystemAuditLogRepository.save(systemAuditLog);
    }

    @PostMapping("/update-status-finish-job")
    public void  updateStatusFinishJob(@RequestParam long id, @RequestParam String status) {
        jobAssignmentService.updateFinishStatusJob(id, status);
    }

    @GetMapping("/get-user-default")
    public ResponseEntity<UserDefaultDto> getUserDefault(HttpServletRequest request) {
        UserDefaultDto userDto = null;
        User user;
        try{
            UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            user = userService.findUserByEmail(profile.getEmail());
            userDto = new UserDefaultDto();
            userDto.setId(user.getId());
        } catch (Exception e){
            logger.error("Exception: MyJobRestController.getUserDefault ", e);
        }
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/get-engineer-job")
    public Map<String, Object> getJobAssignmentEngineerDashboard(HttpServletRequest request) {
        UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        User user = userService.findUserByEmail(profile.getEmail());
        return jobAssignmentService.findJobForEngineerDashboard(user);
    }
}