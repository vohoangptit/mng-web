package com.nera.nms.repositories;

import com.nera.nms.models.SystemAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Martin Do
 *
 */
public interface ISystemAuditLogRepository extends JpaRepository<SystemAuditLog, Long> {

    @Query("Select s from SystemAuditLog s where s.searchString like %:searchString%")
    Page<SystemAuditLog> findLikeSearchString(@Param("searchString") String searchString, Pageable pageable);
    
    long count();

    @Query("Select s from SystemAuditLog s where s.searchString like %:searchString%")
    List<SystemAuditLog> findLikeSearchStringCount(@Param("searchString") String searchString);
    @Query("Select s from SystemAuditLog s where userName IS NOT NULL AND DATE_FORMAT(s.actionDate, '%Y-%m-%d %H:%i:%s') BETWEEN DATE_FORMAT(:start, '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(:end, '%Y-%m-%d %H:%i:%s')")
    List<SystemAuditLog> findActionDateBetween(@Param("start") Timestamp start, @Param("end") Timestamp end);
}