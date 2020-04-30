package com.nera.nms.services;

import com.nera.nms.models.SystemAuditLog;

public interface AuditLogService {
    SystemAuditLog createAuditLog(SystemAuditLog systemAuditLog);
}
