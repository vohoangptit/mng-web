package com.nera.nms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nera.nms.models.SystemSetting;

/**
 * @author Martin Do
 *
 */
public interface ISystemSettingReposytory extends JpaRepository<SystemSetting, Long> {

    SystemSetting findByEmail(String email);
}
