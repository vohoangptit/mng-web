package com.nera.nms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileListingController {
    @GetMapping("/menu/masterdata/file-listing")
    public String fileListing(Model model) {
        return "filemanagement/file-listing";
    }
	
    @GetMapping("/menu/masterdata/file-uploadfile")
    @PreAuthorize("hasAuthority('CREATE_FILE')")
    public String uploadFile(Model model) {
        return "filemanagement/file-uploadfile";
    }
}
