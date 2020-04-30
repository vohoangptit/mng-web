package com.nera.nms.controllers;

import com.nera.nms.components.MenuComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.RolesConstants;
import com.nera.nms.dto.SystemSettingDto;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.dto.WorkflowStatisticDTO;
import com.nera.nms.enums.Menu;
import com.nera.nms.models.Group;
import com.nera.nms.repositories.IGroupRepository;
import com.nera.nms.repositories.PlaybookRepository;
import com.nera.nms.services.GroupService;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.SystemSettingService;
import com.nera.nms.services.UserAccessPermissionService;
import com.nera.nms.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class MenuController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MenuComponent menuComponent;

    @Autowired
    IGroupRepository igroupRepo;

    @Autowired
    GroupService groupService;

    @Autowired
    UserAccessPermissionService accessService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    PlaybookRepository playbookRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JobAssignmentService jobAssignmentService;

    @GetMapping("/menu/system/setting")
    @PreAuthorize("hasAuthority('VIEW_ALL_SETTINGS')")
    public String settingMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "System/Setting");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.SETTING);

        SystemSettingDto systemSettingDto = systemSettingService.findSystemSetting();
        model.addAttribute("systemSetting", systemSettingDto);
        return "system/general";
    }

    @GetMapping("/menu/system/access-control")
    @PreAuthorize("hasAuthority('VIEW_ACCESS_CONTROL')")
    public String accessControlMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "System/Access Control");

        //load all group
        List<Group> groups = igroupRepo.findAllByIsActive(true, false);
        if (groups != null) {
            //load role for group
            LinkedHashMap<String, List<String>> listRole = new LinkedHashMap<>();
            for (Group gr : groups) {
                if (gr.getId() == CommonConstants.SUPER_ADMIN_ID) {
                    continue;
                }

                List<String> data = groupService.getPermissionByGroupName(gr.getGroupName());
                listRole.put(gr.getGroupName(), data);
            }
            model.addAttribute("listRole", listRole);
        } else {
            model.addAttribute("listRole", new LinkedHashMap<String, List<String>>());
        }
        model.addAttribute("roles", RolesConstants.getPERMISSION());

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.ACCESS_CONTROL);

        return "access_control/access-control";
    }

    @GetMapping("/menu/system/user")
    @PreAuthorize("hasAuthority('VIEW_USER_LISTING')")
    public String userMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "System/User");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.USER);

        return "custom/user/userListing";
    }

    @GetMapping("/menu/system/user-group")
    @PreAuthorize("hasAuthority('VIEW_USER_GROUP_LISTING')")
    public String userGroupMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "System/User Group");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.USER_GROUP);
        return "custom/user/userGroupListing";
    }

    @GetMapping("/menu/system/audit")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOG')")
    public String auditMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "System/Audit Log");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.AUDIT);

        return "audit_log/audit-log";
    }

    @GetMapping("/menu/masterdata/playbook")
    @PreAuthorize("hasAuthority('VIEW_PLAYBOOK_LISTING')")
    public String playbookMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Master Data/Playbook");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.PLAYBOOK);

        return "playbook/playbook-listing";
    }

    @GetMapping("/menu/masterdata/inventory")
    public String inventoryMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Master Data/Inventory");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.INVENTORY);

        return "inventory/inventory-details";
    }

    @GetMapping("/menu/masterdata/fileManagement")
    @PreAuthorize("hasAuthority('VIEW_FILE_LISTING')")
    public String fileManagementMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Master Data/File Management");

        //init menu bar
        menuComponent.initMenuModalView(model, Menu.FILEMANAGEMENT);

        return "filemanagement/file-listing";
    }

    @GetMapping("/menu/masterdata/playbook-approved")
    @PreAuthorize("hasAuthority('VIEW_APPROVED_PLAYBOOK_LISTING')")
    public String approvedPlaybook(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Master Data/Playbook Approved");

        //init menu bar
        int number;
        number = playbookRepository.countByStatusNew();
        model.addAttribute("menuName", "Playbook Approval (" + number + ")");
        menuComponent.initMenuModalView(model, Menu.PLAYBOOK_APPROVED);
        return "playbook/playbookapproved-listing";
    }

    @GetMapping("/menu/system/email-template")
    public String emailTemplate(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Master Data/Email Template");
        menuComponent.initMenuModalView(model, Menu.EMAIL_TEMPLATE);
        return "system/general";
    }

    @GetMapping("/profile")
    public String profile(Model model, @RequestParam(name = "uploadSuccess", defaultValue = "") String uploadSuccess) {
        model.addAttribute("upload", uploadSuccess);
        return "custom/index";
    }

    @GetMapping("/menu/workflow/workflow-listing")
    @PreAuthorize("hasAuthority('VIEW_WORKFLOW_LISTING')")
    public String workflowListingMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Workflow Engine/Workflow Listing");
        //init menu bar
        menuComponent.initMenuModalView(model, Menu.WORKFLOW);
        return "workflow/workflow-listing";
    }

    @GetMapping("/menu/workflow/workflow-history/{id}")
    public String workflowHistory(Model model, @PathVariable Long id) {
        //init menu bar
        menuComponent.initMenuModalView(model, Menu.WORKFLOW);
        model.addAttribute("idWorkflow", id);
        return "workflow/workflow-history";
    }

    @GetMapping("/menu/job-planning/calendar")
    @PreAuthorize("hasAuthority('VIEW_JOB_PLANNING')")
    public String calendar(Model model) {
        menuComponent.initMenuModalView(model, Menu.JOB_PLAN_CALENDAR);
        return "job_planning/calendar";
    }

    @GetMapping("/menu/job-planning/listing")
    @PreAuthorize("hasAuthority('VIEW_JOB_PLANNING')")
    public String jobPlanning(Model model) {
        menuComponent.initMenuModalView(model, Menu.JOB_PLAN_LISTING);
        return "job_planning/listing";
    }

    @GetMapping("/menu/job-listing")
    @PreAuthorize("hasAuthority('VIEW_JOB_LISTING')")
    public String jobManagementMenuBar(Model model) {
        model.addAttribute(CommonConstants.TITLE_PAGE, "Job Management");
        //init menu bar
        menuComponent.initMenuModalView(model, Menu.JOB_MANAGEMENT);
        return "job/job-listing";
    }

    @GetMapping("/menu/my-job/listing")
    @PreAuthorize("hasAuthority('VIEW_MY_JOBS')")
    public String myJobListingView(Model model) {
        menuComponent.initMenuModalView(model, Menu.MY_JOB_LISTING);
        return "my_job/listing";
    }

    @GetMapping("/menu/my-job/calendar")
    @PreAuthorize("hasAuthority('VIEW_MY_JOBS')")
    public String myJobCalendarView(Model model) {
        menuComponent.initMenuModalView(model, Menu.MY_JOB_CALENDAR);
        return "my_job/calendar";
    }

    @GetMapping("/menu/approval-request")
    @PreAuthorize("hasAuthority('VIEW_MY_APPROVAL_REQUEST')")
    public String getMyApprovalRequestListingView(Model model) {
        return "approval-request/listing";
    }

    @GetMapping("/menu/my-job/execution")
    public String myJobExecutionView() {
        return "my_job/execution";
    }

    @GetMapping("/menu/dashboard")
    public String dashboardView(Model model, HttpServletRequest request) {
        menuComponent.initMenuModalView(model, Menu.DASHBOARD);
        UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        if(userProfileDto != null) {
            if(StringUtils.containsIgnoreCase(userProfileDto.getGroups(), "administrator")) {
                return adminDashboard(model);
            } else if(StringUtils.containsIgnoreCase(userProfileDto.getGroups(), "leader")) {
                return "dashboard/team_leader";
            } else if(StringUtils.containsIgnoreCase(userProfileDto.getGroups(), "engineer")) {
                return "dashboard/engineer";
            }
        }
        return "index";
    }

    private String adminDashboard(Model model) {
        try {
            List<WorkflowStatisticDTO> workflow = jobAssignmentService.statisticWorkflow();
            model.addAttribute("workflow", workflow);
        } catch (Exception e) {
            logger.error("Exception: MenuController.adminDashboard ", e);
        }
        return "dashboard/admin";
    }
}
