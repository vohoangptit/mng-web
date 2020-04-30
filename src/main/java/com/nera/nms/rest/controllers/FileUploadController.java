package com.nera.nms.rest.controllers;

import com.nera.nms.models.ENUM.PlaybookOutputType;
import com.nera.nms.models.PlaybookOutput;
import com.nera.nms.payload.UploadFileResponse;
import com.nera.nms.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("nera/upload")
public class FileUploadController {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	private static final char SLASH = '/';

	private final FileStorageService fileStorageService;

	public FileUploadController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping("/single")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("nera/upload/downloadFile/")
				.path(fileName).toUriString();

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.stream(files).map(this::uploadFile).collect(Collectors.toList());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@PostMapping("/playbook")
	public Map<String, Object> uploadFilePlaybook(@RequestParam("file") MultipartFile file) {
		Map<String,Object> result = new HashMap<>();
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("nera/upload/downloadFile/")
				.path(fileName).toUriString();

		UploadFileResponse data = new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
		try {
			File files;
			files = multipartToFile(file, fileName);
			List<String> output = searchString(files.getAbsolutePath(), "msg:");
			List<PlaybookOutput> listOutput = new ArrayList<>();
			output.forEach(e ->{
				String[] element = e.split("=",2);
				if(element.length>1){
					PlaybookOutput out = new PlaybookOutput();
					out.setVariable(element[0].trim());
					out.setValue(element[1].trim());
					out.setType(PlaybookOutputType.TEXT);
					listOutput.add(out);
				}
			});
			result.put("output", listOutput);
		} catch (IOException e) {
			logger.error("FileUploadController.uploadFilePlaybook ", e);
		}
		result.put("fileInfo", data);
		return result;
	}
	
	@PostMapping("/playbook/get-output")
	public List<PlaybookOutput> getOutputPlaybook(@RequestParam("file") MultipartFile file) {
		List<PlaybookOutput> listOutput = new ArrayList<>();
		String fileName = fileStorageService.storeFile(file);
		try {
			File files = multipartToFile(file, fileName);
			List<String> output = searchString(files.getAbsolutePath(), "msg:");
			
			output.forEach(e ->{
				String[] element = e.split("=",2);
				if(element.length>1){
					PlaybookOutput out = new PlaybookOutput();
					out.setVariable(element[0].trim());
					out.setValue(element[1].trim());
					out.setType(PlaybookOutputType.TEXT);
					listOutput.add(out);
				}
			});
		} catch (IOException e) {
			logger.error("FileUploadController.getOutputPlaybook ", e);
		}
		return listOutput;
	}

	private static File multipartToFile(MultipartFile multipart, String fileName) throws IOException {
	    File convertFile = new File(System.getProperty("java.io.tmpdir") + SLASH + fileName);
	    multipart.transferTo(convertFile);
	    return convertFile;
	}
	
	public static List<String> searchString(String fileName, String phrase) throws IOException {
		Scanner fileScanner = new Scanner(new File(fileName));
		List<String> messList  = new ArrayList<>();
		Pattern pattern = Pattern.compile(phrase);
		Matcher matcher;
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			matcher = pattern.matcher(line);
			while (matcher.find()) {
				String pass = line.substring(matcher.start()+4).replace("\"", "");
				messList.add(pass);
			}
		}
		fileScanner.close();
		return messList;
	}
}
