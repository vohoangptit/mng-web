package com.nera.nms.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.UserConstants;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.Group;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.models.UserDepartment;
import com.nera.nms.models.UserJobTitle;
import com.nera.nms.repositories.IGroupRepository;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserDepartmentRepository;
import com.nera.nms.repositories.IUserJobTitleRepository;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.services.EmailService;
import com.nera.nms.services.JobAssignmentService;
import com.nera.nms.services.UserService;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.ConcateGroupUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserListingController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String ADD_USER_STATUS = "addUserStatus";

	private static final String EDIT_USER_STATUS = "editUserStatus";

	private static final String CONST_REDIRECT = "redirect:/";

	private static final String PATH_USER_LISTING = "menu/system/userListing";

	@Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;
	
	@Autowired
	IUserDepartmentRepository iUserDepartmentRepository;

	@Autowired
	IGroupRepository iGroupRepository;
	
	@Autowired
	IUserJobTitleRepository iUserJobTitleRepository;
	
	@Autowired
	IUserRepository iUserRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	EmailService emailService;

	@Autowired
	JobAssignmentService jobAssignmentService;
	
    @GetMapping(value = "/menu/system/userListing")
    public ModelAndView index(HttpServletRequest request){
    	HttpSession session = request.getSession();
    	String addUserStatus = "";
    	String editUserStatus = "";
    	if (session.getAttribute(ADD_USER_STATUS) != null) {
    		 addUserStatus = (String) session.getAttribute(ADD_USER_STATUS);
    		 session.setAttribute(ADD_USER_STATUS, StringUtils.EMPTY);
    	}
    	
    	if (session.getAttribute(EDIT_USER_STATUS) != null) {
    		editUserStatus = (String) session.getAttribute(EDIT_USER_STATUS);
   		 	session.setAttribute(EDIT_USER_STATUS, StringUtils.EMPTY);
    	}
   	

    	ModelAndView modelAndView = new ModelAndView("custom/user/userListing");
    	modelAndView.addObject(ADD_USER_STATUS, addUserStatus);
    	modelAndView.addObject(EDIT_USER_STATUS, editUserStatus);

        return modelAndView; 
    }
    
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    @GetMapping(value = "/addUser")
    public ModelAndView addUser(){
    	ModelAndView modelAndView = new ModelAndView("custom/user/addUser");
    	getUserPageInfo(modelAndView);
        return modelAndView;
    }
    
    private void getUserPageInfo(final ModelAndView modelAndView) {
    	List <UserDepartment> userDepartment = iUserDepartmentRepository.findAllByOrderByDepartmentName();
    	Map <Long, String> userDepartmentMap = new HashMap<>();
    	userDepartment.forEach(department -> userDepartmentMap.put(department.getId(), department.getDepartmentName()));
    	modelAndView.addObject("departments", userDepartmentMap);
    	
    	List <Group> userGroups = iGroupRepository.findAllByOrderByGroupName(true,false);
    	Map <Long, String> userGroupsMap = new HashMap<>();
    	userGroups.forEach(userGroup -> userGroupsMap.put(userGroup.getId(), userGroup.getGroupName()));
    	modelAndView.addObject("groups", userGroupsMap);
    	
    	List <UserJobTitle> jobTitles = iUserJobTitleRepository.findAllByOrderByJobTitleName();
    	Map <Long, String> jobTitlesMap = new HashMap<>();
    	jobTitles.forEach(jobTitle -> jobTitlesMap.put(jobTitle.getId(), jobTitle.getJobTitleName()));
    	modelAndView.addObject("jobTitles", jobTitlesMap);
    }
    
    @PreAuthorize("hasAuthority('CREATE_USERS')") 
    @PostMapping(value = "/addUserForm")
    public ModelAndView addUser (@RequestParam(value="email") String email,
    		@RequestParam(value="fullName") String fullName,
    		@RequestParam(value="selectedGroupsIds[]") String selectionGroupsIds,
    		@RequestParam(value="department", required = false) String department,
    		@RequestParam(value="jobTitle", required = false) String jobTitle,
    		@RequestParam(value="mobileNumber", required = false) String mobileNumber,
    		@RequestParam(value="status") String status,
    		@RequestParam(value="image", required = false) MultipartFile image,
    		@RequestParam(value="selectedGroupsNames", required = false) String selectedGroupsNames,
    		@RequestParam(value="selectedDepartmentName", required = false) String selectedDepartmentName,
    		@RequestParam(value="selectedJobTitleName", required = false) String selectedJobTitleName,
    		HttpServletRequest request) {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        }

        List <Group> loadedGroupList = iGroupRepository.findAll();
    	String [] groupIds = StringUtils.split(selectionGroupsIds, ',');
    	List <Long> groupIdsList = new ArrayList<> ();
    	for (String id: groupIds) {
    		groupIdsList.add(Long.parseLong(id));
    	}
    	
    	List<Group> filteredGroupList = loadedGroupList.stream()
                .filter(x->groupIdsList.contains(x.getId()))
                .collect(Collectors.toList());

    	boolean statusBool = false;
    	String statusStr = CommonConstants.INACTIVE;
    	if (status.equalsIgnoreCase("Y")) {
    		statusBool = true;
    		statusStr = "Active";
    	}
    	
    	String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEndcode = bCryptPasswordEncoder.encode(randomPassword);
    	
    	User user = new User();
    	user.setEmail(email);
    	user.setFullName(fullName);
    	user.setGroups(filteredGroupList);
    	if (department != null ) {
    		user.setDepartmentId(Long.parseLong(department));
    		user.setDepartmentName(selectedDepartmentName);
    	}
    	
    	user.setMobileNumber(mobileNumber);
    	if (jobTitle != null ) {
    		user.setJobTitleId(Long.parseLong(jobTitle));
    		user.setJobTitleName(selectedJobTitleName);
    	}
    	user.setActive(statusBool);
    	user.setStatus(statusStr);
    	user.setPassword(passwordEndcode);
    	user.setCreatedBy(username);
    	user.setCreatedDate(new Date());
    	if (image != null) {
	    	try {
				user.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
			} catch (IOException e) {
				logger.error("Exception: UserListingController.addUser ", e);
				ModelAndView modelAndView = new ModelAndView(CONST_REDIRECT+PATH_USER_LISTING);
				request.getSession().setAttribute(ADD_USER_STATUS, CommonConstants.CONST_FALSE);
		    	
		    	return modelAndView;
			}
    	}
    	iUserRepository.save(user);
    	
        String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());

    	String desc = "Create new user.";
        String details = "User " + username + " has created a new user with datails: "
        		+ " Name: " + user.getFullName() 
        		+ " Email:"  + user.getEmail()
        		+ " UserGroup: " + selectedGroupsNames
        		+ " Department: " + selectedDepartmentName
        		+ " JobTitle: " + selectedJobTitleName
        		+ " Mobile Number: " + user.getMobileNumber()
        		+ " Status: " + (statusBool ? CommonConstants.ACTIVE : CommonConstants.INACTIVE);
        
        SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.updateUserLogData(systemAuditLog, 
        		user, 
        		groupStr, 
        		details, 
        		desc, 
        		true);
       
        iSystemAuditLogRepository.save(systemAuditLog);

		if (!emailService.sendEmailForNewAccount(randomPassword, email, user.getFullName(), request)) {
			ModelAndView modelAndView = new ModelAndView(CONST_REDIRECT+PATH_USER_LISTING);
			request.getSession().setAttribute(ADD_USER_STATUS, CommonConstants.CONST_FALSE);
			return modelAndView;
		}
    	
    	ModelAndView modelAndView = new ModelAndView(CONST_REDIRECT+PATH_USER_LISTING);
    	request.getSession().setAttribute(ADD_USER_STATUS, CommonConstants.CONST_TRUE);
    	
    	return modelAndView;
    }
    
    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    @GetMapping(value = "/editUser")
    public ModelAndView editUser(@RequestParam (value="userId") String userIdStr){
    	ModelAndView modelAndView = new ModelAndView("custom/user/userDetail");
    	long userId = Long.parseLong(userIdStr);
    	
    	User user = iUserRepository.findById(userId);
    	modelAndView.addObject("user", user);
    	
    	String lastLogin = BEDateUtils.formatDate(user.getLastLoginDateTime(), CommonConstants.DATE_TIME_FORMAT);
    	user.setFormattedLastLogin(lastLogin);
    	
    	StringBuilder groupIds = new StringBuilder();
    	StringBuilder groupNames = new StringBuilder() ;

    	user.getGroups().forEach(group -> {
    		String id = String.valueOf(group.getId());
    		groupIds.append(id).append(CommonConstants.COMMA);
    		
    		String name = group.getGroupName();
    		groupNames.append(name).append(CommonConstants.COMMA);
    	});
    	String groupIdsStr = "";
    	String groupNamesStr = "";
    	if (groupIds.length() > 0) {
	    	groupIdsStr = groupIds.substring(0, groupIds.length() - 1);
	    	groupNamesStr = groupNames.substring(0, groupNames.length() - 1);
    	}
    	modelAndView.addObject("groupIds", groupIdsStr);
    	modelAndView.addObject("groupNames", groupNamesStr);
    	
    	boolean showUnblockBtn = true;
    	if (user.getLoginCount() < UserConstants.INVALID_LOGIN_MAX) {
    		showUnblockBtn = false;
    	}
    	modelAndView.addObject("showUnblockBtn", showUnblockBtn);
    	
    	getUserPageInfo(modelAndView);
    	return modelAndView;
    }
    
    @PostMapping("/updateUserForm")
    public ModelAndView updateUser (@RequestParam(value="id") String id,
    		@RequestParam(value="email") String email,
    		@RequestParam(value="fullName") String fullName,
    		@RequestParam(value="selectedGroupsIds[]") String selectionGroupsIds,
    		@RequestParam(value="department", required = false) String department,
    		@RequestParam(value="jobTitle", required = false) String jobTitle,
    		@RequestParam(value="mobileNumber", required = false) String mobileNumber,
    		@RequestParam(value="status") String status,
    		@RequestParam(value="image", required = false) MultipartFile image,
    		@RequestParam(value="selectedGroupsNames", required = false) String selectedGroupsNames,
    		@RequestParam(value="selectedDepartmentName", required = false) String selectedDepartmentName,
    		@RequestParam(value="selectedJobTitleName", required = false) String selectedJobTitleName,
    		HttpServletRequest request) {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        }
        
        User loggedInUser = userService.findUserByEmail(username);
        
        User savedUser = userService.findUserById(id);
        
        if (department != null ) {
        	savedUser.setDepartmentId(Long.parseLong(department));
        	savedUser.setDepartmentName(selectedDepartmentName);
    	}
    	
    	if (jobTitle != null ) {
    		savedUser.setJobTitleId(Long.parseLong(jobTitle));
    		savedUser.setJobTitleName(selectedJobTitleName);
    	}        
        boolean statusBool = false;
    	String statusStr = UserConstants.INACTIVE;
    	if (status.equalsIgnoreCase("Y")) {
    		statusBool = true;
    		statusStr = UserConstants.ACTIVE;
    	} else if (status.equalsIgnoreCase(UserConstants.BLOCKED)) {
    		statusStr = UserConstants.BLOCKED;
    	}
        savedUser.setEmail(email);
        savedUser.setFullName(fullName);
        savedUser.setMobileNumber(mobileNumber);
        savedUser.setActive(statusBool);
        savedUser.setStatus(statusStr);        
        savedUser.setModifiedBy(loggedInUser.getFullName());
        savedUser.setModifiedDate(new Date());
        if (image.getSize() != 0) {
	    	try {
	    		savedUser.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
			} catch (IOException e) {
				logger.error("Exception: UserListingController.updateUser ", e);
				ModelAndView modelAndView = new ModelAndView(CONST_REDIRECT+PATH_USER_LISTING);
				request.getSession().setAttribute(EDIT_USER_STATUS, CommonConstants.CONST_FALSE);
		    	
		    	return modelAndView;
			}
    	}
        
        List <Group> loadedGroupList = iGroupRepository.findAll();
    	String [] groupIds = StringUtils.split(selectionGroupsIds, ',');
    	List <Long> groupIdsList = new ArrayList<> ();
    	for (String groupId: groupIds) {
    		groupIdsList.add(Long.parseLong(groupId));
    	}
    	
    	List<Group> filteredGroupList = loadedGroupList.stream()
                .filter(x->groupIdsList.contains(x.getId()))
                .collect(Collectors.toList());
    	
    	savedUser.setGroups(filteredGroupList);
    	if (department != null ) {
    		savedUser.setDepartmentId(Long.parseLong(department));
    	}
    	
    	savedUser.setMobileNumber(mobileNumber);
    	if (jobTitle != null ) {
    		savedUser.setJobTitleId(Long.parseLong(jobTitle));
    	}
        
    	iUserRepository.save(savedUser);
    	
    	String groupStr = ConcateGroupUtil.concateGroupNames(savedUser.getGroups());

    	String desc = "Edit user.";
        String details = "User " + username + " has edited user with datails: "
        		+ " Name: " + savedUser.getFullName() 
        		+ " Email:"  + savedUser.getEmail()
        		+ " UserGroup: " + selectedGroupsNames
        		+ " Department: " + selectedDepartmentName
        		+ " JobTitle: " + selectedJobTitleName
        		+ " Mobile Number: " + savedUser.getMobileNumber()
        		+ " Status: " + (statusBool ? CommonConstants.ACTIVE : CommonConstants.INACTIVE);
        
        SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.updateUserLogData(systemAuditLog, 
        		loggedInUser, 
        		groupStr, 
        		details, 
        		desc, 
        		true);
       
        iSystemAuditLogRepository.save(systemAuditLog);
		// update full name planner job
        List<JobAssignment> planners = jobAssignmentService.findByPlannerId(Long.parseLong(id));
		if(planners != null){
			planners.forEach(e->{
				e.setPlannerName(fullName);
				jobAssignmentService.autoUpdateJobInfo(e);
			});
		}
		// update full name assignee job
		List<JobAssignment> assignees = jobAssignmentService.findByAssigneeId(Long.parseLong(id));
		if(assignees != null){
			assignees.forEach(e->{
				e.setAssigneeName(fullName);
				jobAssignmentService.autoUpdateJobInfo(e);
			});
		}
		UserProfileDto profile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		List<String> listPermission = new ArrayList<>(iUserRepository.selectPermissionByEmail(profile.getEmail()));
		String permission = listPermission.stream().collect(Collectors.joining(CommonConstants.COMMA));
		if (permission == null) {
			permission = "";
		}
		profile.setPermission(permission);
		profile.addToSession(request.getSession(true));
        ModelAndView modelAndView = new ModelAndView(CONST_REDIRECT+PATH_USER_LISTING);
    	request.getSession().setAttribute(EDIT_USER_STATUS, CommonConstants.CONST_TRUE);
    	
    	return modelAndView;
    }

}
