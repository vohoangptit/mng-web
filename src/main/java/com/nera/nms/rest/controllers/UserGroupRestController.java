package com.nera.nms.rest.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.GroupDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.Group;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.IGroupRepository;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.GroupService;
import com.nera.nms.utils.PageableUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("nera/user-group/api")
@AllArgsConstructor
public class UserGroupRestController {

	@Autowired
	private IGroupRepository iGroupRepository;

	@Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;
	
	@Autowired
	private GroupService groupService;

	@PreAuthorize("hasAuthority('VIEW_USER_GROUP_LISTING')")
	@GetMapping("/getAll")
	public ResponseEntity<PageableDTO> getList(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") int page,
			@RequestParam(value = "sort[field]", defaultValue = "groupName") String sortBy,
			@RequestParam(value = "sort[sort]", defaultValue = "ASC") String orderBy,
			@RequestParam(value="query[generalSearch]", defaultValue = "") String query) {
		Page<GroupDTO> data;
		String sortCol;
		switch(sortBy) {
		case "description":
			sortCol = "description";
			break;
		case "active":
			sortCol = "isActive";
			break;
		default:	
		case "groupName":
			sortCol = "groupName";
			break;
		}
		
		Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortCol, orderBy);
		if (query.equalsIgnoreCase("active")) {
			query = "% " + query + "%";
			data = groupService.findActive(pageable, query);
		} else {
			data = groupService.findAll(pageable, query);
		}
		PageableDTO result = PageableUtil.pageableMapperJson(data, pageable);
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasAuthority('DELETE_USER_GROUP')")
	@PutMapping("/delete-by-id")
	public boolean deleteById(@RequestParam(value = "id") long id, HttpServletRequest request) {
		iGroupRepository.updateActiveGroup(true, id);
		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		UserProfileDto data  = (UserProfileDto)request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		String details = "UserGroup Id : "+ id + " has been deleted by " + data.getEmail();
		String desc = "Delete UserGroup";
        commonMethodComponent.addAuditLogData(systemAuditLog, 
        		data.getFullName(), 
        		data.getGroups(), 
        		details, 
        		desc, 
        		true);
        iSystemAuditLogRepository.save(systemAuditLog);
		return true;
	}

	@PreAuthorize("hasAuthority('UPDATE_USER_GROUP') or hasAuthority('CREATE_USER_GROUP')")
	@PostMapping("/add-or-update")
	public Map<String, Object> addOrUpdateGroup(@RequestBody GroupDTO body, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		result.put(CommonConstants.SUCCESS, "false");
		// define audit parameter
		String details = "";
		String desc = "";
		UserProfileDto data = (UserProfileDto)request.getSession().getAttribute(CommonConstants.USER_PROFILE);

		if (body.getGroupName().equals("") || body.getGroupName() == null) {
			result.put(CommonConstants.STATUS, 400);
			result.put(CommonConstants.ERROR_KEY, "missingDataGroupName");
			result.put(CommonConstants.MESSAGE, "data GroupName is required");
		} else {
			Group entity;
			if (body.getId() == null) {
				entity = iGroupRepository.findOneByGroupName(body.getGroupName());
				if (entity == null) {
					body.setCreatedBy(data.getFullName());
					body.setCreatedDate(new Date());
					groupService.addGroup(body);
					result.put(CommonConstants.STATUS, 200);
					result.put(CommonConstants.SUCCESS, "true");
					result.put(CommonConstants.MESSAGE, "Save successfully");
					details = "UserGroup "+ body.getGroupName() + " has been created by " + data.getEmail();
					desc = "Create UserGroup";
				} else {
					result.put(CommonConstants.ERROR_KEY, "groupNameHasExists");
					result.put(CommonConstants.MESSAGE, "GroupName has exist");
					result.put(CommonConstants.STATUS, 400);
				}
			} else {
				entity = iGroupRepository.findOneById(body.getId());
				if (entity.getId().equals(body.getId())) {
					entity.setActive(body.isActive());
					entity.setGroupName(body.getGroupName());
					entity.setDescription(body.getDescription());
					entity.setModifiedBy(data.getFullName());
					entity.setModifiedDate(new Date());
					iGroupRepository.save(entity);
					result.put(CommonConstants.STATUS, 200);
					result.put(CommonConstants.SUCCESS, "true");
					result.put(CommonConstants.MESSAGE, "Update successfully");
					details = "UserGroup "+ body.getGroupName() + " has been updated by " + data.getEmail();
					desc = "Update UserGroup";
				} else {
					result.put(CommonConstants.ERROR_KEY, "idNotExist");
					result.put(CommonConstants.MESSAGE, "Id not right");
					result.put(CommonConstants.STATUS, 400);
				}
			}
		}
		if((int)result.get(CommonConstants.STATUS) == 200) {
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
		
		return result;
	}
	
	@PreAuthorize("hasAuthority('DELETE_USER_GROUP')")
	@GetMapping("/checkUserGroup")
	public int checkUserGroup(@RequestParam(value = "groupId") long groupId) {
		return iGroupRepository.checkActiveUserGroupUsers(groupId);
	}
}
