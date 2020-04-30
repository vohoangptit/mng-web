package com.nera.nms.services;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.InventoryGroupHostDTO;
import com.nera.nms.dto.InventoryHostDTO;
import com.nera.nms.dto.InventoryUploadDTO;
import com.nera.nms.models.InventoryGroupHost;
import com.nera.nms.models.InventoryHost;
import com.nera.nms.repositories.InventoryGroupHostRepository;
import com.nera.nms.repositories.InventoryHostRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InventoryService {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 42L;

	@Autowired
	private	InventoryHostRepository inventoryHostRepository;

	@Autowired
	private	InventoryGroupHostRepository inventoryGroupHostRepository;

	public boolean deleteGroupHostById(long groupHostId) {
		try {
			InventoryGroupHost inventoryGroupHost = inventoryGroupHostRepository.findOneById(groupHostId);
			if (inventoryGroupHost.getHosts() != null && !inventoryGroupHost.getHosts().isEmpty()) {
				inventoryGroupHostRepository.deleteGroupHost(inventoryGroupHost.getId());
			}
			inventoryGroupHostRepository.delete(inventoryGroupHost);
		} catch(final Exception e) {
			logger.error("Exception: InventoryService.deleteGroupHostById ", e);
			return false;
		}
		return true;
	}

	public boolean updateGroupHost(InventoryGroupHostDTO inventoryGroupHostDTO) {
		try {
			InventoryGroupHost groupHost = inventoryGroupHostRepository.findOneById(inventoryGroupHostDTO.getId());

			if(groupHost.getHosts().size() > inventoryGroupHostDTO.getHostsId().size()) {
				inventoryGroupHostDTO.getHostsId().forEach(i -> groupHost.getHosts().removeIf(g -> g.getId()==i));
				groupHost.getHosts().forEach(i -> inventoryGroupHostRepository.deleteGroupHostDetail(groupHost.getId(), i.getId()));
			} else {
				Iterator<InventoryHost> iter = groupHost.getHosts().iterator();
				while (iter.hasNext()) {
					long id = iter.next().getId();
					inventoryGroupHostDTO.getHostsId().removeIf(h -> h == id);
				}
				inventoryGroupHostDTO.getHostsId().forEach(i -> inventoryGroupHostRepository.saveGroupHostDetail(groupHost.getId(), i));
			}
			inventoryGroupHostDTO.dtoToEntity(groupHost);
			inventoryGroupHostRepository.save(groupHost);
		} catch(Exception e) {
			logger.error("Exception: InventoryService.updateGroupHost ", e);
			return false;
		}
		return true;
	}

	public boolean addGroupHost(InventoryGroupHostDTO inventoryGroupHostDTO) {
		try {
			InventoryGroupHost inventoryGroupHost = inventoryGroupHostDTO.dtoToEntity(new InventoryGroupHost());
			inventoryGroupHostRepository.save(inventoryGroupHost);
			inventoryGroupHostDTO.getHostsId().forEach(f -> inventoryGroupHostRepository.saveGroupHostDetail(inventoryGroupHost.getId(), f));
		} catch(Exception e) {
			logger.error("Exception: InventoryService.addGroupHost ", e);
			return false;
		}
		return true;
	}

	public boolean addHost(final InventoryHostDTO inventoryHostDTO) {
		try {
			InventoryHost entityHost = inventoryHostDTO.dtoToEntity(new InventoryHost());
			inventoryHostRepository.save(entityHost);
		} catch (Exception e) {
			logger.error("Exception: InventoryService.addHost ", e);
			return false;
		}
		return true;
	}

	public boolean updateHost(InventoryHostDTO inventoryHostDTO) {
		try {
			InventoryHost inventoryHost = inventoryHostRepository.findOneById(inventoryHostDTO.getId());
			if(inventoryHost.isActive() && inventoryHost.isActive() != inventoryHostDTO.isActive()) {
				
			}
			InventoryHost entityHost = inventoryHostDTO.dtoToEntity(inventoryHost);
			inventoryHostRepository.save(entityHost);
		} catch (Exception e) {
			logger.error("Exception: InventoryService.updateHost ", e);
			return false;
		}
		return true;
	}

	public boolean deleteHostById(long hostId) {
		try {
			InventoryHost inventoryHost = inventoryHostRepository.findOneById(hostId);
			if (inventoryHost != null) {
				inventoryHostRepository.deleteHostDetail(inventoryHost.getId());
				inventoryHostRepository.delete(inventoryHost);
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception: InventoryService.deleteHostById ", e);
		}
		return false;
	}

	public void addHostImport(List<InventoryUploadDTO> inventoryUploadDTOList, Map<String, Object> mapResponseMap) {
		AtomicInteger success = new AtomicInteger();
		try {
			inventoryUploadDTOList.forEach(l -> {
				if(StringUtils.equals(l.getImportStatus(), CommonConstants.SYSTEM_SUCCESS)) {
					InventoryHost inventoryHost = l.dtoToEntity(new InventoryHost());
					List<InventoryHost> findHost = inventoryHostRepository.findByNameAndInventoryId(l.getHostName(), l.getIpAddress(), Integer.parseInt(l.getPort()));
					if (CollectionUtils.isEmpty(findHost)) {
						inventoryHostRepository.save(inventoryHost);
					} else {
						l.setImportStatus("Duplicate record in the database");
						success.getAndIncrement();
						return;
					}
					if (StringUtils.isNotBlank(l.getGroupName())) {
						InventoryGroupHost inventoryGroupHost = inventoryGroupHostRepository.findByName(l.getGroupName());
						if (inventoryGroupHost != null) {
							inventoryGroupHostRepository.saveGroupHostDetail(inventoryGroupHost.getId(), inventoryHost.getId());
						} else {
							l.setImportStatus("Group name not found");
							success.getAndIncrement();
						}
					}
				}
			});
			mapResponseMap.put("totalSuccess", (Integer) mapResponseMap.get("totalSuccess") - success.get());
			mapResponseMap.put("totalFail", (Integer) mapResponseMap.get("totalFail") + success.get());
		} catch(Exception e) {
			logger.error("Exception: InventoryService.addHostImport ", e);
		}
	}

    public List<InventoryHost> findByGroupId(long groupId) {
        return inventoryHostRepository.findByGroupId(groupId);
    }

	public InventoryHost findByHostId(long hostId) {
		return inventoryHostRepository.findOneById(hostId);
	}

	public List<InventoryHost> findByGroupHostId(long groupId, long hostId) {
		return inventoryHostRepository.findByGroupHostId(groupId, hostId);
	}

	public List<InventoryHost> findHostByName(String name) {
		return inventoryHostRepository.findHostByName(name);
	}

	public List<InventoryHost> listingAndFilterHost(String searchString) {
		return inventoryHostRepository.findBySearchString(searchString);
	}

	public List<InventoryHost> listingHostActive(String searchString) {
		return inventoryHostRepository.findBySearchStringAndActive(searchString);
	}

	public List<InventoryGroupHost> listingAndFilterGroupHost (String searchString) {
		return inventoryGroupHostRepository.findBySearchString(searchString);
	}

	public InventoryGroupHost getGroupHostById (long groupHostId) {
		return inventoryGroupHostRepository.findOneById(groupHostId);
	}

	public InventoryHost getHostById (long groupHostId) {
		return inventoryHostRepository.findOneById(groupHostId);
	}

	public Page<InventoryGroupHost> findAllGroupWithSearchString(Pageable pageable, String searchString) {
		return inventoryGroupHostRepository.findAllRecord(searchString, pageable);
	}
    public Page<InventoryHost> findAllHostWithSearchString(Pageable pageable, String searchString) {
        return inventoryHostRepository.findAllRecord(searchString, pageable);
    }
	public List<InventoryGroupHost> sortWithHosts(Pageable pageable, String searchString, String sortBy) {
		if (StringUtils.equals(sortBy, "asc")) {
			return inventoryGroupHostRepository.sortByHostAsc(pageable, searchString);
		} else {
			return inventoryGroupHostRepository.sortByHostDesc(pageable, searchString);
		}
	}
}
