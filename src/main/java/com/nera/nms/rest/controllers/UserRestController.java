package com.nera.nms.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.UserConstants;
import com.nera.nms.dto.DeleteUserDto;
import com.nera.nms.dto.UpdateStatusDto;
import com.nera.nms.models.*;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserDepartmentRepository;
import com.nera.nms.repositories.IUserJobTitleRepository;
import com.nera.nms.services.*;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.ConcateGroupUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@RequestMapping("/nera/api")
public class UserRestController {
	Logger logger = LoggerFactory.getLogger(UserRestController.class);
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserService userService;
	
	@Autowired
    EmailService emailService;
	
	@Autowired
	GroupService groupService;
	
	@Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;
	
	@Autowired
	IUserDepartmentRepository iUserDepartmentRepository;
	
	@Autowired
	IUserJobTitleRepository iUserJobTitleRepository;
	
	@Autowired
	UserDepartmentService userDepartmentService;
	
	@Autowired
	UserJobTitleService userJobTitleService;

	private static final String UNBLOCKED = "unblocked";

	private static final String DELETE_ACTION = "deleted";

	@Value("${message.email.sent.error}")
    private String emailSentError;
	
	@Value("${message.unblock.success}")
    private String unblockSuccess;
	
	@Value("${message.update.status.success}")
    private String updateStatusSuccess;

	@Value("${message.error.occurred}")
    private String updateStatusError;
	
	@Value("${message.delete.status.success}")
    private String deleteSuccess;
	
	@PreAuthorize("hasAuthority('VIEW_USER_LISTING')")
	@PostMapping("/user/")
    public ObjectNode listAllUsers
    (@RequestParam(value = "pagination[perpage]", defaultValue = "10") String perPage,
     @RequestParam(value = "pagination[page]", defaultValue = "0") String page,
     @RequestParam(value = "pagination[pages]", defaultValue = "0") String pages,
     @RequestParam(value="sort[field]", defaultValue = "fullName") String sortBy,
     @RequestParam(value="sort[sort]", defaultValue = "ASC") String orderBy,
     @RequestParam(value="query[generalSearch]", defaultValue = "") String query,
     @RequestParam(value="filteredUserGroupIds", defaultValue = "") String filteredUserGroupIds,
     @RequestParam(value="filteredDepartmentIds", defaultValue = "") String filteredDepartmentIds,
     @RequestParam(value="filteredJobTitleIds", defaultValue = "") String filteredJobTitleIds,
     @RequestParam(value="filteredFromLastLogin", defaultValue = "") String filteredFromLastLogin,
     @RequestParam(value="filteredToLastLogin", defaultValue = "") String filteredToLastLogin) {
        
		long count;
        List <User> userList = new ArrayList <>();
        
        Map <String, Long> userGroupsMap = new TreeMap<>();
        Map <String, Long> departmentMap = new TreeMap<>();
        Map <String, Long> jobTitleMap = new TreeMap<>();

        Map<String, String> mapCondition = new ConcurrentHashMap<>();
		mapCondition.put("searchText", query);
		mapCondition.put("filteredUserGroupIds", filteredUserGroupIds);
		mapCondition.put("filteredDepartmentIds", filteredDepartmentIds);
		mapCondition.put("filteredJobTitleIds", filteredJobTitleIds);
		mapCondition.put("filteredFromLastLogin", filteredFromLastLogin);
		mapCondition.put("filteredToLastLogin", filteredToLastLogin);

     	count = userService.getUsers(page, perPage, sortBy, orderBy, userList, mapCondition);

     	final AtomicInteger index = new AtomicInteger();
        userList.forEach(user -> {
        	user.setFormattedLastLogin(BEDateUtils.formatDate(user.getLastLoginDateTime(), CommonConstants.DATE_TIME_FORMAT));
        	
        	int recordId = index.incrementAndGet() + ((Integer.parseInt(page) - 1) * Integer.parseInt(perPage));

			setDepartmentNameById(user);
			setJobNameById(user);

			if (user.getLoginCount() >= UserConstants.INVALID_LOGIN_MAX) {
        		user.setStatus(concatValueUserTagSpan(UserConstants.BLOCKED));
        		user.setRecordId(concatValueUserTagSpan(recordId));
        		user.setFullName(concatValueUserTagSpan(user.getFullName()));
        		user.setEmail(concatValueUserTagSpan(user.getEmail()));
        		user.setMobileNumber(concatValueUserTagSpan(user.getMobileNumber()));
        		user.setGroupNames(concatValueUserTagSpan(user.getGroupNames()));
        		user.setFormattedLastLogin(concatValueUserTagSpan(user.getFormattedLastLogin()));
        	} else {
        		user.setStatus(user.isActive() ? UserConstants.ACTIVE : UserConstants.INACTIVE);    
        		user.setRecordId(String.valueOf(recordId));
        	}
        });
        
        List <Group> linkedGroups = groupService.findLinkedUserGroups();
        linkedGroups.forEach(group -> userGroupsMap.put(group.getGroupName(), group.getId()));
        
        List <UserDepartment> linkedDepartment = userDepartmentService.findLinkedUserDeparment();
        linkedDepartment.forEach(department -> departmentMap.put(department.getDepartmentName(), department.getId()));
        
        List <UserJobTitle> linkedJobTitles = userJobTitleService.findLinkedUserJobTitle();
        linkedJobTitles.forEach(jobTitle -> jobTitleMap.put(jobTitle.getJobTitleName(), jobTitle.getId()));
        
        ObjectNode metaNode = mapper.createObjectNode();
        ObjectNode paramsNode = mapper.createObjectNode();
        paramsNode.put("total", count);
        paramsNode.put("page", page);
        paramsNode.put("pages", pages);
        paramsNode.put("perpage", perPage);
        
        metaNode.set("meta", paramsNode);
        metaNode.putPOJO("data", userList);
        metaNode.putPOJO("userGroups", userGroupsMap);
        metaNode.putPOJO("departments", departmentMap);
        metaNode.putPOJO("jobTitles", jobTitleMap);
        return metaNode;
    }

	private void setJobNameById(User user) {
		if (user.getJobTitleId() != null) {
			Optional<UserJobTitle> jobTitle = iUserJobTitleRepository.findById(user.getJobTitleId());
			if (jobTitle.isPresent() && user.getLoginCount() >= UserConstants.INVALID_LOGIN_MAX)
				user.setJobTitleName(concatValueUserTagSpan(jobTitle.get().getJobTitleName()));
			else jobTitle.ifPresent(userJobTitle -> user.setJobTitleName(userJobTitle.getJobTitleName()));
		}
	}

	private void setDepartmentNameById(User user) {
		if (user.getDepartmentId() != null) {
			Optional<UserDepartment> department = iUserDepartmentRepository.findById(user.getDepartmentId());
			if (department.isPresent() && user.getLoginCount() >= UserConstants.INVALID_LOGIN_MAX) {
				user.setDepartmentName(concatValueUserTagSpan(department.get().getDepartmentName()));

			}  else department.ifPresent(userDepartment -> user.setDepartmentName(userDepartment.getDepartmentName()));
		}
	}

	private String concatValueUserTagSpan(Object valueUser) {
		return "<span class=\"red\">" + valueUser + "</span>";
	}
	
	@PreAuthorize("hasAuthority('UPDATE_USERS')")
	@PutMapping("/unblockUser/")
    public ResponseEntity<String> unblockUser
    (@Valid @RequestBody UpdateStatusDto unblockUserDto,
    		HttpServletRequest request) {
		String [] arrId = unblockUserDto.getIds();

		List<User> userList = new ArrayList<>();
		for (String id : arrId) {
			User user = userService.findUserById(id);
			userList.add(user);
			String randomPassword = RandomStringUtils.randomAlphanumeric(8);
	        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	        String passwordEndCode = bCryptPasswordEncoder.encode(randomPassword);
	        //send mail
			if (!emailService.sendResetPasswordEmail(randomPassword, user.getEmail(), user.getFullName(), request)) {
				return new ResponseEntity<>(emailSentError, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			log.info(StringUtils.join("Sent an email to ", user.getEmail(), " successfully!"));
	
	        //update new password for user
	        user.setPassword(passwordEndCode);
	        userService.unblockUser(user);
	        
	        String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());
	        
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        String username = StringUtils.EMPTY;
	        if (principal instanceof UserDetails) {
	          username = ((UserDetails)principal).getUsername();
	        }
	        User loggedInUser = userService.findUserByEmail(username);
	        
	        SystemAuditLog systemAuditLog = new SystemAuditLog();
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
	        commonMethodComponent.updateUserLogData(systemAuditLog, 
	        		loggedInUser, 
	        		groupStr,
					getDetailsAudit(username, user.getEmail(), UNBLOCKED),
					"Unblock user",
	        		true);
	       
	        iSystemAuditLogRepository.save(systemAuditLog);
		}
		
        return new ResponseEntity<>(unblockSuccess, HttpStatus.OK);
    }

	/**
	 * updateStatusDto.getIds() .split("\\s*,\\s*");
	 */
	@PreAuthorize("hasAuthority('UPDATE_USERS')")
	@PutMapping("/updateStatus/")
    public ResponseEntity<String> updateStatus (@Valid @RequestBody UpdateStatusDto updateStatusDto) {
		String [] arrId = updateStatusDto.getIds();
		List <Long> idLong = new ArrayList<>();
		for (String i: arrId) {
			idLong.add(Long.parseLong(i));
			
			User user = userService.findUserById(i);
	        String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        String username = StringUtils.EMPTY;
	        if (principal instanceof UserDetails) {
	          username = ((UserDetails)principal).getUsername();
	        }
	        User loggedInUser = userService.findUserByEmail(username);
	        
	        String details = "User " + username + " has updated " + user.getEmail() + " with status of Active: " + updateStatusDto.getActiveOption() + CommonConstants.DOTS;
	        SystemAuditLog systemAuditLog = new SystemAuditLog();
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
	        commonMethodComponent.updateUserLogData(systemAuditLog, 
	        		loggedInUser, 
	        		groupStr, 
	        		details,
					"Update Status user",
	        		true);
	       
	        iSystemAuditLogRepository.save(systemAuditLog);
		}
        userService.updateStatus(idLong, updateStatusDto.getActiveOption());
        
        return new ResponseEntity<>(updateStatusSuccess, HttpStatus.OK);
    }
	
	@PreAuthorize("hasAuthority('DELETE_USERS')")
	@DeleteMapping("/deleteUsers/")
    public ResponseEntity<String> deleteUsers
    (@Valid @RequestBody DeleteUserDto updateStatusDto) {
		String[] idss = updateStatusDto.getIds();
		List <Long> idLong = new ArrayList<>();
		for (String i: idss) {
			idLong.add(Long.parseLong(i));
			
			User user = userService.findUserById(i);
	        String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        String username = StringUtils.EMPTY;
	        if (principal instanceof UserDetails) {
	          username = ((UserDetails)principal).getUsername();
	        }
	        User loggedInUser = userService.findUserByEmail(username);

	        SystemAuditLog systemAuditLog = new SystemAuditLog();
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
	        commonMethodComponent.updateUserLogData(systemAuditLog, 
	        		loggedInUser, 
	        		groupStr,
					getDetailsAudit(username, user.getEmail(), DELETE_ACTION),
					"Delete user",
	        		true);
	       
	        iSystemAuditLogRepository.save(systemAuditLog);
		}
        userService.deleteUsers(idLong);
        
        return new ResponseEntity<>(deleteSuccess, HttpStatus.OK);
    }
	
	@PreAuthorize("hasAuthority('UPDATE_USERS')")
	@GetMapping(value = "checkEmail")
    public String checkEmail
    (@RequestParam(value = "emailEntered") String emailEntered,
    		@RequestParam(value = "isEdit") String isEdit,
    		@RequestParam(value = "userId", defaultValue = "0") String userId) {
		
        User user = userService.findUserByEmail(emailEntered);
		if (user != null) {
			if (Boolean.parseBoolean(isEdit) && user.getId() != Long.parseLong(userId)) {
				return "true";
			}
			if (!Boolean.parseBoolean(isEdit) && emailEntered.equalsIgnoreCase(user.getEmail())) {
				return "true";
			}
		}
        return "false";
	}
	
	@PreAuthorize("hasAuthority('UPDATE_USERS')")
	@PostMapping("/resetPassword")
	public boolean resetPassword(@RequestParam String id,
			HttpServletRequest request) {
		User user = userService.findUserById(id);
		String randomPassword = RandomStringUtils.randomAlphanumeric(8);
		try {
            emailService.sendResetPasswordEmail(randomPassword, user.getEmail(), user.getFullName(), request);
		} catch (Exception e) {
			logger.error("UserRestController.resetPassword ", e);
			return false;
		}
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String passwordEndCode = bCryptPasswordEncoder.encode(randomPassword);
		user.setPassword(passwordEndCode);
		userService.save(user);

		String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = StringUtils.EMPTY;
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        }
        User loggedInUser = userService.findUserByEmail(username);
        
        SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.updateUserLogData(systemAuditLog, 
        		loggedInUser, 
        		groupStr,
				getDetailsAudit(username, user.getEmail(), "reset password"),
				"Reset password",
        		true);
       
        iSystemAuditLogRepository.save(systemAuditLog);
		
		return true;
		
	}
	
	@PostMapping("/unblockOneUser")
    public String unblockOneUser
    (@RequestParam(value = "id") long id,
    		HttpServletRequest request) {
		
		User user = userService.findUserById(String.valueOf(id));
		String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEndCode = bCryptPasswordEncoder.encode(randomPassword);
        //send mail
        try {
            emailService.sendResetPasswordEmail(randomPassword, user.getEmail(), user.getFullName(), request);
            log.info(StringUtils.join("Sent an email to ", user.getEmail(), " successfully!"));
        } catch (Exception e) {
			logger.error("UserRestController.unblockOneUser ", e);
            return "false";
        }

        //update new password for user
        user.setPassword(passwordEndCode);
        userService.unblockUser(user);
        
        String groupStr = ConcateGroupUtil.concateGroupNames(user.getGroups());
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = StringUtils.EMPTY;
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        }
        User loggedInUser = userService.findUserByEmail(username);
        
        SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.updateUserLogData(systemAuditLog, 
        		loggedInUser, 
        		groupStr,
				getDetailsAudit(username, user.getEmail(), UNBLOCKED),
				"Unblock user",
        		true);
        iSystemAuditLogRepository.save(systemAuditLog);
        return "true";
    }

    private String getDetailsAudit(String username, String emailUser, String action) {
		String message = CommonConstants.DOTS;
		if(!StringUtils.equals(DELETE_ACTION, action)) {
			message = " and email with password is sent.";
		}
		return "User " + username + " has "+ action + " " + emailUser + message;
	}
}
