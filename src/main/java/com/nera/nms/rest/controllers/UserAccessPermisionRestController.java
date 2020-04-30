package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.services.UserAccessPermissionService;
import com.nera.nms.utils.UserConvertUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("nera/access-permission")
@AllArgsConstructor
public class UserAccessPermisionRestController {

	@Autowired
	private UserAccessPermissionService userAccessPermissionService;

	@Autowired
	private IUserRepository iUserRepository;

	@PreAuthorize("hasAuthority('UPDATE_ACCESS_CONTROL')")
	@PostMapping("/update")
	public Map<String, Object> addOrUpdateGroup(@RequestBody Map<String, List<String>> body, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		result.put(CommonConstants.SUCCESS, "false");
		try{
			userAccessPermissionService.save(body);
			result.put(CommonConstants.SUCCESS, "true");
			result.put(CommonConstants.STATUS, 200);
			result.put(CommonConstants.MESSAGE, "Save successfully");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object optionUser = iUserRepository.findUserDetail(authentication.getName());
			List<String> listPermission = new ArrayList<>(iUserRepository.selectPermissionByEmail(authentication.getName()));
			String permission = listPermission.stream().collect(Collectors.joining(CommonConstants.COMMA));
			UserProfileDto userProfileDto = UserConvertUtil.convertUserOptionToDto(optionUser);
			if (permission == null) {
				permission = "";
			}
			userProfileDto.setPermission(permission);
			userProfileDto.addToSession(request.getSession(true));
		} catch(Exception e){
			result.put(CommonConstants.STATUS, 500);
			result.put(CommonConstants.ERROR_KEY, "InternalServer");
			result.put(CommonConstants.MESSAGE, "Server has problem");
		}
		return result;
	}
}
