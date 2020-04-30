/**
 * 
 */
package com.nera.nms.components;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nera.nms.dto.SystemSettingDto;

/**
 * @author Martin Do
 *
 */
@Component
public class ConfigMailComponent {

    @Autowired
    ApplicationContext context;

    @Autowired
    MailComponent mailComponent;

    public void reConfigEmailSender(SystemSettingDto systemSettingDto) {

        mailComponent.setHost(systemSettingDto.getSmtpHost());

        mailComponent.setPort(systemSettingDto.getSmtpPort());

        if(StringUtils.isNotBlank(systemSettingDto.getUsername())) {
            mailComponent.setUsername(systemSettingDto.getUsername());
        } else {
            mailComponent.setUsername(systemSettingDto.getEmail());
        }

        mailComponent.setPw(systemSettingDto.getPassword());
    }

}
