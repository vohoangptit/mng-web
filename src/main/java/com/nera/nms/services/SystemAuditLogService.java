/**
 * 
 */
package com.nera.nms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nera.nms.components.PageableComponent;
import com.nera.nms.dto.SystemAuditLogDto;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.utils.BeanConvertUtils;

/**
 * @author Martin Do
 *
 */
@Service
public class SystemAuditLogService {

    @Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;

    @Autowired
    PageableComponent pageableComponent;
    /**
     * @return count all log
     */
    public long countSystemAuditLog() {
        return iSystemAuditLogRepository.count();
    }
    
    /**
     * get all system audit log
     * @param page
     * @param pageSize
     * @param sortBy
     * @param orderBy
     * @param userList
     */
    public List<SystemAuditLogDto> getSystemAuditLog(Pageable pageable) {
        Page<SystemAuditLog> systemAuditLog = iSystemAuditLogRepository.findAll(pageable);

        //copy list
        return BeanConvertUtils.copyList(systemAuditLog.getContent(), SystemAuditLogDto.class);
    }
    
    /**
     * return dto list with search condition
     * @param pageable
     * @param queryString
     * @param userList
     */
    public List<SystemAuditLogDto> getSystemAuditLogBySearch(String queryString, Pageable pageable) {
        Page<SystemAuditLog> systemAuditLog = iSystemAuditLogRepository.findLikeSearchString(queryString, pageable);

        //copy list
        return BeanConvertUtils.copyList(systemAuditLog.getContent(), SystemAuditLogDto.class);
    }
    
    public long getSystemAuditLogBySearchCount(String queryString) {
        return iSystemAuditLogRepository.findLikeSearchStringCount(queryString).size();
    }
}
