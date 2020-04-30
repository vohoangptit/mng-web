/**
 * 
 */
package com.nera.nms.services;

import com.nera.nms.dto.SystemSettingDto;
import com.nera.nms.models.SystemSetting;
import com.nera.nms.repositories.ISystemSettingReposytory;
import com.nera.nms.utils.BeanConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Martin Do
 *
 */
@Service
public class SystemSettingService {

    @Autowired
    ISystemSettingReposytory iSystemSettingReposytory;
    
    /**
     * @param configGeneralDto
     * @return
     */
    public void save(SystemSettingDto systemSettingDto) {
        List<SystemSetting> liSystemSetting = iSystemSettingReposytory.findAll();
        
        SystemSetting systemSettingEntity = new SystemSetting();
        if (CollectionUtils.isNotEmpty(liSystemSetting)) {
            systemSettingEntity = liSystemSetting.get(0);
        }
        BeanConvertUtils.copy(systemSettingDto, systemSettingEntity);
        
        iSystemSettingReposytory.save(systemSettingEntity);
    }
    
    /**
     * @return SystemSettingDto
     */
    public SystemSettingDto findSystemSetting() {
        List<SystemSetting> systemSettings = iSystemSettingReposytory.findAll();

        if(CollectionUtils.isEmpty(systemSettings)) {
            return new SystemSettingDto();
        }

        return BeanConvertUtils.createAndCopy(systemSettings.get(0), SystemSettingDto.class);
    }
}
