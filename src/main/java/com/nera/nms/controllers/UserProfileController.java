package com.nera.nms.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.User;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.services.UserService;
import com.nera.nms.utils.UrlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

@Controller
@Slf4j
public class UserProfileController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;
	
	@Autowired
	IUserRepository iUserRepository;
	
	@PostMapping("/uploadImage")
	public String addUser (@RequestParam(value="file") MultipartFile file,
			HttpServletRequest request) {
		String url = "redirect:" + UrlBuilder.getServerUrl(request) + CommonConstants.PATH_DELIMITER;
		if(file.getSize()/1000 > 2048){
			return url + "profile?uploadSuccess=false";
		}
		String mime;
		mime = file.getContentType();
		log.debug("fileType=" + mime);
		boolean isValidFile = false;
		switch (mime) {
			case "image/jpeg":
			case "image/jpg":
			case "image/png":
				isValidFile = true;
				break;
			default:
		}
		String fileString = "";
		String urls;
		if (isValidFile) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        String username = "";
	        if (principal instanceof UserDetails) {
	          username = ((UserDetails)principal).getUsername();
	        }
	        
	        User loggedInUser = userService.findUserByEmail(username);
	        
	        try {
	        	fileString = Base64.getEncoder().encodeToString(file.getBytes());
				loggedInUser.setImage(fileString);
			} catch (IOException e) {
				logger.error("Exception: UserProfileController.addUser ", e);
			}
	        iUserRepository.save(loggedInUser);
	        
	        UserProfileDto userProfile = (UserProfileDto) request.getSession(false).getAttribute(CommonConstants.USER_PROFILE);
			userProfile.setImageUrl(fileString);
			userProfile.addToSession(request.getSession(false));
		} else {
			urls =url + "uploadSuccess=false";
			return urls;
		}
		urls = url + "profile?uploadSuccess=true";
		return urls;
	}
}
