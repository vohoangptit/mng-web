package com.nera.nms.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.FileManagementDTO;
import com.nera.nms.dto.FileManagementSaveDTO;
import com.nera.nms.dto.FileManagementWrapper;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.FileManagement;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.FileManagementHistoryService;
import com.nera.nms.services.FileManagementService;
import com.nera.nms.utils.BEDateUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("nera/api")
public class FileManagementRestController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String MESSAGE_USER_DETAIL = "User ";

	@Autowired
	private FileManagementService fileManagementService;

	@Autowired
	private FileManagementHistoryService fileHistoryService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private ISystemAuditLogRepository iSystemAuditLogRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@PreAuthorize("hasAuthority('VIEW_FILE_LISTING')")
	@PostMapping("/GetAllFileManagement")
	public ObjectNode getList(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") String perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") String page,
			@RequestParam(value="sort[field]", defaultValue = "createdByDate") String sortBy,
			@RequestParam(value="sort[sort]", defaultValue = "DESC") String orderBy,
			@RequestParam(value="query[generalSearch]", defaultValue = "") String searchText) {
		List<FileManagement> fileList = new ArrayList <> ();

		long count = fileManagementService.getFileList(page, perPage, sortBy, orderBy, searchText, fileList);

		for (int i = 0; i < fileList.size(); i++) {
			fileList.get(i).setStatus(fileList.get(i).isActive() ? CommonConstants.ACTIVE : CommonConstants.INACTIVE);
			fileList.get(i).setCreatedByDate(BEDateUtils.formatDate(fileList.get(i).getCreatedDate(), CommonConstants.DATE_FORMAT_DD_MM));
		}
		AtomicLong numIncrement = new AtomicLong(1);
		fileList.forEach(file -> file.setNo((numIncrement.getAndIncrement() + (Integer.valueOf(page) - 1)*10)));
		log.info("get all file management success");
		ObjectNode metaNode = mapper.createObjectNode();
		ObjectNode paramsNode = mapper.createObjectNode();
		paramsNode.put("total", count);
		paramsNode.put("page", page);
		paramsNode.put("perpage", perPage);

		metaNode.set("meta", paramsNode);
		metaNode.putPOJO("data", fileList);
		return metaNode;
	}

	@PreAuthorize("hasAuthority('DELETE_FILE')")
	@DeleteMapping(value = "/deleteFile/")
	public String deleteFile
			(@RequestParam(value="id", defaultValue = "") String id, HttpServletRequest request) {

		FileManagement file = fileManagementService.getById(Long.parseLong(id));

		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		String groupStr = data.getGroups();
		String desc = "Delete File";
		String details = MESSAGE_USER_DETAIL + data.getFullName() + " has deleted " + file.getFilename() + CommonConstants.DOTS;

		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		commonMethodComponent.addAuditLogData(systemAuditLog,
				data.getFullName(),
				groupStr,
				details,
				desc,
				true);

		fileManagementService.deleteById(Long.parseLong(id));
		iSystemAuditLogRepository.save(systemAuditLog);

		return "true";
	}

	@PreAuthorize("hasAuthority('UPDATE_FILE')")
	@PostMapping(value = "/updateFileDetail")
	public String updateFile
			(@RequestParam(value="fileid") String id,
			 @RequestParam(value="name") String name,
			 @RequestParam(value="description") String description,
			 @RequestParam(value="file", required = false) MultipartFile file,
			 @RequestParam(value="status") String status,HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		FileManagement fileById = fileManagementService.getById(Long.parseLong(id));
		String nameFile = "";
		if (file != null) {

			String ext = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!ext.equalsIgnoreCase("yml") && !ext.equalsIgnoreCase("txt")
					&& !ext.equalsIgnoreCase("doc") && !ext.equalsIgnoreCase("docx")
					&& !ext.equalsIgnoreCase("pdf")) {
				return "false2";
			}

			byte[] bytes;
			try {
				bytes = file.getBytes();
				String[] arrNameFile = file.getOriginalFilename().split("\\.");
				int versionFile = fileById.getVersion()+1;
				nameFile = arrNameFile[0] + "_version" + versionFile + CommonConstants.DOTS + arrNameFile[1];
				Path path = Paths.get(uploadDir + CommonConstants.PATH_DELIMITER + nameFile);
				Files.write(path, bytes);
			} catch (IOException e) {
				log.error("FileManagementRestController.updateFile : ", e);
				return "false2";
			}
		}
		boolean isActive = true;
		if (status.equalsIgnoreCase("N")) {
			isActive = false;
		}


		fileHistoryService.create(fileById, data.getFullName());

		if (file == null) {
			long count = fileManagementService.checkName(name, id);
			if (count > 0) {
				return "false3";
			}
			fileManagementService.updateFile(id, name, description, isActive, data.getFullName());

		} else {
			fileManagementService.updateFileWithFilename(id, name, description, nameFile, isActive, data.getFullName());
		}

		String desc = "Update File";
		String details = MESSAGE_USER_DETAIL + data.getFullName() + " has updated " + name + CommonConstants.DOTS;
		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		commonMethodComponent.addAuditLogData(systemAuditLog,
				data.getFullName(),
				data.getGroups(),
				details,
				desc,
				true);

		iSystemAuditLogRepository.save(systemAuditLog);
		return "true";
	}

	@PreAuthorize("hasAuthority('CREATE_FILE')")
	@PostMapping(value = "/saveUploadFileListing")
	public List <String> saveUploadFileListing(
			@RequestParam(value="files") MultipartFile [] files,
			@RequestParam (value="fileManagement") String fileManagement, HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		ObjectMapper objectMapper = new ObjectMapper();
		List <FileManagementSaveDTO> fileDTO = new ArrayList<> ();
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			CollectionType collectionType = typeFactory.constructCollectionType(
					List.class, FileManagementSaveDTO.class);
			fileDTO = objectMapper.readValue(fileManagement, collectionType);
		} catch (Exception e) {
			log.error("FileManagementRestController.saveUploadFileListing : ", e);
		}
		List <String> uploadStatusList = new ArrayList<> ();
		int i =0;
		for(FileManagementSaveDTO f: fileDTO) {
			String uploadStatus = CommonConstants.SUCCESS;
			String ext = FilenameUtils.getExtension(f.getFile());
			if (!ext.equalsIgnoreCase("yml") && !ext.equalsIgnoreCase("txt")
					&& !ext.equalsIgnoreCase("doc") && !ext.equalsIgnoreCase("docx") && !ext.equalsIgnoreCase("pdf")
					&& !ext.equalsIgnoreCase("py") && !ext.equalsIgnoreCase("csv") && !ext.equalsIgnoreCase("log")
					&& !ext.equalsIgnoreCase("yaml") && !ext.equalsIgnoreCase("xml") && !ext.equalsIgnoreCase("j2") && !ext.equalsIgnoreCase("json")
					&& !ext.equalsIgnoreCase("gz") && !ext.equalsIgnoreCase("zip") && !ext.equalsIgnoreCase("tgz") && !ext.equalsIgnoreCase("tar")
					&& !ext.equalsIgnoreCase("bin") && !ext.equalsIgnoreCase("iso") && !ext.equalsIgnoreCase("out") && !ext.equalsIgnoreCase("tar.gz")) {
				uploadStatus = "invalidFileType";
				uploadStatusList.add(uploadStatus);
				continue;
			}

			boolean isActive = true;
			if (f.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE)) {
				isActive = false;
			}
			byte[] bytes;
			long fileSize = files[i].getSize();
			if (fileSize > CommonConstants.MAX_FILE_UPLOAD_SIZE) {
				uploadStatus = "fileSizeExceeded";
				uploadStatusList.add(uploadStatus);
			} else {
				String nameFile = "";
				try {
					bytes = files[i].getBytes();
					String[] arrNameFile = f.getFile().split("\\.");
					nameFile = arrNameFile[0] + "_version1." + arrNameFile[1];
					Path path = Paths.get(uploadDir + CommonConstants.PATH_DELIMITER + nameFile);
					Files.write(path, bytes);
					i = i + 1;

					String desc = "Upload new file";
					String details = MESSAGE_USER_DETAIL + data.getFullName() + " has uploaded " + nameFile + CommonConstants.DOTS;

					long millis=System.currentTimeMillis();
					java.sql.Date date=new java.sql.Date(millis);
					fileManagementService.createFileWithFilename(f.getName(), f.getDescription(), nameFile, data.getFullName(), date, isActive, false);

					SystemAuditLog systemAuditLog = new SystemAuditLog();
					CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
					commonMethodComponent.addAuditLogData(systemAuditLog,
							data.getFullName(),
							data.getGroups(),
							details,
							desc,
							true);

					uploadStatusList.add(uploadStatus);
					iSystemAuditLogRepository.save(systemAuditLog);
				} catch (IOException e) {
					log.error("FileManagementRestController.saveUploadFileListing : ", e);
					uploadStatus = "otherError";
					uploadStatusList.add(uploadStatus);
				}
			}

		}
		return uploadStatusList;
	}

	@GetMapping(value = "/downloadFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<byte[]> getFile(@RequestParam(value="filename") String filename) {
		byte[] fileBytes = null;
		try {
			fileBytes = Files.readAllBytes(Paths.get(uploadDir + CommonConstants.PATH_DELIMITER + filename));
		} catch (IOException e) {
			log.error("FileManagementRestController.getFile : ", e);
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.body(fileBytes);
	}

	@PostMapping(value = "/CheckValidateBeforeSave", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public FileManagementWrapper checkValidateBeforeSave(@RequestBody FileManagementWrapper fileManagement) {
		fileManagementService.checkMultipleFiles(fileManagement);
		return fileManagement;
	}

	@PostMapping("/get-file-by-id")
	public ResponseEntity<FileManagementDTO> getFileById(@RequestParam("fileId") long fileId) {
		log.info("Calling getFileById api");
		FileManagementDTO fileDto = new FileManagementDTO();
		try {
			FileManagement file = fileManagementService.getById(fileId);

			fileDto.setName(file.getName());
			fileDto.setVersion(file.getVersion());
			fileDto.setDescription(file.getDescription());
			fileDto.setFile(file.getFilename());
			fileDto.setActive(file.isActive());
			fileDto.setUpdatedAt(BEDateUtils.convertDateToStringByFormatOn(file.getModifiedDate() != null ? file.getModifiedDate() : file.getCreatedDate()));
			fileDto.setUpdatedBy(StringUtils.isNotBlank(file.getModifiedBy())? file.getModifiedBy() : file.getCreatedBy());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(fileDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(fileDto, HttpStatus.OK);
	}
}
