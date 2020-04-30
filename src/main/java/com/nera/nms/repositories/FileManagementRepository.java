package com.nera.nms.repositories;

import com.nera.nms.models.FileManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FileManagementRepository extends JpaRepository<FileManagement, Long> {

	List<FileManagement> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted);
	
	FileManagement findOneById(Long id);

	@Query("Select f.filename from FileManagement f where f.isDeleted = false and f.name=:name")
	String findFileNameByName(String name);
	
	@Query(value = "SELECT COUNT(id) FROM file_management WHERE (filename = :filename OR name = :name) AND id <> :id AND is_deleted = '0'", nativeQuery = true)
	 long findbyfilenameExceptId(@Param("filename") String filename, @Param("name") String name, @Param("id") long id);
	
	@Query(value = "SELECT COUNT(id) FROM file_management WHERE name = :name AND id <> :id AND is_deleted = '0'", nativeQuery = true)
	 long findbyNameExceptId(@Param("name") String name, @Param("id") long id);
	
}
