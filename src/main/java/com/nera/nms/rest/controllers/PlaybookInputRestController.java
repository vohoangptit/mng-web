package com.nera.nms.rest.controllers;

import com.nera.nms.services.PlaybookInputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("nera/playbook-input/api")
public class PlaybookInputRestController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PlaybookInputService playbookInputService;

	@PostMapping("/count-by-file-id")
	public long countPlayBookInputByFileId(@RequestParam("fileId") long fileId) {
		log.info("Calling PlaybookInputRestController::countPlayBookInputByFileId api");
		try {
			return playbookInputService.count(fileId);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return -1;
		}
	}
		
}
