package com.nera.nms.rest.controllers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;
import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.WorkflowConstants;
import com.nera.nms.dto.JobInputDTO;
import com.nera.nms.dto.PageableDTO;
import com.nera.nms.dto.ResultDTO;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.dto.WorkflowDTO;
import com.nera.nms.dto.WorkflowHistoryDTO;
import com.nera.nms.dto.WorkflowOperatorHistoryDTO;
import com.nera.nms.models.ENUM;
import com.nera.nms.models.JobInput;
import com.nera.nms.models.Playbook;
import com.nera.nms.models.PlaybookInput;
import com.nera.nms.models.PlaybookOutput;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.Workflow;
import com.nera.nms.models.WorkflowHistory;
import com.nera.nms.models.WorkflowOperator;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.repositories.JobInputRepository;
import com.nera.nms.repositories.WorkflowRepository;
import com.nera.nms.services.FileStorageService;
import com.nera.nms.services.JobInputService;
import com.nera.nms.services.JobService;
import com.nera.nms.services.PlaybookService;
import com.nera.nms.services.WorkflowService;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.PageableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/nera/api")
@PropertySource("classpath:messages.properties")
public class WorkflowRestController {

	@Autowired
	private WorkflowRepository workflowRepository;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String CONST_WORKFLOW = "(Workflow: ";

	private static final String CONST_LOG_ERROR = "WorkflowRestController.exportByCreatedDate ";

	private static final String FORMAT = "format";

	@Autowired
	private JobInputRepository jobInputRepository;

	@Autowired
	ISystemAuditLogRepository iSystemAuditLogRepository;
	/**
	 * Cache for all images.
	 */
	@Autowired
	private IUserRepository userRepository;

	protected Map<String, Image> imageCache = new ConcurrentHashMap<>();
	/**
	 * 
	 */
	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private JobService jobService;

	@Autowired
	private JobInputService jobInputService;

	@Autowired
	private PlaybookService playbookService;

	@Autowired
	private FileStorageService fileStorageService;

	private SAXParserFactory parserFactory = SAXParserFactory.newInstance();

	@Value("${file.image-dir}")
	private String imageDir;

	@PostMapping("/save-workflow")
 	public ResponseEntity<ResultDTO> saveWorkFlow(@RequestBody WorkflowDTO body, HttpServletRequest request)
 	{
		ResultDTO result = new ResultDTO();
		try {
			if(workflowService.findByName(body.getName(), body.getId())) {
				result.setCode(HttpStatus.BAD_REQUEST.value());
				result.setMess("The same workflow name already exists");
			} else {
				UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
				String nameCreatedBy =  userProfileDto.getFullName();
				if (workflowService.saveWorkflow(nameCreatedBy, body)) {
					SystemAuditLog systemAuditLog = new SystemAuditLog();
					CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
					String details = CONST_WORKFLOW + body.getName() + ") has been created by " + nameCreatedBy;
					commonMethodComponent.addAuditLogData(systemAuditLog,
							nameCreatedBy,
							userProfileDto.getGroups(),
							details,
							"Create Workflow Successful",
							true);
					iSystemAuditLogRepository.save(systemAuditLog);
					result.setCode(HttpStatus.OK.value());
					result.setMess("Create workflow success");
				}
			}
		} catch(Exception e) {
			logger.error("Exception: WorkflowRestController.saveWorkFlow ", e);
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			result.setMess("Create workflow fail");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
 		return new ResponseEntity<>(result, HttpStatus.OK);
 	}

	@PostMapping("/update-workflow")
	public ResponseEntity<ResultDTO> updateWorkFlow(@RequestBody WorkflowDTO body, HttpServletRequest request)
	{
		ResultDTO result = new ResultDTO();
		try {
			if(workflowService.findByName(body.getName(), body.getId())) {
				result.setCode(HttpStatus.BAD_REQUEST.value());
				result.setMess("The same workflow name already exists");
			} else {
				Workflow wf = workflowService.findByIdWorkflow(body.getId());

				workflowService.saveWorkflowHistory(wf);

				String nameWorkflow = wf.getName();
				if(body.getJobInput() != null) {
					deleteJobInput(body, wf);
				}
				UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
				String nameCreatedBy =  userProfileDto.getFullName();
				List<WorkflowOperator> listOperators = new ArrayList<>();
				listOperators.addAll(wf.getWorkflowOperator());
				if (workflowService.saveWorkflow(nameCreatedBy, body)) {
					workflowService.deleteWorkflowOperator(listOperators);
					if(!StringUtils.equals(nameWorkflow,body.getName())) {
						jobService.updateJobManagementByWorkflow(body.getId(), body.getName());
					}
					SystemAuditLog systemAuditLog = new SystemAuditLog();
					CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
					String details = CONST_WORKFLOW + body.getName() + ") has been update by " + nameCreatedBy;
					commonMethodComponent.addAuditLogData(systemAuditLog,
							nameCreatedBy,
							userProfileDto.getGroups(),
							details,
							"Update Workflow Successful",
							true);
					iSystemAuditLogRepository.save(systemAuditLog);
					result.setCode(HttpStatus.OK.value());
					result.setMess("Update workflow success");
				}
			}
		} catch(Exception e) {
			logger.error("Exception: WorkflowRestController.updateWorkFlow ", e);
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			result.setMess("Update workflow fail");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private void deleteJobInput(WorkflowDTO body, Workflow wf) {
		if(wf.getJobInput() != null) {
			List<Long> listIdJob = new ArrayList<>();
			wf.getJobInput().forEach(n -> listIdJob.add(n.getId()));
			body.getJobInput().forEach(b -> listIdJob.removeIf(o -> o==b.getId()));
			for(Long item : listIdJob) {
				jobInputService.deleteJobById(item);
			}
		}
	}

	@PostMapping("/load-workflow")
 	public ResponseEntity<Workflow> loadWorkFlow(@RequestParam long id) {
 		Workflow optionalWorkflow = workflowRepository.findOneById(id);
 		return new ResponseEntity<>(optionalWorkflow, HttpStatus.OK);
 	}

	@PostMapping("/load-history-workflow")
	public ResponseEntity<WorkflowHistoryDTO> loadHistoryWorkflow(@RequestParam long id) {
		WorkflowHistory optionalWorkflow = workflowService.getWorkflowHistoryById(id);
		List<WorkflowOperatorHistoryDTO> workflowOperatorHistoryDTOs = optionalWorkflow.getWorkflowOperatorHistories().stream().map(WorkflowOperatorHistoryDTO::new).collect(Collectors.toList());
		WorkflowHistoryDTO workflowHistoryDTO = new WorkflowHistoryDTO(optionalWorkflow);
		workflowHistoryDTO.setWorkflowOperatorHistories(workflowOperatorHistoryDTOs);
		return new ResponseEntity<>(workflowHistoryDTO, HttpStatus.OK);
	}


	@PostMapping("/save-job-input")
 	public ResponseEntity<Object> saveJobInput(@RequestBody List<JobInputDTO> body)
 	{
		for (JobInputDTO jobInputDTO : body) {
			int del = jobInputDTO.getDel();
			if (del == 1) {
				long id = jobInputDTO.getId();
				jobInputRepository.deletebyId(id);
			} else {
				String variable = jobInputDTO.getVariable();
				long id = jobInputDTO.getId();
				String value = jobInputDTO.getValue();
				int type = jobInputDTO.getType();
				JobInput entity = new JobInput();
				entity.setId(id);
				entity.setType(type);
				entity.setVariable(variable);
				entity.setValue(value);
				jobInputRepository.save(entity);
			}
		}
 		return new ResponseEntity<>(HttpStatus.OK);
 	}

	@PostMapping("/get-all-user-approve")
	public ResponseEntity<List<String>> getAllGroupUserApprove (@RequestParam String approved)
	{
		List<Object[]> listGroup = userRepository.findAllGroupApprovedPlaybook();
		List<String> lstDataUserGroup = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(listGroup)) {
			for(Object[] item : listGroup) {
				lstDataUserGroup.addAll(userRepository.findAllUserApprovedPlaybook(Long.parseLong(item[0].toString()), approved));
			}
			lstDataUserGroup = lstDataUserGroup.stream()
					.distinct()
					.collect(Collectors.toList());
		}
		return new ResponseEntity<>(lstDataUserGroup,HttpStatus.OK);
	}

	@PostMapping("/get-job-by-id")
	public ResponseEntity<List<JobInputDTO>> getJobInputById(@RequestParam long id)
	{
		List<JobInput> jobInputList = jobInputService.findJobByWorkflowId(id);
		List<JobInputDTO> datadto = new ArrayList<>();
		for (JobInput i : jobInputList) {
			JobInputDTO dto = new JobInputDTO();
			dto.setId(i.getId());
			dto.setVariable(i.getVariable());
			dto.setType(i.getType());
			dto.setDel(0);
			datadto.add(dto);
		}
		return new ResponseEntity<>(datadto, HttpStatus.OK);
	}

	@GetMapping(
			value = "/get-image-workflow",
			produces = MediaType.IMAGE_PNG_VALUE
	)
	@ResponseBody
	public String getImageWithMediaType(@RequestParam long id) throws IOException {
		Workflow wf = workflowService.findByIdWorkflow(id);
		InputStream is = new FileInputStream(imageDir + CommonConstants.PATH_DELIMITER + wf.getName() + CommonConstants.DOTS + CommonConstants.PNG);
		return Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
	}

	@GetMapping(
			value = "/get-image-workflow-by-name",
			produces = MediaType.IMAGE_PNG_VALUE
	)
	@ResponseBody
	public String getImageWithMediaType(@RequestParam String name) throws IOException {
		InputStream is = new FileInputStream(imageDir + CommonConstants.PATH_DELIMITER + name + CommonConstants.DOTS + CommonConstants.PNG);
		return Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
	}

	@GetMapping("/download/imageWorkflow")
	@ResponseBody
	public ResponseEntity<Object> getFile(@RequestParam String fileName, HttpServletRequest request) {
		String fileNameFormat = fileName + CommonConstants.DOTS + CommonConstants.PNG;
		// Load file as Resource
		Resource resource = fileStorageService.loadImageAsResource(fileNameFormat);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}
		assert contentType != null;
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameFormat + "\"")
				.body(resource);
	}

    @PostMapping("/export-image-from-workflow")
    public ResponseEntity<Object> exportByCreatedDate(HttpServletRequest request,
    HttpServletResponse response) {
    	try
		{
			if (request.getContentLength() < WorkflowConstants.MAX_REQUEST_SIZE)
			{
				handleRequest(request, response);
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
			}
		}
		catch (OutOfMemoryError e)
		{
			logger.error(CONST_LOG_ERROR, e);
			final Runtime r = Runtime.getRuntime();
			logger.info("r.freeMemory() = {}", r.freeMemory() / 1024.0 / 1024);
			logger.info("r.totalMemory() = {}", r.totalMemory() / 1024.0 / 1024);
			logger.info("r.maxMemory() = {}", r.maxMemory() / 1024.0 / 1024);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		catch (Exception e)
		{
			logger.error(CONST_LOG_ERROR, e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/create-image")
    public ResponseEntity<Object> renderXml(HttpServletRequest request,
    HttpServletResponse response) {
    	String url = StringUtils.EMPTY;
    	try
		{
			if (request.getContentLength() < WorkflowConstants.MAX_REQUEST_SIZE)
			{
				String format = request.getParameter(FORMAT);
				String fname = request.getParameter("filename");
				int w = Integer.parseInt(request.getParameter("w"));
				int h = Integer.parseInt(request.getParameter("h"));
				String tmp = request.getParameter("bg");
				String xml = getRequestXml(request);

				Color bg = (tmp != null) ? mxUtils.parseColor(tmp) : null;

				// Checks parameters
				if (w > 0 && h > 0 && w * h < WorkflowConstants.MAX_AREA && format != null && xml != null && xml.length() > 0)
				{
					// Allows transparent backgrounds only for PNG
					bg = setBackgroundImage(format, bg);

					fname = setFileNameXML(format, fname);

					url = request.getRequestURL().toString();

					// Writes response
					BufferedImage image = mxUtils.createBufferedImage(w, h, bg);

					if (image != null)
					{
						File file = new File(imageDir);
						checkFileExistsAndCreateFile(file);
						Graphics2D g2 = image.createGraphics();
						mxUtils.setAntiAlias(g2, true, true);
						renderXml(xml, createCanvas(url, g2));
						ImageIO.write(image, CommonConstants.PNG, new File(imageDir + '/' + fname + CommonConstants.DOTS + CommonConstants.PNG));
						url = imageDir + CommonConstants.PATH_DELIMITER + fname + CommonConstants.DOTS + CommonConstants.PNG;
					}
					saveAuditImage(request, fname);
				}
				else
				{
					response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
				}
			}
		}
		catch (OutOfMemoryError e)
		{
			logger.error("WorkflowRestController.createImageByCreatedDate ", e);
			final Runtime r = Runtime.getRuntime();
			logger.info("r.freeMemory() = {}", r.freeMemory() / 1024.0 / 1024);
			logger.info("r.totalMemory() = {}", r.totalMemory() / 1024.0 / 1024);
			logger.info("r.maxMemory() = {}", r.maxMemory() / 1024.0 / 1024);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		catch (Exception e)
		{
			logger.error("WorkflowRestController.createImageByCreatedDate ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
        return new ResponseEntity<>(url,HttpStatus.OK);
    }

	private void checkFileExistsAndCreateFile(File file) {
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	private String setFileNameXML(String format, String fname) {
		if (fname != null && fname.toLowerCase().endsWith(".xml"))
		{
			fname = fname.substring(0, fname.length() - 4) + format;
		}
		return fname;
	}

	private Color setBackgroundImage(String format, Color bg) {
		if (bg == null && !format.equals(CommonConstants.PNG)) {
			bg = Color.WHITE;
		}
		return bg;
	}

	private void saveAuditImage(HttpServletRequest request, String fname) {
		if(StringUtils.isNotBlank(fname)) {
			UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
			String nameCreatedBy = userProfileDto.getFullName();
			SystemAuditLog systemAuditLog = new SystemAuditLog();
			CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
			String details = "(" + fname + ") has been exported by " + nameCreatedBy;
			commonMethodComponent.addAuditLogData(systemAuditLog,
					nameCreatedBy,
					userProfileDto.getGroups(),
					details,
					"Export file to image successful",
					true);
			iSystemAuditLogRepository.save(systemAuditLog);
		}
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException, DocumentException {
		// Parses parameters
		String format = request.getParameter(FORMAT);
		String fName = request.getParameter("filename");
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
		String dateFormat = sdf.format(new Date());
		Long workflowId = Long.parseLong(request.getParameter("wfId"));
		int w = 560;
		int h = 280;
		String tmp = request.getParameter("bg");
		String xml = getRequestXml(request);
		Color bg = (tmp != null) ? mxUtils.parseColor(tmp) : null;
		String filePdfName = "";
		String wfName = "";
		// Checks parameters
//		if (w > 0 && h > 0 && w * h < WorkflowConstants.MAX_AREA && format != null && xml != null && xml.length() > 0)
//		{ }
		// Allows transparent backgrounds only for PNG
		bg = setBackgroundImage(format, bg);

		if (fName != null)
		{
			fName = fName.substring(0, fName.length() - 4)  + '_' + dateFormat;
		}
		String url = request.getRequestURL().toString();

		// Writes response

		BufferedImage image = mxUtils.createBufferedImage(w, h, bg);

		if (image != null) {
			Graphics2D g2 = image.createGraphics();
			mxUtils.setAntiAlias(g2, true, true);
			renderXml(xml, createCanvas(url, g2));

			assert format != null;
			ImageIO.write(image, CommonConstants.PNG, new File(imageDir + '/' + fName + CommonConstants.DOTS + CommonConstants.PNG));
			url = imageDir + CommonConstants.PATH_DELIMITER + fName + CommonConstants.DOTS + CommonConstants.PNG;
			Workflow wf = workflowService.findByIdWorkflow(workflowId);
			String workflowName = "Workflow Name: " + wf.getName();
			int ver = wf.getVersion() == 0 ? 1 : wf.getVersion();
			String version = "version: " + ver;
			String createdBy = "Created By: " + wf.getCreatedBy();
			String description = "Description: " + wf.getDescription();
			wfName = wf.getName();
			filePdfName = imageDir+"/" +wf.getName()+".pdf";
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(filePdfName));
			document.open();

			Font font2 = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
			Paragraph paragraph2 = new Paragraph();
			paragraph2.setLeading(0, 1.2f);
			paragraph2.setAlignment(Element.ALIGN_LEFT);
			paragraph2.setSpacingAfter(10);
			paragraph2.add(new Chunk("Nera-NMS System", font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk(workflowName, font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk(version, font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk(createdBy, font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk(description, font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk("I.  Overall Workflow", font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(new Chunk("<Exported overall workflow over here>", font2));
			paragraph2.add(Chunk.NEWLINE);
			paragraph2.add(Chunk.NEWLINE);
			document.add(paragraph2);


			com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(url);
			img.setScaleToFitHeight(true);
			document.add(img);
			document.newPage();

			Font font3 = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
			Paragraph paragraph3 = new Paragraph();
			paragraph3.setLeading(0, 1.2f);
			paragraph3.setAlignment(Element.ALIGN_CENTER);
			paragraph3.setSpacingAfter(10);

			paragraph3.add(new Chunk("II.  GLOBAL VARIABLES (JOB INPUT)", font3));
			paragraph3.add(Chunk.NEWLINE);
			document.add(paragraph3);
			PdfPTable table = new PdfPTable(3);
			table.setWidths(new float[]{1,3,7});
			addTableHeader(table, new String[]{"S/N","Input Type","Variable"});
			addRows(table, wf.getJobInput());
			document.add(table);

			document.newPage();
			Paragraph paragraph = new Paragraph();
			paragraph.setLeading(0, 1.2f);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.setSpacingAfter(10);
			paragraph.add(new Chunk("III.  PLAYBOOKS", font3));
			paragraph.add(Chunk.NEWLINE);
			document.add(paragraph);
			PdfPTable tablePlaybook = new PdfPTable(3);
			tablePlaybook.setWidths(new float[]{1,3,7});
			addTableHeader(tablePlaybook, new String[]{"S/N","Playbook Name","Properties"});

			int index=0;
			for(WorkflowOperator item : wf.getWorkflowOperator()) {
				if(StringUtils.containsIgnoreCase(item.getContentXML(),"<Playbook ")) {
					if(index==0) {
						tablePlaybook.addCell("<S/N>");
						tablePlaybook.addCell("<Name of the Playbook>");
						tablePlaybook.addCell("<The detail properties of the Playbook which include \\n - Playbook Input assignment \\n - Playbook Output>");
					}
					Pattern pattern = Pattern.compile("pbid=\"(\\d{1,})\"");
					Matcher matcher = pattern.matcher(item.getContentXML());
					if(matcher.find()) {
						PdfPTable tableInput = new PdfPTable(3);
						PdfPTable tableOutput = new PdfPTable(2);
						Playbook pl = playbookService.getPlaybookById(Long.parseLong(matcher.group(1)));

						tablePlaybook.addCell(String.valueOf(index+1));
						tablePlaybook.addCell(pl.getName());

						PdfPCell pdfPCell = new PdfPCell();
						Paragraph elementInput = new Paragraph();
						elementInput.add(new Chunk("Playbook Input Assignment", font3));
						elementInput.add(Chunk.NEWLINE);
						pdfPCell.addElement(elementInput);
						addTableHeader(tableInput, new String[]{"Variable","Assigned Value","Mandatory"});
						addRowsInput(tableInput, pl.getPlaybookInput(), pl.getName());
						pdfPCell.addElement(tableInput);

						Paragraph elementOutput = new Paragraph();
						elementOutput.add(Chunk.NEWLINE);
						elementOutput.add(new Chunk("Playbook Output", font3));
						elementOutput.add(Chunk.NEWLINE);
						pdfPCell.addElement(elementOutput);
						addTableHeader2(tableOutput, new String[]{"Variable Name","Default Value"});
						addRowsOutput(tableOutput, pl.getPlaybookOutput(), pl.getName());
						pdfPCell.addElement(tableOutput);

						tablePlaybook.addCell(pdfPCell);
						document.add(tablePlaybook);
						index++;
					}
				}
			}
			document.newPage();
			Paragraph paragraph4 = new Paragraph();
			paragraph4.setLeading(0, 1.2f);
			paragraph4.setAlignment(Element.ALIGN_CENTER);
			paragraph4.setSpacingAfter(10);
			paragraph4.add(new Chunk("IV.  Switch Cases", font3));
			paragraph4.add(Chunk.NEWLINE);
			document.add(paragraph4);
			PdfPTable tableSwitchCases = new PdfPTable(3);
			tableSwitchCases.setWidths(new float[]{1,3,7});
			addTableHeader(tableSwitchCases, new String[]{"S/N","Switch case Name","Properties"});
			int indexSw=0;
			for(WorkflowOperator item : wf.getWorkflowOperator()) {
				if(StringUtils.containsIgnoreCase(item.getContentXML(),"<Condition ")) {
					if(indexSw==0) {
						tableSwitchCases.addCell("<S/N>");
						tableSwitchCases.addCell("<The Name of Switch Cases in the workflow>");
						tableSwitchCases.addCell("<The detail properties of the switch case which includes to the conditions to the specific operators (playbook, switch cases, approval & end)>");
					}
					indexSw++;
					Pattern patternLabel = Pattern.compile("label=\"(.*?)\"");
					Matcher matcherLabel = patternLabel.matcher(item.getContentXML());
					matcherLabel.find();
					String nameSwitchCase = matcherLabel.group(1);
					tableSwitchCases.addCell(String.valueOf(indexSw));
					tableSwitchCases.addCell(nameSwitchCase);

					PdfPCell pdfPCell = new PdfPCell();

					Pattern pattern5 = Pattern.compile("conditionPlaybook(\\d*)_\\d+=\"(.*?)\"");
					Matcher matcher5 = pattern5.matcher(item.getContentXML());
					while(matcher5.find()) {
						Paragraph pharElements = new Paragraph();
						pharElements.add(new Chunk("Switch Case to " + matcher5.group(2), font3));
						pharElements.add(Chunk.NEWLINE);
						pdfPCell.addElement(pharElements);
						String key = matcher5.group(1);

						PdfPTable tableCondition = new PdfPTable(3);
						addTableHeader(tableCondition, new String[]{"Parameter Name","Condition","Value"});
						addRowsCondition(tableCondition, item.getContentXML(), nameSwitchCase, key);
						pdfPCell.addElement(tableCondition);
					}
					tableSwitchCases.addCell(pdfPCell);
					document.add(tableSwitchCases);
				}
			}
			document.newPage();
			Paragraph paragraph5 = new Paragraph();
			paragraph5.setLeading(0, 1.2f);
			paragraph5.setAlignment(Element.ALIGN_CENTER);
			paragraph5.setSpacingAfter(10);
			paragraph5.add(new Chunk("V.  APPROVAL", font3));
			paragraph5.add(Chunk.NEWLINE);
			document.add(paragraph5);
			PdfPTable tableApproved = new PdfPTable(3);
			tableApproved.setWidths(new float[]{1,3,7});
			addTableHeader(tableApproved, new String[]{"S/N","Approval Name","Properties"});
			int indexApproval=0;
			for(WorkflowOperator item : wf.getWorkflowOperator()) {
				if(StringUtils.containsIgnoreCase(item.getContentXML(),"<Approval ")) {
					if(indexApproval==0) {
						tableApproved.addCell("<S/N>");
						tableApproved.addCell("<Name of the approval>");
						tableApproved.addCell("<The approver name>");
					}
					indexApproval++;
					Pattern patternApprover = Pattern.compile("approver=\"(.*?)\"");
					Matcher matcherApproval = patternApprover.matcher(item.getContentXML());
					matcherApproval.find();
					String approval = matcherApproval.group(1);
					Pattern patternLabel = Pattern.compile("label=\"(.*?)\"");
					Matcher matcherLabel = patternLabel.matcher(item.getContentXML());
					matcherLabel.find();
					addRowsApproval(tableApproved, matcherLabel.group(1), indexApproval, approval);
					document.add(tableApproved);
				}
			}
			document.close();
		}
		saveAuditImage(request, wfName);
		if (wfName != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + wfName +".pdf" + "\"; filename*=UTF-8''" + wfName+".pdf");
		}
		response.setContentType("application/pdf");
		InputStream is = new FileInputStream(new File(filePdfName));
		org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		response.flushBuffer();
	}

	private void addTableHeader2(PdfPTable table, String[] arrStr) {
		Stream.of(arrStr[0],arrStr[1]).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	private void addTableHeader(PdfPTable table, String[] arrStr) {
		Stream.of(arrStr[0],arrStr[1],arrStr[2]).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	private void addRows(PdfPTable table, List<JobInput> listJobInput) {
		for(int n=0; n<listJobInput.size(); n++) {
			if(n==0) {
				table.addCell("<S/N>");
				table.addCell("<Input Type of the global variables>");
				table.addCell("<variables name of the global variables>");
			}
			table.addCell(String.valueOf(n+1));
			table.addCell(listJobInput.get(n).getType() == 0 ? "File" : "Text");
			table.addCell(listJobInput.get(n).getVariable());
		}
	}

	private void addRowsCondition(PdfPTable table, String conditionSwitchCase, String nameSwitchCase, String keyRow) {
		table.addCell("<Parameter Name of the switch case " + nameSwitchCase+">");
		table.addCell("<The comparison operator>");
		table.addCell("<The value to compare>");

		Pattern pattern = Pattern.compile("paramvalue"+keyRow+"_\\d+=\"(.*?)\"");
		Matcher matcher = pattern.matcher(conditionSwitchCase);

		Pattern pattern2 = Pattern.compile(" value"+keyRow+"_\\d+=\"(.*?)\"");
		Matcher matcher2 = pattern2.matcher(conditionSwitchCase);

		Pattern pattern3 = Pattern.compile("conditionkey"+keyRow+"_\\d+=\"(.*?)\"");
		Matcher matcher3 = pattern3.matcher(conditionSwitchCase);

		while(matcher.find() && matcher2.find() && matcher3.find()) {
			table.addCell(matcher.group(1));
			table.addCell(matcher3.group(1));
			table.addCell(matcher2.group(1));
		}
	}

	private void addRowsInput(PdfPTable table, List<PlaybookInput> playbookInputs, String playbookName) {
		for(int n=0; n<playbookInputs.size(); n++) {
			if(n==0) {
				table.addCell("<Input variable name of the " + playbookName + ">");
				table.addCell("<Assigned value of the " + playbookName + " in the workflow>");
				table.addCell("<Mandatory checking>");
			}
			table.addCell(playbookInputs.get(n).getVariable());
			table.addCell(playbookInputs.get(n).getValue());
			table.addCell(playbookInputs.get(n).isMandatory() ? "Yes" : "No");
		}
	}

	private void addRowsOutput(PdfPTable table, List<PlaybookOutput> playbookOutputs, String playbookName) {
		for(int n=0; n<playbookOutputs.size(); n++) {
			if(n==0) {
				table.addCell("<Output variable name of the "+playbookName+">");
				table.addCell("<Default value of the output variables>");
			}
			table.addCell(playbookOutputs.get(n).getVariable());
			table.addCell(playbookOutputs.get(n).getValue());
		}
	}

	private void addRowsApproval(PdfPTable table, String approvalName, int indexRow, String emails) {
		table.addCell(String.valueOf(indexRow));
		table.addCell(approvalName);
		table.addCell("Email : " + emails);
	}


	/**
	 * Gets the XML request parameter.
	 */
	private String getRequestXml(HttpServletRequest request) throws IOException {
		String xml = request.getParameter("xml");
		
		// Decoding is optional (no plain text values allowed)
		if (xml != null && xml.startsWith("%3C")) {
			xml = URLDecoder.decode(xml, "UTF-8");
		}

		return xml;
	}

	/**
	 * Renders the XML to the given canvas.
	 */
	private void renderXml(String xml, mxICanvas2D canvas) throws SAXException, ParserConfigurationException, IOException {
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
		reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		reader.setContentHandler(new mxSaxOutputHandler(canvas));
		reader.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Creates a graphics canvas with an image cache.
	 */
	protected mxGraphicsCanvas2D createCanvas(String url, Graphics2D g2) {
		// Caches custom images for the time of the request
		final Map<String, Image> shortCache = new ConcurrentHashMap<>();
		final String domain = url.substring(0, url.lastIndexOf('/'));

		return new mxGraphicsCanvas2D(g2)
		{
			@Override
			public Image loadImage(String src)
			{
				// Uses local image cache by default
				Map<String, Image> cache = shortCache;

				// Uses global image cache for local images
				if (src.startsWith(domain))
				{
					cache = imageCache;
				}

				Image image = cache.get(src);

				if (image == null)
				{
					image = super.loadImage(src);

					if (image != null)
					{
						cache.put(src, image);
					}
					else
					{
						cache.put(src, WorkflowConstants.EMPTY_IMAGE);
					}
				}
				else if (image == WorkflowConstants.EMPTY_IMAGE)
				{
					image = null;
				}

				return image;
			}
		};
	}

	@GetMapping("/workflow/listing-and-filter")
	public ResponseEntity<PageableDTO> getList(
			@RequestParam(value = "pagination[perpage]", defaultValue = "10") int perPage,
			@RequestParam(value = "pagination[page]", defaultValue = "1") int page,
			@RequestParam(value = "sort[field]", defaultValue = "createdDate") String sortBy,
			@RequestParam(value = "sort[sort]", defaultValue = "DESC") String orderBy,
			@RequestParam(value = "query[generalSearch]", defaultValue = "") String query,
			@RequestParam(value = "createdBy[]", required = false) List<String> createdBy,
			@RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
			@RequestParam(value = "endDate", required = false, defaultValue = "") String endDate) {

		// result search
		Page<Workflow> workFlows;
		PageableDTO result = new PageableDTO();
		try {
			// create pageable with client parameter
			Pageable pageable = PageableUtil.createPageable(page - 1, perPage, sortBy, orderBy);

			workFlows = getPageWorkflowByConditions(query, createdBy, startDate, endDate, pageable);

			// map entity to dto
			Page<WorkflowDTO> workflowDTOS = workFlows.map(WorkflowDTO::new);

			// identified number row and map to dto result
			AtomicLong numIncrement = new AtomicLong();
			List<WorkflowDTO> workflowDTOList = workflowDTOS.getContent();

			workflowDTOList.forEach(object -> object.setNo(numIncrement.incrementAndGet() + ((page - 1) * perPage)));
			result = PageableUtil.pageableMapper(workflowDTOS, pageable, workflowDTOList);
		} catch(Exception e) {
			logger.error("WorkflowRestController.getList ", e);
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/workflow/listing")
	public ResponseEntity<List<WorkflowDTO>> getListWorkflow(){
		List<WorkflowDTO> workflowDTOS = new ArrayList<>();
		try {
			List<Workflow> workflowList = workflowService.findAllWorkflow();
			for(Workflow item : workflowList) {
				WorkflowDTO dto = new WorkflowDTO();
				dto.setId(item.getId());
				dto.setName(item.getName());
				workflowDTOS.add(dto);
			}
		} catch(Exception e) {
			logger.error("WorkflowRestController.getListWorkflow ", e);
			return new ResponseEntity<>(workflowDTOS, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(workflowDTOS, HttpStatus.OK);
	}

	private Page<Workflow> getPageWorkflowByConditions(String query, List<String> createdBy, String startDate, String endDate, Pageable pageable) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Page<Workflow> workFlows;
		Date start = null;
		Date end = null;
		if (StringUtils.isNotBlank(startDate)) {
			start = formatter.parse(startDate);
		}
		if(StringUtils.isNotBlank(endDate)) {
			end = DateUtils.addDays(formatter.parse(endDate),1);
		}

		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate) && CollectionUtils.isNotEmpty(createdBy)) {
			workFlows = workflowService.findByAllConditions(pageable, query, start, end, createdBy);
		} else if (StringUtils.isNotBlank(startDate) && CollectionUtils.isNotEmpty(createdBy)) {
			workFlows = workflowService.findByStartDayAndCreatedBy(pageable, start, query, createdBy);
		} else if (StringUtils.isNotBlank(endDate) && CollectionUtils.isNotEmpty(createdBy)) {
			workFlows = workflowService.findByEndDayAndCreatedBy(pageable, end, query, createdBy);
		} else if (CollectionUtils.isNotEmpty(createdBy)) {
			workFlows = workflowService.findByCreatedBy(pageable, query, createdBy);
		} else if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			workFlows = workflowService.findAllByDate(pageable, start, end, query);
		} else if (StringUtils.isNotBlank(startDate)) {
			workFlows = workflowService.findAllByStartDate(pageable, start, query);
		} else if (StringUtils.isNotBlank(endDate)) {
			workFlows = workflowService.findAllByEndDate(pageable, end, query);
		}  else {
			workFlows = workflowService.findAllWorkflow(pageable, query);
		}
		return workFlows;
	}

	@GetMapping("/workflow/created-person")
	public Set<String> getListCreatedPerson() {
		Set<String> setCreatedPerson = new HashSet<>();
		try {
			setCreatedPerson = workflowService.findAllCreatedPerson();
		} catch(Exception e) {
			logger.error("WorkflowRestController.getListCreatedPerson ", e);
		}
		return setCreatedPerson;
	}

	@PostMapping("/workflow/delete-workflow")
	public boolean deleteWorkflow(@RequestParam long id, HttpServletRequest request) {
		boolean check = true;
		try {
			String nameWorkflow = workflowService.deleteWorkflow(id);
			if(StringUtils.isNotBlank(nameWorkflow)) {
				UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
				String nameCreatedBy =  userProfileDto.getFullName();
				SystemAuditLog systemAuditLog = new SystemAuditLog();
				CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
				String details = CONST_WORKFLOW + nameWorkflow + ") has been deleted by " + nameCreatedBy;
				commonMethodComponent.addAuditLogData(systemAuditLog,
						nameCreatedBy,
						userProfileDto.getGroups(),
						details,
						"Delete Workflow Successful",
						true);
				iSystemAuditLogRepository.save(systemAuditLog);
			}
		} catch (Exception e) {
			logger.error("Exception: WorkflowRestController.deleteWorkflow ", e);
			check = false;
		}
		return check;
	}

	@PostMapping("/clone-workflow")
	public boolean cloneWorkflow(@RequestParam long id, HttpServletRequest request) {
		boolean check = true;
		try {
			UserProfileDto userProfileDto = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
			String nameCreatedBy =  userProfileDto.getFullName();
			String nameWorkflow = workflowService.cloneWorkflow(id, nameCreatedBy);
			if(StringUtils.isNotBlank(nameWorkflow)) {
				SystemAuditLog systemAuditLog = new SystemAuditLog();
				CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
				String details = CONST_WORKFLOW + nameWorkflow + ") has been clone by " + nameCreatedBy;
				commonMethodComponent.addAuditLogData(systemAuditLog,
						nameCreatedBy,
						userProfileDto.getGroups(),
						details,
						"Clone Workflow Successful",
						true);
				iSystemAuditLogRepository.save(systemAuditLog);
			}
		} catch (Exception e) {
			logger.error("Exception: WorkflowRestController.cloneWorkflow ", e);
			check = false;
		}
		return check;
	}

	@GetMapping("/workflow/history-listing")
	public Map<String, Object> historyListingWorkflow(@RequestParam long id) {
		Map<String, Object> mapHistoryWorkflow = new ConcurrentHashMap<>();
		try {
			Workflow wf = workflowService.findByIdWorkflow(id);
			mapHistoryWorkflow.put("name", wf.getName());
			mapHistoryWorkflow.put("description", wf.getDescription());
			mapHistoryWorkflow.put("version", wf.getVersion() == 0 ? 1 : wf.getVersion());

			if(StringUtils.isNotBlank(wf.getModifiedBy())) {
				mapHistoryWorkflow.put("updateBy", wf.getModifiedBy());
				mapHistoryWorkflow.put("updateAt", BEDateUtils.convertDateToStringByFormatOn(wf.getModifiedDate()));
			} else {
				mapHistoryWorkflow.put("updateBy", wf.getCreatedBy());
				mapHistoryWorkflow.put("updateAt", BEDateUtils.convertDateToStringByFormatOn(wf.getCreatedDate()));
			}
			List<WorkflowHistory> workflowHistories = workflowService.getListWorkflowByWorkflowId(id);
			List<WorkflowHistoryDTO> workflowHistoryDTOS = workflowHistories.stream().map(WorkflowHistoryDTO::new).collect(Collectors.toList());
			AtomicLong numIncrement = new AtomicLong();
			workflowHistoryDTOS.forEach(object -> object.setNo(numIncrement.incrementAndGet()));
			mapHistoryWorkflow.put("listWorkflow", workflowHistoryDTOS);
		} catch (Exception e) {
			logger.error("Exception: WorkflowRestController.historyListingWorkflow ", e);
		}
		return mapHistoryWorkflow;
	}

	@PostMapping("/workflow/restore-workflow")
	public boolean restoreWorkflow(@RequestParam long historyId, HttpServletRequest request) {
		UserProfileDto data = (UserProfileDto) request.getSession().getAttribute(CommonConstants.USER_PROFILE);
		WorkflowHistory workflowHistory = workflowService.getWorkflowHistoryById(historyId);
		Workflow workflow = workflowService.findByIdWorkflow(workflowHistory.getWorkflowId());
		workflowService.saveWorkflowHistory(workflow);
		workflowService.restoreWorkflow(workflowHistory, data.getFullName());

		SystemAuditLog systemAuditLog = new SystemAuditLog();
		CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
		String details = "Workflow Name: " + workflow.getName() + " has been restored by " + data.getEmail();
		String desc = "Restore Workflow";
		commonMethodComponent.addAuditLogData(systemAuditLog, data.getFullName(), data.getGroups(), details, desc,
				true);
		iSystemAuditLogRepository.save(systemAuditLog);
		return true;
	}

	@GetMapping("/workflow/check-workflow")
	public String checkWorkflowRestore(@RequestParam long historyId) {
		WorkflowHistory workflowHistory = workflowService.getWorkflowHistoryById(historyId);
		if(workflowService.checkVersionPlaybook(workflowHistory)) {
			return "Cannot restore the workflow to the previous version because the playbook(s) are used in this workflow which are not in latest version or removed.";
		}
		if(CollectionUtils.isNotEmpty(jobService.getListJobByWorkflowId(workflowHistory.getWorkflowId()))) {
			return "The current version of the workflow is used by the job. Cannot restore to the previous version.";
		}
		return StringUtils.EMPTY;
	}
}
