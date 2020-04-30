package com.nera.nms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class JobListingController {

    @GetMapping("/nera/api/job-detail")
    @PreAuthorize("hasAuthority('CREATE_JOB')")
    public String redirectCreatePage(Model model) {
        return "job/job-detail";
    }

    @GetMapping("/nera/api/job-detail/{id}")
    @PreAuthorize("hasAuthority('UPDATE_JOB')")
    public String redirectCreatePage(Model model, @PathVariable Long id) {
        model.addAttribute("idJob", id);
        return "job/job-detail";
    }
}
