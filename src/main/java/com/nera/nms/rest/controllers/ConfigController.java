package com.nera.nms.rest.controllers;

import com.nera.nms.components.ConfigMailComponent;
import com.nera.nms.dto.SystemSettingDto;
import com.nera.nms.models.User;
import com.nera.nms.services.EmailService;
import com.nera.nms.services.SystemSettingService;
import com.nera.nms.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nera/api/config")
@Slf4j
@PropertySource("classpath:messages.properties")
public class ConfigController {

    @Value("${message.system.general.config.invalid}")
    private String configInvalid;
    
    @Value("${message.system.general.config.valid}")
    private String configVaild;

    @Value("${message.email.no.send}")
    private String emailNoSend;
    
    @Value("${message.system.general.save.failure}")
    private String saveConfigFailure;
    
    @Value("${message.system.general.save.success}")
    private String saveConfigSuccess;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    ConfigMailComponent configMailComponent;
    
    @Autowired
    SystemSettingService systemSettingService;

    @PreAuthorize("hasAuthority('UPDATE_GENERAL_SETTING')")
    @PostMapping(path = "/general")
    public ResponseEntity<String> sendTestEmail(SystemSettingDto systemSettingDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = StringUtils.EMPTY;
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        }

        User user = userService.findUserByEmail(username);

        try {
            emailService.testSendEmail2(user.getEmail(), systemSettingDto);
        } catch (MailException e) {
            log.error(configInvalid);
            return new ResponseEntity<>(configInvalid, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(emailNoSend);
            return new ResponseEntity<>(emailNoSend, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(configVaild, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_GENERAL_SETTING')")
    @PostMapping(path = "/general/save")
    public ResponseEntity<String> saveEmailConfiguration(SystemSettingDto systemSettingDto) {
        try {
            systemSettingService.save(systemSettingDto);
        } catch (Exception e) {
            log.error(saveConfigFailure);
            return new ResponseEntity<>(saveConfigFailure, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(saveConfigSuccess, HttpStatus.OK);
    }
}
