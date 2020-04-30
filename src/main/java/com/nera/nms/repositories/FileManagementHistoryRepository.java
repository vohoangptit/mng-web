package com.nera.nms.repositories;

import com.nera.nms.models.FileManagementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileManagementHistoryRepository extends JpaRepository<FileManagementHistory,Long> {

    List<FileManagementHistory> findAllByFileManagementId(long fileId);

    FileManagementHistory findById(long id);

}
