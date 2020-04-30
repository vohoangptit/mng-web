/**
 * 
 */
package com.nera.nms.configures;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.components.LoggedUserComponent;
import com.nera.nms.constants.UserConstants;
import com.nera.nms.dto.ActiveUserStore;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Martin Do
 *
 */
@Component
@Slf4j
public class LogoutStatusHandler implements LogoutHandler {

    
    private IUserRepository iUserRepository;
    private ISystemAuditLogRepository iSystemAuditLogRepository;
    private ActiveUserStore activeUserStore;
    /**
     * 
     */
    public LogoutStatusHandler(IUserRepository iUserRepository, ISystemAuditLogRepository iSystemAuditLogRepository, ActiveUserStore activeUserStore) {
        this.iUserRepository = iUserRepository;
        this.iSystemAuditLogRepository = iSystemAuditLogRepository;
        this.activeUserStore = activeUserStore;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
      //log user
        HttpSession session = request.getSession(false);
        if (session != null) {
            LoggedUserComponent user = new LoggedUserComponent(auth.getName(), activeUserStore);
            log.info(StringUtils.join("user logout: ",user.getUsername()));
            
            //save log to DB
            User userEntity = iUserRepository.selectUserByEmail(user.getUsername());
            CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
            StringBuilder userGroup = new StringBuilder();
            commonMethodComponent.auditGroupNameFormat(userGroup, userEntity);

            SystemAuditLog systemAuditLog = new SystemAuditLog();
            commonMethodComponent.initSystemAuditLogData(systemAuditLog
                                                            , userEntity
                                                            , userGroup.toString()
                                                            , !UserConstants.IS_LOGGIN
                                                            , UserConstants.IS_AUTHENTICAION);
            iSystemAuditLogRepository.save(systemAuditLog);
        }
        
        
    }


}
