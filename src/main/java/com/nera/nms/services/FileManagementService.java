package com.nera.nms.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.utils.BEDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nera.nms.dto.FileManagementWrapper;
import com.nera.nms.models.FileManagement;
import com.nera.nms.repositories.FileManagementRepository;
import com.nera.nms.repositories.impl.FileManagementDaoImpl;

@Service
public class FileManagementService {

	@Autowired
	private FileManagementRepository fileManagementRepository;
	
	@Autowired
	private FileManagementDaoImpl fileManagementDaoImpl;
	
	@PersistenceContext
	EntityManager em;

	public List<FileManagement> getAll() {
		return fileManagementRepository.findAllByIsActiveAndIsDeleted(true, false);
	}
	
	public FileManagement getById(Long id) {
		return fileManagementRepository.findOneById(id);
	}
	 
	public long getFileList(String page, String perPage, String sortBy, String orderBy, String searchText, List <FileManagement> fileList) {
		return fileManagementDaoImpl.findFilesWithQuery(page, perPage, sortBy, orderBy, searchText, fileList);
	}

	public String findFileNameByName(String name) {
		return fileManagementRepository.findFileNameByName(name);
	}

	public void deleteById(long id) {
		fileManagementRepository.deleteById(id);
	}
	
	public void updateFile(String id, String name, String description, boolean isActive, String updateBy) {
		Optional<FileManagement> file= fileManagementRepository.findById(Long.parseLong(id));
		if(file.isPresent()){
			FileManagement fileManagement = file.get();
			fileManagement.setName(name);
			fileManagement.setActive(isActive);
			fileManagement.setDescription(description);
			fileManagement.setVersion(fileManagement.getVersion()+1);
			fileManagement.setModifiedDate(BEDateUtils.getCurrentDate());
			fileManagement.setModifiedBy(updateBy);
			fileManagementRepository.flush();
		}
	}
	
	public void updateFileWithFilename(String id, String name, String description, String filename, boolean isActive, String updateBy) {
		Optional<FileManagement> file= fileManagementRepository.findById(Long.parseLong(id));
		if(file.isPresent()){
			FileManagement fileManagement = file.get();
			fileManagement.setName(name);
			fileManagement.setActive(isActive);
			fileManagement.setFilename(filename);
			fileManagement.setDescription(description);
			fileManagement.setVersion(fileManagement.getVersion()+1);
			fileManagement.setModifiedBy(updateBy);
			fileManagement.setModifiedDate(BEDateUtils.getCurrentDate());
			fileManagementRepository.flush();
		}
	}
	
	public long checkFile(String filename, String name, String id) {
		return fileManagementRepository.findbyfilenameExceptId(filename, name, Long.parseLong(id));
	}
	
	public long checkName(String name, String id) {
		return fileManagementRepository.findbyNameExceptId(name, Long.parseLong(id));
	}
	
	public FileManagementWrapper checkMultipleFiles(FileManagementWrapper  fileManagement) {
		Query query = em.createQuery("SELECT COUNT(id) FROM FileManagement where name = :name AND isDeleted = false");
		for (int i = 0; i < fileManagement.getFileManagement().size(); i++) {
			String name = fileManagement.getFileManagement().get(i).getName();
			query.setParameter("name", name);
			
	    	long count = (long) query.getSingleResult();
	    	
	    	if (count > 0) {
	    		fileManagement.getFileManagement().get(i).setResult("Fail");
	    	} else {
	    		fileManagement.getFileManagement().get(i).setResult(CommonConstants.SYSTEM_SUCCESS);
	    	}
		}
		
		return fileManagement;
	}
	
	public boolean createFileWithFilename(String name, String description, String filename, String createdBy, Date createdDate, boolean isActive, boolean isDeleted) {
		FileManagement fileManagement = new FileManagement(name, description, filename, createdBy, new Timestamp(createdDate.getTime()), isActive, isDeleted);
		fileManagementRepository.save(fileManagement);
		return true;
	}
}
