package com.nera.nms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nera.nms.models.InventoryGroupHost;
import com.nera.nms.repositories.InventoryGroupHostRepository;

@Service
public class InventoryGroupHostService {

	@Autowired
	private	InventoryGroupHostRepository inventoryGroupOHostRepository;
	
	public InventoryGroupHost findByName(String name) {
		 return inventoryGroupOHostRepository.findByName(name);
	}

	public InventoryGroupHost findByNameAndOtherId(String name, long groupId) {
		return inventoryGroupOHostRepository.findByNameAndOtherId(name, groupId);
	}
	
}
