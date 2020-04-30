package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.FileManagementHistoryDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.FileManagement;
import com.nera.nms.models.FileManagementHistory;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.services.AuditLogService;
import com.nera.nms.services.FileManagementHistoryService;
import com.nera.nms.services.FileManagementService;
import com.nera.nms.utils.AuditLogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("nera/file-history/api")
public class FileManagementHistoryRestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileManagementHistoryService fileManagementHistoryService;

    @Autowired
    private FileManagementService fileManagementService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<List<FileManagementHistoryDTO>> getListFileHistoryByFileId(@RequestParam("fileId") long fileId) {
        List<FileManagementHistoryDTO> listDto = new ArrayList<>();
        logger.info("Calling FileManagementHistoryRestController::getListFileHistoryByFileId");
        try {
            List<FileManagementHistory> listByFileId = fileManagementHistoryService.getListByFileId(fileId);
            if (CollectionUtils.isEmpty(listByFileId)) {
                return new ResponseEntity<>(listDto, HttpStatus.NO_CONTENT);
            }
            AtomicLong numIncrement = new AtomicLong();
            listByFileId.forEach(file -> {
                FileManagementHistoryDTO fileHistory = new FileManagementHistoryDTO(file);
                fileHistory.setNo(numIncrement.incrementAndGet());
                listDto.add(fileHistory);
            });
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(listDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

    @PostMapping("/get-by-id")
    public ResponseEntity<FileManagementHistoryDTO> getFileHistoryById(@RequestParam("id") long id) {
        logger.info("Calling FileManagementHistoryRestController::getFileHistoryById");
        try {
            FileManagementHistory fileHistory = fileManagementHistoryService.getFileHistoryById(id);
            if (fileHistory == null) {
                return new ResponseEntity<>(new FileManagementHistoryDTO(), HttpStatus.NO_CONTENT);
            }
            FileManagementHistoryDTO fileHistoryDto = new FileManagementHistoryDTO(fileHistory);
            return new ResponseEntity<>(fileHistoryDto, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(new FileManagementHistoryDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/restore")
    public ResponseEntity<ResultDTO> restore(@RequestParam("id") long id, HttpServletRequest request) {
        logger.info("Calling FileManagementHistoryRestController::restore api");
        UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
        ResultDTO result = new ResultDTO();
        try {
            FileManagementHistory fileHistory = fileManagementHistoryService.getFileHistoryById(id);
            if (fileHistory == null) {
                result.setCode(HttpStatus.NO_CONTENT.value());
                return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
            }

            FileManagement file = fileManagementService.getById(fileHistory.getFileManagementId());
            fileManagementHistoryService.create(file, data.getFullName());

            fileManagementService.updateFileWithFilename(String.valueOf(fileHistory.getFileManagementId()), fileHistory.getName(), fileHistory.getDescription(), fileHistory.getFilename(), fileHistory.isActive(), data.getFullName());

            result.setCode(HttpStatus.OK.value());

            String details = "File: " + file.getName() + " has been restored by " + data.getFullName();
            SystemAuditLog systemAuditLog  = AuditLogUtil.getSystemAuditLog(data.getFullName(), data.getGroups(), details, "Restore the file");
            auditLogService.createAuditLog(systemAuditLog);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}