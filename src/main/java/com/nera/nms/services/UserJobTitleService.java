package com.nera.nms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nera.nms.models.UserJobTitle;
import com.nera.nms.repositories.IUserJobTitleRepository;

@Service
public class UserJobTitleService {
	
	@Autowired
	IUserJobTitleRepository iUserJobTitleRepository;
	
	public List<UserJobTitle> findLinkedUserJobTitle(){
		return iUserJobTitleRepository.findLinkedUserJobTitles();
	}
}
