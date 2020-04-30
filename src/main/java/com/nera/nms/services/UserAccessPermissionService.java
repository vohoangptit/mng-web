package com.nera.nms.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nera.nms.constants.RolesConstants;
import com.nera.nms.models.Group;
import com.nera.nms.models.User;
import com.nera.nms.repositories.IGroupRepository;
import com.nera.nms.repositories.IUserAccessPermissionRepository;
import com.nera.nms.repositories.IUserRepository;

@Service
public class UserAccessPermissionService {

	@Autowired
	private IUserAccessPermissionRepository iUserAccessPermissionRepository;

	@Autowired
	IGroupRepository iGroupRepository;
	@Autowired
	private UserService userService;
	@Autowired
	IUserRepository iUserRepository;
	@Autowired
	GroupService groupService;

	public void save(Map<String, List<String>> body) {
		for (Map.Entry<String, List<String>> object : body.entrySet()) {
			Group entity = iGroupRepository.findOneByGroupName(object.getKey());
			if (entity != null) {
				iUserAccessPermissionRepository.deleteRole(entity.getId());
				object.getValue().forEach(x -> iUserAccessPermissionRepository.reCreateRole(entity.getId(), x));
			}
		}
	}

	@PostConstruct
	public void doPermission() {
//		insert user group for supper admin
		List<String> groupList = groupService.getPermissionByGroupName(RolesConstants.ROLE_SYSTEM);
		Group group = new Group();
		if(CollectionUtils.isEmpty(groupList))
		{
			group.setActive(true);
			group.setGroupName(RolesConstants.ROLE_SYSTEM);
			group.setDescription("The system user has full permission for nera system");
			group = iGroupRepository.save(group);
		} else {
			group = iGroupRepository.findOneByGroupName(RolesConstants.ROLE_SYSTEM);
		}
		
//		insert user
		User userisexist = userService.findUserByEmail("sys@gmail.com");
		if(userisexist == null)
		{
			User user = new User();
			user.setEmail("sys@gmail.com");
			user.setFullName("System Admin");
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			String passwordEncode = bCryptPasswordEncoder.encode("123456");
			user.setPassword(passwordEncode);
			user.setGroupNames(RolesConstants.ROLE_SYSTEM);
			user.setImage(StringUtils.EMPTY);
			user.setActive(true);
			user.setDepartmentId(0L);
			user.setJobTitleId(0L);
			userisexist = userService.saveUser(user);
			iUserAccessPermissionRepository.updateUserGrUser(group.getId(), userisexist.getId());
		}

//		insert full access control for administrator
		List<String> items = new ArrayList<>();
		RolesConstants.getPERMISSION().forEach((k, v) -> v.forEach((kv, vv) -> items.add(vv.replace(" ", "_"))));
		items.add("ACTUATOR");
		Map<String, List<String>> mapPermission = new LinkedHashMap<>();
		mapPermission.put(RolesConstants.ROLE_SYSTEM, items);
		save(mapPermission);
	}
}