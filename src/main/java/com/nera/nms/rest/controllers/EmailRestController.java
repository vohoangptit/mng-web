package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.ChangePasswordDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.User;
import com.nera.nms.services.EmailService;
import com.nera.nms.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/nera/api/email")
@Slf4j
@PropertySource("classpath:messages.properties")
public class EmailRestController {

    @Value("${message.email.invalid}")
    private String emailInvaild;

    @Value("${message.email.no.send}")
    private String emailNoSend;

    @Value("${message.email.not.exist}")
    private String emailNotExist;

    @Value("${message.email.locked}")
    private String emailLocked;

    @Value("${message.user.password.wrong}")
    private String passwordWrong;
    
    @Value("${message.email.change.password.successfully}")
    private String changePasswordSuccess;

    @Autowired
    UserService userService;
    
    @Autowired
    EmailService emailService;

    @GetMapping(path = "/forgot/password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email,
    		HttpServletRequest request) {
        if(StringUtils.isBlank(email)) {
            return new ResponseEntity<>(emailInvaild, HttpStatus.BAD_REQUEST);
        }

        StringUtils.replace(email, CommonConstants.COMMA, StringUtils.EMPTY);

        //find email in the system
        User user = userService.findUserByEmail(email);

        if(user == null) {
            return new ResponseEntity<>(emailNotExist, HttpStatus.BAD_REQUEST);
        }
        
        if(!user.isActive()) {
            return new ResponseEntity<>(emailLocked, HttpStatus.BAD_REQUEST);
        }
        
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEndcode = bCryptPasswordEncoder.encode(randomPassword);
        //send mail
        if(emailService.sendForgetPasswordEmail(randomPassword, email, user.getFullName(), request)) {
            log.info(StringUtils.join("send and email to ", email, "successfully!"));
            //update new password for user
            user.setPassword(passwordEndcode);
            userService.save(user);
            return new ResponseEntity<>(changePasswordSuccess, HttpStatus.OK);
        }
        return new ResponseEntity<>(emailNoSend, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/password/change")
    public ResponseEntity<String> changPassword(@RequestBody ChangePasswordDTO passwordDTO, HttpServletRequest request) {
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        if(StringUtils.isBlank(data.getEmail())) {
            return new ResponseEntity<>(emailInvaild, HttpStatus.BAD_REQUEST);
        }

        //find email in the system
        User user = userService.findUserByEmail(data.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = bCryptPasswordEncoder.encode(passwordDTO.getNewPassword());
        user.setPassword(passwordEncode);
        userService.save(user);
        //update password
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/first-login")
    public ResponseEntity<String> changePasswordFirstLogin(@RequestBody ChangePasswordDTO passwordDTO, HttpServletRequest request
                                               ) {
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);

        //find email in the system
        User user = userService.findUserByEmail(data.getEmail());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = bCryptPasswordEncoder.encode(passwordDTO.getNewPassword());
        user.setFirstLogin(false);
        user.setPassword(passwordEncode);
        //update password
        userService.save(user);
        return new ResponseEntity<>("done", HttpStatus.OK);
    }

    @PostMapping("/password/check")
    public ResponseEntity<String> checkPasswordBeforeChange(@RequestBody ChangePasswordDTO passwordDTO, HttpServletRequest request
    ) {
        String result = "failed";
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);

        //find email in the system
        User user = userService.findUserByEmail(data.getEmail());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            result = "ok";
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
}
