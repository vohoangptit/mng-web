package com.nera.nms.services;

import com.nera.nms.models.InventoryHost;
import com.nera.nms.repositories.InventoryHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryHostService {

    @Autowired
    private InventoryHostRepository inventoryHostRepository;

    public List<InventoryHost> findByNameAndInventoryId(String name, String ipAddress, int port) {
        return inventoryHostRepository.findByNameAndInventoryId(name, ipAddress, port);
    }

    public List<InventoryHost> findByNameIpAndOtherID(String name, String ipAddress, int port, long hostId) {
        return inventoryHostRepository.findByNameIpAndOtherID(name, ipAddress, port, hostId);
    }
}
