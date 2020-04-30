package com.nera.nms.dto;

import java.util.Date;

import com.nera.nms.models.InventoryHost;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class InventoryHostDTO {

    private long no = 0;

	private long id;
    
    private String name;
    
    private String ipAddress;
    
    private int port;
    
    private String description;
    
    private boolean isActive;

    private boolean isDeleted;
    
	private long inventoryId;
	
	private String createdBy;
	
	private Date createdDate;
	
	private String modifiedBy;
	
	private Date modifiedDate;

	private String groupName;

	private String username;

	private String password;

    public InventoryHostDTO() {
    }

    public InventoryHostDTO(InventoryHost inventoryHost) {
        this.id = inventoryHost.getId();
        this.name = inventoryHost.getName();
        this.description = inventoryHost.getDescription();
        this.ipAddress = inventoryHost.getIpAddress();
        this.port = inventoryHost.getPort();
        this.isActive = inventoryHost.isActive();
        this.username = inventoryHost.getUsername();
        this.password = inventoryHost.getPassword();
    }

    public InventoryHost dtoToEntity(InventoryHost entityHost) {
        if (entityHost != null) {
            entityHost.setName(getName());
            entityHost.setDescription(getDescription());
            entityHost.setIpAddress(getIpAddress());
            entityHost.setPort(getPort());
            entityHost.setActive(isActive());
            entityHost.setUsername(getUsername());
            entityHost.setPassword(getPassword());
            if (StringUtils.isNotBlank(getCreatedBy())) {
                entityHost.setCreatedDate(getCreatedDate());
                entityHost.setCreatedBy(getCreatedBy());
            }
            entityHost.setModifiedBy(getModifiedBy());
            entityHost.setModifiedDate(getModifiedDate());
            entityHost.setDeleted(isDeleted());
        }
        return entityHost;
    }
}
