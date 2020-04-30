package com.nera.nms.rest.controllers;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.*;
import com.nera.nms.models.InventoryGroupHost;
import com.nera.nms.models.InventoryHost;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.FileStorageService;
import com.nera.nms.services.InventoryGroupHostService;
import com.nera.nms.services.InventoryHostService;
import com.nera.nms.services.InventoryService;
import com.nera.nms.utils.CSVUtils;
import com.nera.nms.utils.PageableUtil;
import com.nera.nms.utils.ValidationUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("nera/inventory/api")
public class InventoryRestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final char DEFAULT_SEPARATOR = ',';

    private static final char DEFAULT_QUOTE = '"';

    @Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryHostService inventoryHostService;

    @Autowired
    private InventoryGroupHostService inventoryGroupHostService;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/get-by-id")
    public ResponseEntity<PageableDTO> getListHost(
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "1") int page,
            @RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
            @RequestParam(value = "query[hostSearchKey]", defaultValue = "") String query) {
        // create pageable with client parameter
        Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);

        Page<InventoryHost> inventoryHosts = inventoryService.findAllHostWithSearchString(pageable, query);
        // map entity to dto
        Page<InventoryHostDTO> inventoryHostDTOS = inventoryHosts.map(InventoryHostDTO::new);

        // identified number row and map to dto result
        AtomicLong numIncrement = new AtomicLong();
        List<InventoryHostDTO> listHost = inventoryHostDTOS.getContent();
        listHost.forEach(object -> object.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage)));
        PageableDTO result = PageableUtil.pageableMapper(inventoryHostDTOS, pageable, listHost);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-list-group")
    public ResponseEntity<PageableDTO> getListGroup(
            @RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
            @RequestParam(value = "pagination[page]", defaultValue = "1") int page,
            @RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
            @RequestParam(value = "query[groupSearchKey]", defaultValue = "") String query) {
        // create pageable with client parameter
        Pageable pageable;
        List<InventoryGroupHostDTO> listGroup;
        Page<InventoryGroupHost> inventoryGroupHosts;
        PageableDTO result;
        AtomicLong numIncrement = new AtomicLong();

        if(sortBy.equals("numberHost")) {
            pageable = PageRequest.of(page - 1, perPage);
            List<InventoryGroupHost> lstGroup = inventoryService.sortWithHosts(pageable, query, orderBy);
            listGroup = lstGroup.stream().map(InventoryGroupHostDTO::new).collect(Collectors.toList());
            listGroup.forEach(object -> object.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage)));

            pageable = PageableUtil.createPageable(page - 1, perPage, "createdDate", orderBy);
            inventoryGroupHosts = inventoryService.findAllGroupWithSearchString(pageable, query);
            result = PageableUtil.pageableMapper(inventoryGroupHosts, pageable, listGroup);
        } else {
            pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);
            inventoryGroupHosts = inventoryService.findAllGroupWithSearchString(pageable, query);
            // map entity to dto
            Page<InventoryGroupHostDTO> inventoryGroupHostDTOS = inventoryGroupHosts.map(InventoryGroupHostDTO::new);
            listGroup = inventoryGroupHostDTOS.getContent();
            // identified number row and map to dto result
            listGroup.forEach(object -> object.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage)));
            result = PageableUtil.pageableMapper(inventoryGroupHostDTOS, pageable, listGroup);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/delete-host")
    public boolean deleteHostById(@RequestParam long hostId, HttpServletRequest request) {
        boolean result = inventoryService.deleteHostById(hostId);
        if (result) {
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            SystemAuditLog systemAuditLog = new SystemAuditLog();
            CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
            String details = "Host Id: " + hostId + " has been deleted by " + data.getEmail();
            String desc = "Delete Host Successful";
            commonMethodComponent.addAuditLogData(systemAuditLog,
                    data.getFullName(),
                    data.getGroups(),
                    details,
                    desc,
                    true);
            iSystemAuditLogRepository.save(systemAuditLog);
        }
        return result;
    }

    @PostMapping("/delete-group-host")
    public void deleteGroupHostById(@RequestParam long groupHostId,
                                    HttpServletRequest request) {
        try {
            if (inventoryService.deleteGroupHostById(groupHostId)) {
                UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = "GroupHost Id: " + groupHostId + "has been deleted by " + data.getEmail();
                String desc = "Delete GroupHost Inventory";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        details,
                        desc,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
            }
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.deleteGroupHostById ", e);
        }
    }

    @PostMapping("/add-host")
    public ResponseEntity<ResultDTO> addHost(@RequestBody InventoryHostDTO dto, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (checkNameIpPort(dto, result)) {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            dto.setCreatedBy(data.getFullName());
            dto.setCreatedDate(new Date());
            if (inventoryService.addHost(dto)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = "Host: " + dto.getName() + "(Inventory Id: " + dto.getInventoryId() + ") has been created by " + data.getEmail();
                String desc = "Create Host Inventory";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        details,
                        desc,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess("Add Host Successful");
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.addHost ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/check-host-job")
    public boolean checkHostJob(@RequestBody InventoryHostDTO dto) {
        InventoryHost host = inventoryService.findByHostId(dto.getId());
        if(CollectionUtils.isNotEmpty(host.getJobs()) && host.isActive() && !dto.isActive()) {
            return true;
        }
        return false;
    }

    @PostMapping("/update-host")
    public ResponseEntity<ResultDTO> updateHost(@RequestBody InventoryHostDTO dto, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (checkNameIpPort(dto, result)) {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            dto.setModifiedBy(data.getFullName());
            dto.setModifiedDate(new Date());

            if (inventoryService.updateHost(dto)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = "Host: " + dto.getName() + "(Inventory Id: " + dto.getInventoryId() + ") has been updated by " + data.getEmail();
                String desc = "Update Host Inventory";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        details,
                        desc,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess("Update Host Successful");
                result.setCode(200);
            }

        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.updateHost ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private boolean checkNameIpPort(@RequestBody InventoryHostDTO dto, ResultDTO result) {
        if (!ValidationUtil.validate(dto.getIpAddress())) {
            result.setFieldName("ipAddress");
            result.setDetail("Invalid IP Address. Format like : XXX.XXX.XXX.XXX");
            result.setCode(400);
            return true;
        }
        List<InventoryHost> inventoryHosts;

        if (dto.getId() > 0) {
            inventoryHosts = inventoryHostService.findByNameIpAndOtherID(dto.getName(), dto.getIpAddress(), dto.getPort(), dto.getId());
        } else {
            inventoryHosts = inventoryHostService.findByNameAndInventoryId(dto.getName(), dto.getIpAddress(), dto.getPort());
        }

        if (!inventoryHosts.isEmpty()) {
            for (InventoryHost item : inventoryHosts) {
                if (StringUtils.equalsIgnoreCase(item.getName(), dto.getName())) {
                    result.setFieldName("HostName");
                    result.setDetail("Host Name already exists");
                    continue;
                }
                if (StringUtils.equalsIgnoreCase(item.getIpAddress(), dto.getIpAddress()) && item.getPort() == dto.getPort()) {
                    result.setFieldName("IP Address/Port");
                    result.setDetail("The same IP address already exists in this Inventory");
                }
            }
            result.setCode(400);
            return true;
        }
        return false;
    }

    @PostMapping("/add-group-host")
    public ResponseEntity<ResultDTO> addGroupHost(@RequestBody InventoryGroupHostDTO inventoryGroupHostDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (checkGroupNameExisted(inventoryGroupHostDTO, result)) {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            inventoryGroupHostDTO.setCreatedBy(data.getFullName());
            inventoryGroupHostDTO.setCreatedDate(new Date());

            if (inventoryService.addGroupHost(inventoryGroupHostDTO)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = "GroupHost: " + inventoryGroupHostDTO.getName() + " has been created by " + data.getEmail();
                String desc = "Create GroupHost Inventory";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        details,
                        desc,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess("Add Group Host Successful");
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.addGroupHost ", e);
            result.setMess("Add Fail");
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/update-group-host")
    public ResponseEntity<ResultDTO> updateGroupHost(@RequestBody InventoryGroupHostDTO inventoryGroupHostDTO, HttpServletRequest request) {
        ResultDTO result = new ResultDTO();
        try {
            if (checkGroupNameExisted(inventoryGroupHostDTO, result)) {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            inventoryGroupHostDTO.setModifiedBy(data.getFullName());
            inventoryGroupHostDTO.setModifiedDate(new Date());

            if (inventoryService.updateGroupHost(inventoryGroupHostDTO)) {
                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String details = "GroupHost: " + inventoryGroupHostDTO.getName() + " has been updated by " + data.getEmail();
                String desc = "Update GroupHost Inventory";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        data.getFullName(),
                        data.getGroups(),
                        details,
                        desc,
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
                result.setMess("Update Group Host Successful");
                result.setCode(200);
            }
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.updateGroupHost ", e);
            result.setMess("Update Failed");
            result.setCode(500);
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private boolean checkGroupNameExisted(InventoryGroupHostDTO inventoryGroupHostDTO, ResultDTO result) {
        InventoryGroupHost checkInventoryGroupHost;

        if (inventoryGroupHostDTO.getId() > 0) {
            checkInventoryGroupHost = inventoryGroupHostService.findByNameAndOtherId(inventoryGroupHostDTO.getName(), inventoryGroupHostDTO.getId());
        } else {
            checkInventoryGroupHost = inventoryGroupHostService.findByName(inventoryGroupHostDTO.getName());
        }

        if (checkInventoryGroupHost != null) {
            result.setFieldName("GroupHostName");
            result.setDetail("GroupHost Name already exists");
            result.setCode(400);
            return true;
        }
        return false;
    }

    @GetMapping("/search-host")
    public ResponseEntity<List<InventoryHost>> searchHost(
            @RequestParam(value = "searchString", defaultValue = "", required = false) String searchString) {
        List<InventoryHost> result;
        try {
            result = inventoryService.listingAndFilterHost(searchString);
            AtomicLong numIncrement = new AtomicLong(1);
            result.forEach(object ->
                    object.setNo(numIncrement.getAndIncrement()));
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.searchHost ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search-group-host")
    public ResponseEntity<List<InventoryGroupHost>> searchGroupHost(
            @RequestParam(value = "searchString", defaultValue = "", required = false) String searchString) {
        List<InventoryGroupHost> result;
        try {
            result = inventoryService.listingAndFilterGroupHost(searchString);
            AtomicLong numIncrement = new AtomicLong(1);
            result.forEach(object -> {
                object.setNo(numIncrement.getAndIncrement());
                object.setNumberHost(object.getHosts().size());
            });
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.searchGroupHost ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-host-by-id")
    public ResponseEntity<InventoryHostDTO> getHostById(@RequestParam long hostId) {
        InventoryHost result;
        try {
            result = inventoryService.getHostById(hostId);
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.getHostById ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new InventoryHostDTO(result), HttpStatus.OK);
    }

    @GetMapping("/get-group-host-by-id")
    public ResponseEntity<InventoryGroupHost> getGroupHostById(@RequestParam long groupHostId) {
        InventoryGroupHost result;
        try {
            result = inventoryService.getGroupHostById(groupHostId);
            result.setNumberHost(result.getHosts().size());
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.getGroupHostById ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/upload-file")
    public ResponseEntity<Map<String, Object>> uploadFileGroupHost(@RequestParam("file") MultipartFile uploadFile, HttpServletRequest request) {
        Map<String, Object> mapResponseMap = new ConcurrentHashMap<>();
        List<InventoryUploadDTO> result = new ArrayList<>();
        try(Scanner sc = new Scanner(uploadFile.getInputStream())) {
            UserProfileDto userProfile = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
            int i = 0;
            int totalSuccess = 0;
            int totalFail = 0;
            List<String> line = Collections.emptyList();
            while (sc.hasNext()) {
                line = getListStringLine(sc, line);
                if (i == 0) {
                    i++;
                    if (CollectionUtils.isEmpty(line) || line.size() != 7) {
                        return new ResponseEntity<>(mapResponseMap, HttpStatus.METHOD_NOT_ALLOWED);
                    }
                    line = getListStringLine(sc, line);
                }
                InventoryUploadDTO inventoryUploadDTO = getInventoryUploadDTO(userProfile, line);
                if (StringUtils.isBlank(inventoryUploadDTO.getImportStatus())) {
                    inventoryUploadDTO.setImportStatus(CommonConstants.SYSTEM_SUCCESS);
                    totalSuccess+=1;
                } else {
                    totalFail+=1;
                }
                result.add(inventoryUploadDTO);
            }
            mapResponseMap.put("totalSuccess", totalSuccess);
            mapResponseMap.put("totalFail", totalFail);
            if (!result.isEmpty()) {
                findDuplicates(result, mapResponseMap);
                inventoryService.addHostImport(result, mapResponseMap);
                long milliseconds = new Date().getTime();
                String fileNameReport = "Import Report-" + milliseconds + ".csv";
                String pathFile = uploadDir + CommonConstants.PATH_DELIMITER + fileNameReport;

                //create file and write file import report
                boolean checkCreateFileCSV = writeFileCSV(result, pathFile, fileNameReport);

                StringBuilder sb = new StringBuilder();

                SystemAuditLog systemAuditLog = new SystemAuditLog();
                CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
                String fileName = fileStorageService.storeFile(uploadFile);

                sb.append("Link Down File : <a href='" + getFileDownloadUri(fileName) + "'>" + uploadFile.getOriginalFilename() + "</a><br/>");
                if (checkCreateFileCSV) {
                    sb.append("Import Report : <a href='").append(getFileDownloadUri(fileNameReport)).append("'>").append("Import Report").append("</a>");
                }

                String details = "Import Host CSV";
                commonMethodComponent.addAuditLogData(systemAuditLog,
                        userProfile.getFullName(),
                        userProfile.getGroups(),
                        details,
                        sb.toString(),
                        true);
                iSystemAuditLogRepository.save(systemAuditLog);
            }
            mapResponseMap.put("listReport", result);
        } catch (Exception e) {
            logger.error("Exception: InventoryRestController.uploadFileGroupHost ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(mapResponseMap, HttpStatus.OK);
    }

    private List<String> getListStringLine(Scanner sc, List<String> line) {
        String lineText = sc.nextLine();
        if (StringUtils.isNotBlank(lineText)) {
            line = CSVUtils.readLine(lineText, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
        }
        return line;
    }

    private String getFileDownloadUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("nera/upload/downloadFile/")
                .path(fileName).toUriString();
    }

    private boolean writeFileCSV(List<InventoryUploadDTO> result, String pathFile, String fileNameReport) throws IOException {
        File fileCsv = new File(pathFile);
        boolean checkCreateFile = fileCsv.createNewFile();
        if (checkCreateFile) {
            FileWriter csvWriter = fileStorageService.getWriter(fileNameReport);
            CSVUtils.writeLine(csvWriter, Arrays.asList("S/N", "Host name", "Host group name", "IP address",
                    "Port", "Description", "Status", "Import status", "username", "password"), ' ', ' ');

            for (int n = 0; n < result.size(); n++) {
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(n + 1));
                list.add(result.get(n).getHostName());
                list.add(result.get(n).getGroupName());
                list.add(result.get(n).getIpAddress());
                list.add(result.get(n).getPort());
                list.add(result.get(n).getDescription());
                list.add("Active");
                list.add(result.get(n).getImportStatus());
                list.add(result.get(n).getUsername());
                list.add(result.get(n).getPassword());
                CSVUtils.writeLine(csvWriter, list, ' ', ' ');
            }
            csvWriter.flush();
            csvWriter.close();
        }
        return checkCreateFile;
    }


    private InventoryUploadDTO getInventoryUploadDTO(UserProfileDto userProfile, List<String> line) {
        InventoryUploadDTO inventoryUploadDTO = new InventoryUploadDTO();
        if (StringUtils.isBlank(line.get(0))) {
            inventoryUploadDTO.setImportStatus("Missing Host Name");
        }
        inventoryUploadDTO.setHostName(line.get(0));

        inventoryUploadDTO.setDescription(line.get(1));

        String ipAddress = line.get(2);
        if (StringUtils.isBlank(ipAddress) && StringUtils.isBlank(inventoryUploadDTO.getImportStatus())) {
            inventoryUploadDTO.setImportStatus("Missing IP address");
        } else if (!ValidationUtil.validate(ipAddress) && StringUtils.isBlank(inventoryUploadDTO.getImportStatus())) {
            inventoryUploadDTO.setImportStatus("Invalid IP Address. Format like : XXX.XXX.XXX.XXX");
        }
        inventoryUploadDTO.setIpAddress(ipAddress);

        String port = line.get(3);
        if (StringUtils.isBlank(port) && StringUtils.isBlank(inventoryUploadDTO.getImportStatus())) {
            inventoryUploadDTO.setImportStatus("Missing Port");
        } else if (!StringUtils.isNumeric(port) && StringUtils.isBlank(inventoryUploadDTO.getImportStatus())) {
            inventoryUploadDTO.setImportStatus("Wrong data type. Port is number");
        }
        inventoryUploadDTO.setPort(port);

        inventoryUploadDTO.setIpAndPort(ipAddress + ":" + port);
        inventoryUploadDTO.setGroupName(line.get(4));
        inventoryUploadDTO.setUsername(line.get(5));
        inventoryUploadDTO.setPassword(line.get(6));
        inventoryUploadDTO.setCreatedBy(userProfile.getFullName());
        return inventoryUploadDTO;
    }

    private static void findDuplicates(List<InventoryUploadDTO> listContainingDuplicates, Map<String, Object> mapResponseMap) {
        final Set<String> setHostName = new HashSet<>();
        final Set<String> setPort = new HashSet<>();
        Integer totalSuccess = (Integer) mapResponseMap.get("totalSuccess");
        Integer totalFail = (Integer) mapResponseMap.get("totalFail");
        for (InventoryUploadDTO item : listContainingDuplicates) {
            if (StringUtils.equalsIgnoreCase(item.getImportStatus(), CommonConstants.SYSTEM_SUCCESS)) {
                if (!setHostName.add(item.getHostName())) {
                    item.setImportStatus("Duplicate Host Name in the import file");
                    totalSuccess-=1;
                    totalFail+=1;
                } else if (!setPort.add(item.getIpAndPort())) {
                    item.setImportStatus("Duplicate IP and Port in the import file");
                    totalSuccess-=1;
                    totalFail+=1;
                }
            }
        }
        mapResponseMap.put("totalSuccess", totalSuccess);
        mapResponseMap.put("totalFail", totalFail);
    }
}
