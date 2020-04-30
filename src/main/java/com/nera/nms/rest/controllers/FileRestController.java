package com.nera.nms.rest.controllers;

import com.nera.nms.models.FileManagement;
import com.nera.nms.services.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("nera/file-management/api")
public class FileRestController {

	@Autowired
	private FileManagementService fileManagementService;
	
	@GetMapping("/get-all")
	public ResponseEntity<List<FileManagement>> getList() {
		List<FileManagement> fileManagement = fileManagementService.getAll();
		return new ResponseEntity<>(fileManagement, HttpStatus.OK);
	}
}
