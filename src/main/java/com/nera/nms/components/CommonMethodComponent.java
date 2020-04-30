
package com.nera.nms.components;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.UserConstants;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommonMethodComponent {

    /**
     * @param userGroup
     * @param userEntity
     */
    public void auditGroupNameFormat(StringBuilder userGroup, User userEntity) {
        for(int i=0; i<userEntity.getGroups().size(); i++) {
            if(i > 0) {
                userGroup.append(CommonConstants.COMMA);
            }
            userGroup.append(userEntity.getGroups().get(i).getGroupName());
        }
    }

    /**
     * @param systemAuditLog
     * @param user
     * @param userGroup
     * @param isLogin
     * @param isAuthenticaion
     */
    public void initSystemAuditLogData(SystemAuditLog systemAuditLog, User user, String userGroup, boolean isLogin, boolean isAuthenticaion) {
        systemAuditLog.setUserName(user.getFullName());
        systemAuditLog.setUserGroupName(userGroup);
        systemAuditLog.setDescription(isLogin ? UserConstants.USER_LOGIN_DESCRIPTION
                                                        : UserConstants.USER_LOGOUT_DESCRIPTION);
        systemAuditLog.setActionDate(new Date());
        systemAuditLog.setStatus(isAuthenticaion ? CommonConstants.SYSTEM_SUCCESS : CommonConstants.SYSTEM_FAILED);
        systemAuditLog.setDetails(StringUtils.join(UserConstants.USER_LOGIN_DESCRIPTION
                                                        , ": "
                                                        , user.getFullName()));
    }
    
    /**
     * @param systemAuditLog
     * @param user
     * @param userGroup
     * @param details
     * @param desc
     * @param isSuccess
     */
    public void updateUserLogData(SystemAuditLog systemAuditLog, User user, String userGroup, String details, String desc, boolean isSuccess) {
        systemAuditLog.setUserName(user.getFullName());
        systemAuditLog.setUserGroupName(userGroup);
        systemAuditLog.setDescription(desc);
        systemAuditLog.setActionDate(new Date());
        systemAuditLog.setStatus(isSuccess ? CommonConstants.SYSTEM_SUCCESS : CommonConstants.SYSTEM_FAILED);
        systemAuditLog.setDetails(details);
    }
    
    /**
     * @param systemAuditLog
     * @param userName
     * @param userGroup
     * @param details
     * @param desc
     * @param isSuccess
     */
    public void addAuditLogData(SystemAuditLog systemAuditLog, String userName, String userGroup, String details, String desc, boolean isSuccess) {
        systemAuditLog.setUserName(userName);
        systemAuditLog.setUserGroupName(userGroup);
        systemAuditLog.setDescription(desc);
        systemAuditLog.setActionDate(new Date());
        systemAuditLog.setStatus(isSuccess ? CommonConstants.SYSTEM_SUCCESS : CommonConstants.SYSTEM_FAILED);
        systemAuditLog.setDetails(details);
    }
}
