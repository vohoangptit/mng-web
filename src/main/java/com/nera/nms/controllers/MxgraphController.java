package com.nera.nms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Phuong Nguyen
 *
 */
@Controller

public class MxgraphController {

	private static final String PATH_PAGE_DRAWING = "drawing_workflow/index";

	private static final String WORKFLOW_ID = "workflowid";

	@GetMapping("/history/workflow/{id}")
	public String mxgraphHistory(Model model, @PathVariable Long id) {
		model.addAttribute(WORKFLOW_ID, id);
		return "drawing_workflow/index-history";
	}

	@GetMapping("/history/editor")
	public String mxgraphContainHistory(Model model){
		return "drawing_workflow/contain-history";
	}

	@GetMapping("/drawing")
	@PreAuthorize("hasAuthority('CREATE_WORKFLOW')")
	public String mxgraphViewer(Model model) {
		return PATH_PAGE_DRAWING;
	}

	@GetMapping("/clone/drawing/{id}")
	public String mxgraphClone(Model model,@PathVariable Long id) {
		model.addAttribute(WORKFLOW_ID, id);
		model.addAttribute("cloneId", id);
		return PATH_PAGE_DRAWING;
	}

	@GetMapping("/drawing/{id}")
	@PreAuthorize("hasAuthority('UPDATE_WORKFLOW')")
	public String mxgraphViewerback(Model model,@PathVariable Long id) {
		model.addAttribute(WORKFLOW_ID, id);
		return PATH_PAGE_DRAWING;
	}

	@GetMapping("/editor")
	public String mxgraphContain(Model model){
		return "drawing_workflow/contain";
	}

	@GetMapping("/editor/{id}")
	public String mxgraphContainback(Model model,@PathVariable Long id){
		model.addAttribute(WORKFLOW_ID, id);
		return "drawing_workflow/contain";
	}

	@GetMapping(value = "/uploadpage")
    public String auditLog(Model model){
               return "/upload/index";
	}

	@GetMapping(value = "/playbooklisting_workflow")
    public String playbook4workflow(Model model){
               return "drawing_workflow/playbookapproved4workflow";
    }
}
