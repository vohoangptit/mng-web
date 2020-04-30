package com.nera.nms.services;


import com.nera.nms.dto.GroupDTO;
import com.nera.nms.models.Group;
import com.nera.nms.repositories.IGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private IGroupRepository iGroupRepository;
    

    public Page<GroupDTO> findAll(Pageable pageable, String search) {
        return iGroupRepository.findLikeSearchString(search, pageable).map(GroupDTO::new);
    }
    
    public Page<GroupDTO> findActive(Pageable pageable, String search) {
        return iGroupRepository.findActiveSearchString(search, pageable).map(GroupDTO::new);
    }
    
    public void addGroup(GroupDTO dto) {
    	iGroupRepository.save(dto.dtoToEntity(dto));
    }
    
    public List<String> getPermissionByGroupName(String groupName){
    	return new ArrayList<>(iGroupRepository.getPermissionByGroup(groupName));
    }
    
    public List <Group> findLinkedUserGroups() {
    	return iGroupRepository.findLinkedUserGroups();
    }
}
