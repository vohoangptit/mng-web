package com.nera.nms.dto;

import com.nera.nms.models.InventoryGroupHost;
import com.nera.nms.models.InventoryHost;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class InventoryGroupHostDTO{

	private long no = 0;

	private long inventoryId;
	
	private long id;
   
	private String name;
    
    private boolean isActive;
    
    private boolean isDeleted;
    
    private ArrayList<Long> hostsId;

    private int numberHost;
    
    private String createdBy;
	
	private Date createdDate;
	
	private String modifiedBy;
	
	private Date modifiedDate;

	public InventoryGroupHostDTO() {
	}

	public InventoryGroupHostDTO(InventoryGroupHost inventoryGroupHost) {
		this.id = inventoryGroupHost.getId();
		this.name = inventoryGroupHost.getName();
		this.numberHost = CollectionUtils.isNotEmpty(inventoryGroupHost.getHosts()) ? inventoryGroupHost.getHosts().size() : 0;
		this.isActive = inventoryGroupHost.isActive();
	}

	public InventoryGroupHost dtoToEntity(InventoryGroupHost inventoryGroupHost) {
		if (inventoryGroupHost != null) {
			inventoryGroupHost.setName(getName());

			Set<InventoryHost> inventoryHostSet = new HashSet<>();
			if(!getHostsId().isEmpty()) {
				for(Long item : getHostsId()) {
					InventoryHost inventoryHost = new InventoryHost();
					inventoryHost.setId(item);
					inventoryHostSet.add(inventoryHost);
				}
			}

			inventoryGroupHost.setHosts(inventoryHostSet);
			inventoryGroupHost.setActive(isActive());
			if (StringUtils.isNotBlank(getCreatedBy())) {
				inventoryGroupHost.setCreatedDate(getCreatedDate());
				inventoryGroupHost.setCreatedBy(getCreatedBy());
			}
			inventoryGroupHost.setModifiedBy(getModifiedBy());
			inventoryGroupHost.setModifiedDate(getModifiedDate());
			inventoryGroupHost.setDeleted(isDeleted());
		}
		return inventoryGroupHost;
	}
}
