package com.nera.nms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileHistoryController {

    @PreAuthorize("hasAuthority('VIEW_HISTORY_FILE')")
    @GetMapping("/file-history/{id}")
    public String fileHistoryView(Model model, @PathVariable Long id) {
        model.addAttribute("fileId", id);
        return "file_management_history/listing";
    }
}
