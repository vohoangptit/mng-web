package com.nera.nms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nera.nms.models.UserDepartment;
import com.nera.nms.repositories.IUserDepartmentRepository;

@Service
public class UserDepartmentService {
	@Autowired
	IUserDepartmentRepository iUserDepartmentRepository;
	public List <UserDepartment> findLinkedUserDeparment () {
		return iUserDepartmentRepository.findLinkedUserDepartment();
	}
}
