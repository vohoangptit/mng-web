package com.nera.nms.controllers;

import com.nera.nms.components.MenuComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.repositories.PlaybookRepository;
import com.nera.nms.utils.UserConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String UPLOAD_FILE_ERROR =  "uploadFileError";

	@Autowired
	private IUserRepository iUserRepository;
	
	@Autowired
    PlaybookRepository playbookRepository; 

	@Autowired
    MenuComponent menuComponent;
	
	@GetMapping(value = "/")
	public ModelAndView root(Model model, HttpServletRequest request) {
		try{
			if (isRememberMeAuthenticated() && request.getSession(true).getAttribute(CommonConstants.USER_PROFILE) == null) {
				int number = 0;
				number = playbookRepository.countByStatusNew();
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				// user add list
				logger.info("auth name : {}", authentication.getName());
				Object optionUser = iUserRepository.findUserDetail(authentication.getName());
				List<String> listPermission = new ArrayList<>(iUserRepository.selectPermissionByEmail(authentication.getName()));
				String permission = listPermission.stream().collect(Collectors.joining(CommonConstants.COMMA));
				UserProfileDto userProfileDto = UserConvertUtil.convertUserOptionToDto(optionUser);
				if (permission == null) {
					permission = "";
				}
				userProfileDto.setPermission(permission);
				userProfileDto.addToSession(request.getSession(true));
				request.getSession(true).setAttribute("playbookApproveMenu", "Playbook Approved ("+ number +")");
			}
			if (request.getParameter(UPLOAD_FILE_ERROR) != null && request.getParameter(UPLOAD_FILE_ERROR).equalsIgnoreCase("true")) {
				ModelAndView modelAndView = new ModelAndView("index");
				modelAndView.addObject(UPLOAD_FILE_ERROR, true);
						
				return modelAndView;
			}
		} catch(Exception e){
			logger.error("WebController.root : ", e);
		}
		return new ModelAndView("index");
	}

	private boolean isRememberMeAuthenticated() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}

		return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
	}

	@GetMapping(value = "/login")
	public String index(Model model) {
		return "authentication/login";
	}

	@GetMapping(value = "/password/change")
	public String wc(Model model) {

		return "change-pw-first";
	}
}
