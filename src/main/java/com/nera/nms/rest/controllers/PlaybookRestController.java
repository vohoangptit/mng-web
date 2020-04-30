package com.nera.nms.rest.controllers;

import com.google.common.base.Strings;
import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.ApproveAndRejectDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.PlaybookDTO;
import com.nera.nms.dto.PlaybookHistoryDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.ENUM.PlaybookInputType;
import com.nera.nms.models.ENUM.StatusPlaybook;
import com.nera.nms.models.FileManagement;
import com.nera.nms.models.Playbook;
import com.nera.nms.models.PlaybookHistory;
import com.nera.nms.models.PlaybookInputHistory;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.services.FileManagementService;
import com.nera.nms.services.PlaybookService;
import com.nera.nms.services.WorkflowService;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.PageableUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * The type Playbook rest controller.
 */
@RestController
@RequestMapping("nera/playbook/api")
public class PlaybookRestController {

	/**
	 * The Logger.
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The constant MESSAGE_FAILED.
	 */
	private static final String MESSAGE_FAILED = "failed";

	/**
	 * The constant MESSAGE_DUPLICATED_NAME.
	 */
	private static final String MESSAGE_DUPLICATED_NAME =  "duplicated variableName: ";

	/**
	 * The constant MESSAGE_PLAYBOOK_INPUT.
	 */
	private static final String MESSAGE_PLAYBOOK_INPUT = " for PlaybookInput!";

	/**
	 * The constant MESSAGE_FILE.
	 */
	private static final String MESSAGE_FILE = "This file: ";

	/**
	 * The constant MESSAGE_WARNING_INPUT.
	 */
	public static final String MESSAGE_WARNING_INPUT = " is used in other input!";

	/**
	 * The constant MESSAGE_PLAYBOOK.
	 */
	private static final String MESSAGE_PLAYBOOK = "Playbook ";

	/**
	 * The Playbook service.
	 */
	private final PlaybookService playbookService;

	/**
	 * The System audit log repository.
	 */
	private final ISystemAuditLogRepository iSystemAuditLogRepository;

	/**
	 * The File management service.
	 */
	private final FileManagementService fileManagementService;

	/**
	 * The Workflow service.
	 */
	@Autowired
	private WorkflowService workflowService;

	/**
	 * Instantiates a new Playbook rest controller.
	 *
	 * @param playbookService           the playbook service
	 * @param iSystemAuditLogRepository the system audit log repository
	 * @param fileManagementService     the file management service
	 */
	public PlaybookRestController(PlaybookService playbookService, ISystemAuditLogRepository iSystemAuditLogRepository, FileManagementService fileManagementService) {
		this.playbookService = playbookService;
		this.iSystemAuditLogRepository = iSystemAuditLogRepository;
		this.fileManagementService = fileManagementService;
	}

	/**
	 * Gets list.
	 *
	 * @param perPage    the per page
	 * @param page       the page
	 * @param sortBy     the sort by
	 * @param orderBy    the order by
	 * @param query      the query
	 * @param createdBy  the created by
	 * @param approvedBy the approved by
	 * @param startDate  the start date
	 * @param endDate    the end date
	 * @return the list
	 */
	@GetMapping("/listing-and-filter")
	public ResponseEntity<PageableDTO> getList(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") int page,
			@RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
			@RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
			@RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
			@RequestParam(value = "createdBy[]", required = false) List<String> createdBy,
			@RequestParam(value = "approvedBy[]", required = false) List<String> approvedBy,
			@RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
			@RequestParam(value = "endDate", required = false, defaultValue = "") String endDate) {
		Page<PlaybookDTO> playbookDto;
		Page<Playbook> playbook = null; // result search
		PageableDTO result;
		// create pageable with client parameter
		Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);

		try {
			playbook = playbookService.findAllWithSearchString(pageable, query, createdBy, approvedBy, startDate,
					endDate);
		} catch (ParseException e) {
			logger.error("Exception: PlaybookRestController.getList ",e);
		}
		// map entity to dto
		assert playbook != null;
		playbookDto = playbook.map(PlaybookDTO::new);

		// identified number row and map to dto result
		AtomicLong numIncrement = new AtomicLong(1);
		List<PlaybookDTO> datas = playbookDto.getContent();
		datas.forEach(object -> object.setNo(numIncrement.getAndIncrement() + (page - 1)*10));
		result = PageableUtil.pageableMapper(playbookDto, pageable, datas);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Gets list approving.
	 *
	 * @param perPage   the per page
	 * @param page      the page
	 * @param sortBy    the sort by
	 * @param orderBy   the order by
	 * @param query     the query
	 * @param createdBy the created by
	 * @return the list approving
	 */
	@GetMapping("/listing-approved")
	public ResponseEntity<PageableDTO> getListApproving(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") int page,
			@RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
			@RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
			@RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
			@RequestParam(value = "createdBy[]", required = false) List<String> createdBy) {
		Page<PlaybookDTO> playbookDto;
		Page<Playbook> playbook; // result search
		PageableDTO result;
		// create pageable with client parameter
		Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);

		playbook = playbookService.findByStatusNew(pageable, query, createdBy);
		// map entity to dto
		playbookDto = playbook.map(PlaybookDTO::new);

		// identified number row and map to dto result
		AtomicLong numIncrement = new AtomicLong(1);
		List<PlaybookDTO> listPlaybook = playbookDto.getContent();
		listPlaybook.forEach(object -> object.setNo(numIncrement.getAndIncrement() + (page - 1)*10));
		result = PageableUtil.pageableMapper(playbookDto, pageable, listPlaybook);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Gets list created person.
	 *
	 * @return the list created person
	 */
	@GetMapping("/created-person")
	public ResponseEntity<List<String>> getListCreatedPerson() {

		List<String> datas = playbookService.getListCreatedPerson();

		return new ResponseEntity<>(datas, HttpStatus.OK);
	}

	/**
	 * Gets list created person to approve.
	 *
	 * @return the list created person to approve
	 */
	@GetMapping("/created-person-to-approve")
	public ResponseEntity<List<String>> getListCreatedPersonToApprove() {

		List<String> datas = playbookService.getListCreatedPersonToApprove();

		return new ResponseEntity<>(datas, HttpStatus.OK);
	}

	/**
	 * Gets list approved person.
	 *
	 * @return the list approved person
	 */
	@GetMapping("/approved-person")
	public ResponseEntity<List<String>> getListApprovedPerson() {

		List<String> datas = playbookService.getListApprovedPerson();

		return new ResponseEntity<>(datas, HttpStatus.OK);
	}

	/**
	 * Add playbook with detail response entity.
	 *
	 * @param body    the body
	 * @param request the request
	 * @return the response entity
	 */
	@PostMapping("/save")
	public ResponseEntity<ResultDTO> addPlaybookWithDetail(@RequestBody PlaybookDTO body, HttpServletRequest request) {
		ResultDTO result = new ResultDTO();
		result.setMess(MESSAGE_FAILED);
		UserProfileDto data;
		if (Strings.isNullOrEmpty(body.getName())) {
			result.setCode(400);
			result.setDetail("playbookName cannot be blank or null!");
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		} else {
			try {
				if (body.getPlaybookInput().size() > 1 && checkPlaybookExist(body, result)) {
					return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
				}
				data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
				body.setCreatedBy(data.getFullName());
				body.setCreatedDate(new Date());
				playbookService.savePlaybook(body);
				result.setCode(200);
				result.setMess(CommonConstants.SUCCESS);
			} catch(DataIntegrityViolationException ex) {
				result.setCode(400);
				result.setFieldName("playbookName");
				result.setDetail("playbookName has exists");
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			} catch (Exception e) {
				logger.error("Exception: PlaybookRestController.addPlaybookWithDetail ",e);
				result.setCode(500);
				result.setDetail("Internal Server Exception!");
				result.setDetail(e.getMessage());
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			SystemAuditLog systemAuditLog = new SystemAuditLog();
			String details = MESSAGE_PLAYBOOK + body.getName() + " has been created by " + data.getEmail();
			String desc = "Create Playbook";
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
			commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
					true);
			iSystemAuditLogRepository.save(systemAuditLog);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private boolean checkPlaybookExist(PlaybookDTO body, ResultDTO result) {
		for (int a = 0; a < body.getPlaybookInput().size(); a++) {
			for (int i = a + 1; i < body.getPlaybookInput().size(); i++) {
				if (body.getPlaybookInput().get(a).getVariable()
						.equals(body.getPlaybookInput().get(i).getVariable())) {
					result.setCode(400);
					result.setFieldName("variableName");
					result.setDetail(MESSAGE_DUPLICATED_NAME
							+ body.getPlaybookInput().get(i).getVariable() + MESSAGE_PLAYBOOK_INPUT);
					logger.error(MESSAGE_DUPLICATED_NAME + body.getPlaybookInput().get(i).getVariable()
							+ MESSAGE_PLAYBOOK_INPUT);
					return true;
				}
				if (body.getPlaybookInput().get(a).getType().equals(PlaybookInputType.FILE)
						&& body.getPlaybookInput().get(i).getType().equals(PlaybookInputType.FILE)
						&& body.getPlaybookInput().get(a).getValue()
						.equals(body.getPlaybookInput().get(i).getValue())) {
					result.setCode(400);
					result.setFieldName("value");
					result.setDetail(MESSAGE_FILE + body.getPlaybookInput().get(i).getValue()
							+ MESSAGE_WARNING_INPUT);
					logger.error(MESSAGE_FILE + body.getPlaybookInput().get(i).getValue()
							+ MESSAGE_WARNING_INPUT);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update playbook with detail response entity.
	 *
	 * @param body    the body
	 * @param request the request
	 * @return the response entity
	 */
	@PostMapping("/update")
	public ResponseEntity<ResultDTO> updatePlaybookWithDetail(@RequestBody PlaybookDTO body,
			HttpServletRequest request) {
		ResultDTO result = new ResultDTO();
		result.setMess(MESSAGE_FAILED);
		if (body.getId() == 0) {
			result.setCode(400);
			result.setDetail("Id is a required field !");
		} else if (StringUtils.isBlank(body.getName())) {
			result.setCode(400);
			result.setDetail("playbookName cannot be blank or null!");
		} else {
			try {
				UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
				// check duplicated file for playbook input
				if (body.getPlaybookInput().size() > 1 && checkPlaybookExist(body, result)) {
					return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
				}
				body.setModifiedBy(data.getFullName());
				body.setModifiedDate(new Date());
				playbookService.updatePlaybook(body, data.getFullName());
				result.setCode(200);
				result.setMess(CommonConstants.SUCCESS);
				SystemAuditLog systemAuditLog = new SystemAuditLog();
				String details = MESSAGE_PLAYBOOK + body.getName() + " has been updated by " + data.getEmail();
				String desc = "Update Playbook";
				CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
				commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
						true);
				iSystemAuditLogRepository.save(systemAuditLog);
			} catch (DataIntegrityViolationException ex) {
				result.setCode(400);
				result.setDetail("playbookName has exists");
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			} catch (Exception e) {
				logger.error("Exception: PlaybookRestController.updatePlaybookWithDetail ",e);
				result.setCode(500);
				result.setDetail("Internal Server Exception!");
				result.setDetail(e.getCause().getLocalizedMessage());
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Gets by id.
	 *
	 * @param id the id
	 * @return the by id
	 */
	@GetMapping("/get-by-id")
	public ResponseEntity<PlaybookDTO> getById(@RequestParam long id) {
		PlaybookDTO playbookDto = playbookService.getById(id);
		playbookDto.getPlaybookInput().forEach(e -> {
			if (e.getFileManagementId() != null) {
				e.setFileInfo(fileManagementService.getById(e.getFileManagementId()));
			}
		});
		logger.info("get detail Playbook by id");
		return new ResponseEntity<>(playbookDto, HttpStatus.OK);
	}

	/**
	 * Gets file name playbook.
	 *
	 * @param playbookId the playbook id
	 * @return the file name playbook
	 */
	@GetMapping("/get-file-name")
	public ResponseEntity<PlaybookDTO> getFileNamePlaybook(@RequestParam long playbookId) {
		PlaybookDTO playbookDto = playbookService.getById(playbookId);
		playbookDto.getPlaybookInput().forEach(e -> {
			if (e.getFileManagementId() != null) {
				e.setFileInfo(fileManagementService.getById(playbookId));
			}
		});
		logger.info("get detail Playbook by id");
		return new ResponseEntity<>(playbookDto, HttpStatus.OK);
	}

	/**
	 * Delete by id response entity.
	 *
	 * @param id      the id
	 * @param request the request
	 * @return the response entity
	 */
	@PostMapping("/delete-by-id")
	public ResponseEntity<ResultDTO> deleteById(@RequestParam long id, HttpServletRequest request) {
		ResultDTO result = new ResultDTO();
		UserProfileDto data;
		PlaybookDTO body = playbookService.getById(id);
		result.setMess(MESSAGE_FAILED);
		if (id == 0) {
			result.setCode(400);
			result.setDetail("Invalid Id!");
		} else {
			try {
				playbookService.deletePlaybookById(id);
				result.setCode(200);
				result.setMess(CommonConstants.SUCCESS);
				data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);

			} catch (DataIntegrityViolationException ex) {
				result.setCode(400);
				result.setDetail("Invalid Id!");
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			} catch (Exception e) {
				result.setCode(500);
				result.setDetail(e.getCause().getLocalizedMessage());
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			SystemAuditLog systemAuditLog = new SystemAuditLog();
			String details = MESSAGE_PLAYBOOK + body.getName() + " has been delete by " + data.getEmail();
			String desc = "Delete Playbook";
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
			commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
					true);
			iSystemAuditLogRepository.save(systemAuditLog);

		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Delete list by id.
	 *
	 * @param listId  the list id
	 * @param request the request
	 */
	@PostMapping("/delete-list-by-id")
	public void deleteListById(@RequestBody List<Long> listId, HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		listId.forEach(e -> {
			playbookService.deleteById(e);
			String details = "Playbook Id: " + e + " has been deleted by " + data.getEmail();
			String desc = "Delete Playbook";
			commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
					true);
			iSystemAuditLogRepository.save(systemAuditLog);
		});
	}

	/**
	 * Approve or reject playbook.
	 *
	 * @param listDTO the list dto
	 * @param request the request
	 */
// Aprrove or Reject list Playbook
	@PostMapping("/approved-or-rejected-playbook")
	public void approveOrRejectPlaybook(@RequestBody List<ApproveAndRejectDTO> listDTO, HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		StatusPlaybook status = listDTO.get(0).getStatus();
		listDTO.forEach(e -> {
			playbookService.approveOrRejectPlaybook(e, data.getFullName());
			String details = "Playbook Id: " + e.getId() + " has been " + status + " by " + data.getEmail();
			String desc = status + " Playbook";
			commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
					true);
			iSystemAuditLogRepository.save(systemAuditLog);
		});
	}

	/**
	 * Count playbook approved int.
	 *
	 * @param request the request
	 * @return the int
	 */
	@GetMapping("/count-approved-playbook")
	public int countPlaybookApproved(HttpServletRequest request) {
		int number = playbookService.countPlaybookAprroved();
		request.getSession(true).setAttribute("playbookApproveMenu", "Playbook Approved ("+ number +")");
		return number;
	}

	/**
	 * Send approval.
	 *
	 * @param id      the id
	 * @param request the request
	 */
	@PostMapping("/send-approval-playbook-by-id")
	public void sendApproval(@RequestParam long id, HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		Playbook entity = 
				playbookService.sendApprovalPlaybook(id);
		String details = "Playbook Name: " + entity.getName() + " has been send to approval by " + data.getEmail();
		String desc = "Send Playbook to Approval";
		commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
				true);
		iSystemAuditLogRepository.save(systemAuditLog);	
	}

	/**
	 * Gets list approved and active.
	 *
	 * @param perPage    the per page
	 * @param page       the page
	 * @param sortBy     the sort by
	 * @param orderBy    the order by
	 * @param query      the query
	 * @param approvedBy the approved by
	 * @return the list approved and active
	 */
	@GetMapping("/listing-approved-active")
	public ResponseEntity<PageableDTO> getListApprovedAndActive(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") int page,
			@RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
			@RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
			@RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
			@RequestParam(value = "approvedBy[]", required = false) List<String> approvedBy) {
		Page<PlaybookDTO> playbookDto;
		Page<Playbook> playbook; // result search
		PageableDTO result;
		// create pageable with client parameter
		Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);

		playbook = playbookService.findByStatusActiveAndApproved(pageable, query, approvedBy);
		// map entity to dto
		playbookDto = playbook.map(PlaybookDTO::new);

		// identified number row and map to dto result
		AtomicLong numIncrement = new AtomicLong(1);
		List<PlaybookDTO> data = playbookDto.getContent();
		data.forEach(object -> object.setNo(numIncrement.getAndIncrement() + (page - 1)));
		result = PageableUtil.pageableMapper(playbookDto, pageable, data);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Gets list approved and active.
	 *
	 * @param searchString the search string
	 * @return the list approved and active
	 */
	@PostMapping("/listing-playbook-approved")
	public ResponseEntity<List<PlaybookDTO>> getListApprovedAndActive(@RequestParam String searchString) {
		List<Playbook> playbooks = playbookService.findByPlaybookApproved(searchString);
		List<PlaybookDTO> playbookDTOS = playbooks.stream().map(PlaybookDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(playbookDTOS, HttpStatus.OK);
	}

	/**
	 * History listing workflow map.
	 *
	 * @param id the id
	 * @return the map
	 */
	@GetMapping("/history-listing")
	@PreAuthorize("hasAuthority('VIEW_HISTORY_PLAYBOOK')")
	public Map<String, Object> historyListingWorkflow(@RequestParam long id) {
		Map<String, Object> mapHistoryPlaybook = new ConcurrentHashMap<>();
		try {
			PlaybookDTO plDTO = playbookService.getById(id);
			mapHistoryPlaybook.put("name", plDTO.getName());
			mapHistoryPlaybook.put("playbookFile", plDTO.getFileName());
			mapHistoryPlaybook.put("version", plDTO.getVersion() == 0 ? 1 : plDTO.getVersion());
			mapHistoryPlaybook.put("isSendApproval", plDTO.isSendToApproved());
			if(StringUtils.isNotBlank(plDTO.getModifiedBy())) {
				mapHistoryPlaybook.put("updateBy", plDTO.getModifiedBy());
				mapHistoryPlaybook.put("updateAt", BEDateUtils.convertDateToStringByFormatOn(plDTO.getModifiedDate()));
			} else {
				mapHistoryPlaybook.put("updateBy", plDTO.getCreatedBy());
				mapHistoryPlaybook.put("updateAt", BEDateUtils.convertDateToStringByFormatOn(plDTO.getCreatedDate()));
			}
			List<PlaybookHistory> playbookHistories = playbookService.getListPlayBookHistoryById(id);
			List<PlaybookHistoryDTO> workflowHistoryDTOS = playbookHistories.stream().map(PlaybookHistoryDTO::new).collect(Collectors.toList());
			AtomicLong numIncrement = new AtomicLong();
			workflowHistoryDTOS.forEach(object -> object.setNo(numIncrement.incrementAndGet()));
			mapHistoryPlaybook.put("listPlaybookHistory", workflowHistoryDTOS);
		} catch (Exception e) {
			logger.error("Exception: PlaybookRestController.historyListingWorkflow ", e);
		}
		return mapHistoryPlaybook;
	}


	/**
	 * Gets history detail.
	 *
	 * @param id the id
	 * @return the history detail
	 */
	@GetMapping("/history-detail")
	@PreAuthorize("hasAuthority('VIEW_HISTORY_PLAYBOOK')")
	public ResponseEntity<PlaybookHistoryDTO> getHistoryDetail(@RequestParam long id) {
		PlaybookHistory playbookHistory = playbookService.getPlaybookHistoryById(id);
		return new ResponseEntity<>(new PlaybookHistoryDTO(playbookHistory), HttpStatus.OK);
	}

	/**
	 * Check version file string.
	 *
	 * @param historyId the history id
	 * @return the string
	 */
	@GetMapping("/check-playbook")
	public String checkVersionFile(@RequestParam long historyId) {
		PlaybookHistory playbookHistory = playbookService.getPlaybookHistoryById(historyId);
		try {
			if (workflowService.checkListWorkflowOperator(playbookHistory.getPlaybookId())) {
				return "Cannot restore the playbook to the previous version because the current version is used by the workflow.";
			}
			for (PlaybookInputHistory item : playbookHistory.getPlaybookInput()) {
				if (item.getFileManagementId() != null) {
					FileManagement file = fileManagementService.getById(item.getFileManagementId());
					if (file != null && file.getVersion() > item.getFileVersion()) {
						return "Cannot restore the playbook to the previous version because the file(s) are used in this playbook which are not in latest version or removed.";
					}
				}
			}
			if (playbookHistory.getId() != 0) {
				Playbook playbook = playbookService.getPlaybookById(playbookHistory.getPlaybookId());
				return playbook.getStatus().toString();
			}
		} catch (Exception e) {
			logger.error("Exception: PlaybookRestController.checkVersionFile ", e);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Restore playbook boolean.
	 *
	 * @param historyId the history id
	 * @param request   the request
	 * @return the boolean
	 */
	@PostMapping("/restore-playbook")
	@PreAuthorize("hasAuthority('RESTORE_HISTORICAL_PLAYBOOK')")
	public boolean restorePlaybook(@RequestParam long historyId, HttpServletRequest request) {
		try {
			UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
			PlaybookHistory playbookHistory = playbookService.getPlaybookHistoryById(historyId);
			Playbook playbook = playbookService.getPlaybookById(playbookHistory.getPlaybookId());
			playbookService.restorePlaybook(playbookHistory, data.getFullName(), playbook);

			SystemAuditLog systemAuditLog = new SystemAuditLog();
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
			String details = "Playbook Name: " + playbook.getName() + " has been restored by " + data.getEmail();
			String desc = "Restore Playbook";
			commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
					true);
			iSystemAuditLogRepository.save(systemAuditLog);
		} catch (Exception e) {
			logger.error("Exception: PlaybookRestController.restorePlaybook ", e);
			return false;
		}
		return true;
	}

	/**
	 * Is used by workflow boolean.
	 *
	 * @param id the id
	 * @return the boolean
	 */
	@PostMapping("/check-used-by-workflow")
	public boolean isUsedByWorkflow(@RequestParam("id") Long id) {
		return workflowService.checkListWorkflowOperator(id);
	}
}