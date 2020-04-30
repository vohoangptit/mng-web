/**
 * 
 */
package com.nera.nms.components;

import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import com.nera.nms.constants.MailConstants;
import com.nera.nms.dto.SystemSettingDto;
import com.nera.nms.models.SystemSetting;
import com.nera.nms.repositories.ISystemSettingReposytory;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Component
@Data
public class MailComponent {

    @Autowired
    ISystemSettingReposytory iSystemSettingReposytory;
    private String host = MailConstants.HOST_MAIL;
    private int port = 587;
    private String username = MailConstants.USERNAME_MAIL;
    private String pw = MailConstants.PW_MAIL;
    private String transportProtocol = MailConstants.MAIL_TRANSPORT_PROTOCOL;
    private String smtpAuth = MailConstants.MAIL_SMTP_AUTH;
    private String smtpStartTlsEnable = MailConstants.MAIL_SMTP_STARTTLS_ENABLE;
    private String mailDebug = MailConstants.MAIL_DEBUG;
    private boolean smtpAuthStatus = true;

    /**
     * init java mail sender
     * @return
     */
    public JavaMailSender getJavaMailSender() {

        //get system setting
        List<SystemSetting> liSystemSetting = iSystemSettingReposytory.findAll();
        if(CollectionUtils.isNotEmpty(liSystemSetting)) {
            SystemSetting initSystemSetting = liSystemSetting.get(0);
            host = initSystemSetting.getSmtpHost();
            port = Integer.parseInt(initSystemSetting.getSmtpPort());
            username = initSystemSetting.getUsername();
            pw = initSystemSetting.getPassword();
            smtpAuthStatus = initSystemSetting.isSbSmtpSecurity();
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(pw);

        Properties props = mailSender.getJavaMailProperties();
        props.put(transportProtocol, MailConstants.SMTP);
        props.put(smtpAuth, smtpAuthStatus);
        props.put(smtpStartTlsEnable, MailConstants.TRUE);
        props.put(mailDebug, MailConstants.TRUE);

        return mailSender;
    }
    
    public SystemSetting getPropertiesMailSender() {
        //get system setting
        List<SystemSetting> systemSetting = iSystemSettingReposytory.findAll();
        return systemSetting.get(0);
    }
    
    /**
     * init java mail sender
     * @return
     */
    public JavaMailSender getJavaMailSender(SystemSettingDto systemSettingDto) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(systemSettingDto.getSmtpHost());
        mailSender.setPort(systemSettingDto.getSmtpPort());

        mailSender.setUsername(systemSettingDto.getUsername());
        mailSender.setPassword(systemSettingDto.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put(transportProtocol, MailConstants.SMTP);
        props.put(smtpAuth, systemSettingDto.isSbSmtpSecurity());
        props.put(smtpStartTlsEnable, MailConstants.TRUE);
        props.put(mailDebug, MailConstants.TRUE);

        return mailSender;
    }
}
