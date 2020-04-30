package com.nera.nms.rest.controllers;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.MetaDto;
import com.nera.nms.dto.ResponseDataTableDto;
import com.nera.nms.dto.SystemAuditLogDto;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.impl.AuditLogDaoImpl;
import com.nera.nms.services.FileStorageService;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.BeanConvertUtils;
import com.nera.nms.utils.CSVUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/nera/api")
@Slf4j
@PropertySource("classpath:messages.properties")
public class AuditLogRestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ISystemAuditLogRepository iSystemAuditLogRepository;

    private FileStorageService fileStorageService;

    private AuditLogDaoImpl auditLogDaoImpl;

    public AuditLogRestController(ISystemAuditLogRepository iSystemAuditLogRepository, FileStorageService fileStorageService, AuditLogDaoImpl auditLogDaoImpl) {
        this.iSystemAuditLogRepository = iSystemAuditLogRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogDaoImpl = auditLogDaoImpl;
    }

    @PostMapping(path = "/list/auditlog")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOG')")
    public ResponseDataTableDto<SystemAuditLogDto> listAllAuditLogs(
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") String perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "0") String page,
            @RequestParam(value = "sort[field]", defaultValue = "actionDate") String sortBy,
            @RequestParam(value = "sort[sort]", defaultValue = CommonConstants.DESCENDING) String orderBy,
            @RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
            @RequestParam(value = "filteredFromActionDate", defaultValue = "") String filteredFromActionDate,
            @RequestParam(value = "filteredToActionDate", defaultValue = "") String filteredToActionDate) {

        // count current audit log data
        List<SystemAuditLogDto> systemAuditLogDTOs = new ArrayList<>();
        systemAuditLogDTOs.addAll(auditLogDaoImpl.findAuditLogWithQuery(page, perPage, sortBy, orderBy, query, filteredFromActionDate, filteredToActionDate));

        long count = auditLogDaoImpl.countAuditByQuery(query, filteredFromActionDate, filteredToActionDate);
        if (count == 0) {
            log.error("system audit log does not exist any data!");
            return null;
        }
        // row number for No
        AtomicLong numIncrement = new AtomicLong(1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        systemAuditLogDTOs.forEach(auditLog -> {
            auditLog.setNo(numIncrement.getAndIncrement() + (Integer.parseInt(page) - 1)*10);
            auditLog.setStrActionDate(simpleDateFormat.format(auditLog.getActionDate()));
        });
        String cal = Integer.toString((int) Math.ceil(count / 10.0));
        MetaDto metaDto = MetaDto.builder().total(count).page(page).pages(cal).perpage(perPage).build();
        return ResponseDataTableDto.<SystemAuditLogDto>builder().meta(metaDto).data(systemAuditLogDTOs).build();
    }

    @PostMapping("/export-by-time")
    public ResponseEntity<Object> exportByCreatedDate(@ModelAttribute(value = "path") String path,
            @ModelAttribute(value = "startDate") String startDate, @ModelAttribute(value = "endDate") String endDate) {
        try {
            String newFromActionDate = startDate + " 00:00:00";
            String newToActionDate = endDate + " 23:59:59";

            FileWriter writer = fileStorageService.getWriter(path);
            List<SystemAuditLog> entity = iSystemAuditLogRepository.findActionDateBetween(
                    BEDateUtils.formatDate(newFromActionDate, CommonConstants.DATE_TIME_SEC_FORMAT),
                    BEDateUtils.formatDate(newToActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
            logger.info("Size Audit Log : {}", entity.size());
            List<SystemAuditLogDto> dto = BeanConvertUtils.copyList(entity, SystemAuditLogDto.class);
            CSVUtils.writeLine(writer, Arrays.asList("UserName", "UserGroupName", "Description", "Details",
                    "Status", "Action Date(MM/dd/yyyy hh:mm)"), ' ', ' ');
            TimeZone timezone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat formatWriter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatWriter.setTimeZone(timezone);

            for (SystemAuditLogDto data : dto) {
                List<String> list = new ArrayList<>();
                list.add(data.getUserName());
                list.add(data.getUserGroupName().replace(',', ' '));
                list.add(data.getDescription());
                list.add(data.getDetails());
                list.add(data.getStatus());
                list.add(formatWriter.format(data.getActionDate()));
                CSVUtils.writeLine(writer, list, ' ', ' ');
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("AuditLogRestController.exportByCreatedDate ", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
