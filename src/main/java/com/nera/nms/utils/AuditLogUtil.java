package com.nera.nms.utils;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.models.SystemAuditLog;
import org.apache.commons.lang3.StringUtils;

public final class AuditLogUtil {

    public static SystemAuditLog getSystemAuditLog(String fullName, String groups, String details, String description) {
        SystemAuditLog systemAuditLog = new SystemAuditLog();
        CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
        commonMethodComponent.addAuditLogData(systemAuditLog, fullName, groups, details, description,true);

        return systemAuditLog;
    }

    public static String getDetailsAudit(String nameJob, Long idJob, String emailUser, String action) {
        if(StringUtils.isNotBlank(nameJob)) {
            return "Job: " + nameJob + "(Job Id: " + idJob + ") has been "+ action + " by " + emailUser;
        }
        return "Job Id: " + idJob + " has been " + action + " by " + emailUser;
    }

    public static String getDetailsAudit(String nameJob, String emailUser, String action) {
        return "Job: " + nameJob + " has been "+ action + " by " + emailUser;
    }

    private AuditLogUtil(){}
}
