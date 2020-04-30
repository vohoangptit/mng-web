package com.nera.nms.services.impl;

import com.nera.nms.models.FileManagement;
import com.nera.nms.models.FileManagementHistory;
import com.nera.nms.repositories.FileManagementHistoryRepository;
import com.nera.nms.services.FileManagementHistoryService;
import com.nera.nms.utils.BEDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileManagementHistoryServiceImpl implements FileManagementHistoryService {

    @Autowired
    FileManagementHistoryRepository fileHistoryRepository;

    @Override
    public FileManagementHistory save(FileManagementHistory fileManagementHistory) {
        return fileHistoryRepository.save(fileManagementHistory);
    }

    @Override
    public List<FileManagementHistory> getListByFileId(long fileId) {
        return fileHistoryRepository.findAllByFileManagementId(fileId);
    }

    @Override
    public FileManagementHistory getFileHistoryById(long id) {
        return fileHistoryRepository.findById(id);
    }

    @Override
    public FileManagementHistory create(FileManagement fileManagement, String createdBy) {
        FileManagementHistory fileHistory = new FileManagementHistory();

        fileHistory.setName(fileManagement.getName());
        fileHistory.setDescription(fileManagement.getDescription());
        fileHistory.setActive(fileManagement.isActive());
        fileHistory.setCreatedDate(BEDateUtils.getCurrentDate());
        fileHistory.setCreatedBy(createdBy);
        fileHistory.setFileManagementId(fileManagement.getId());
        fileHistory.setVersion(fileManagement.getVersion());
        fileHistory.setFilename(fileManagement.getFilename());

        return save(fileHistory);
    }
}
