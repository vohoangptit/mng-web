package com.nera.nms.services;

        import com.nera.nms.models.FileManagement;
        import com.nera.nms.models.FileManagementHistory;

        import java.util.List;

public interface FileManagementHistoryService {

    FileManagementHistory save(FileManagementHistory fileManagementHistory);

    List<FileManagementHistory> getListByFileId(long fileId);

    FileManagementHistory getFileHistoryById(long id);

    FileManagementHistory create(FileManagement fileManagement, String createdBy);

}
