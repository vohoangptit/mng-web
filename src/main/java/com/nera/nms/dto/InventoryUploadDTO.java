package com.nera.nms.dto;

import com.nera.nms.models.InventoryHost;
import lombok.Data;

import java.util.Date;

@Data
public class InventoryUploadDTO {

    private String hostName;
    private String description;
    private String ipAddress;
    private String port;
    private String ipAndPort;
    private String groupName;
    private String importStatus;
    private String createdBy;
    private String username;
    private String password;

    public InventoryHost dtoToEntity(InventoryHost entityHost) {
        if (entityHost != null) {
            entityHost.setName(getHostName());
            entityHost.setDescription(getDescription());
            entityHost.setIpAddress(getIpAddress());
            entityHost.setPort(Integer.parseInt(getPort()));
            entityHost.setActive(true);
            entityHost.setCreatedDate(new Date());
            entityHost.setCreatedBy(getCreatedBy());
            entityHost.setDeleted(false);
            entityHost.setUsername(getUsername());
            entityHost.setPassword(getPassword());
        }
        return entityHost;
    }
}
