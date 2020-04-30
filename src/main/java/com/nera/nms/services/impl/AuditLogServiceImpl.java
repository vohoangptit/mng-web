package com.nera.nms.services.impl;

import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private ISystemAuditLogRepository iSystemAuditLogRepository;

    @Override
    public SystemAuditLog createAuditLog(SystemAuditLog systemAuditLog) {
        return iSystemAuditLogRepository.save(systemAuditLog);
    }
}
