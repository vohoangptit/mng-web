package com.nera.nms.dto;

import com.nera.nms.models.FileManagementHistory;
import com.nera.nms.utils.BEDateUtils;
import lombok.Data;

@Data
public class FileManagementHistoryDTO {

    private long id;
    private long no;
    private int version;
    private String name;
    private String updatedBy;
    private String updatedAt;
    private String description;
    private String fileName;
    private long fileManagementId;
    private boolean isActive;


    public FileManagementHistoryDTO() {
    }

    public FileManagementHistoryDTO(FileManagementHistory file) {
        setId(file.getId());
        setVersion(file.getVersion());
        setUpdatedBy(file.getCreatedBy());
        setUpdatedAt(BEDateUtils.convertDateToStringByFormatOn(file.getCreatedDate()));
        setDescription(file.getDescription());
        setActive(file.isActive());
        setFileName(file.getFilename());
        setName(file.getName());
        setFileManagementId(file.getFileManagementId());
    }
}
