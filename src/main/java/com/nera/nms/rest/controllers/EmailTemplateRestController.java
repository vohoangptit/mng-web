package com.nera.nms.rest.controllers;

import com.nera.nms.dto.UserEmailTemplateDTO;
import com.nera.nms.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nera/email-template")
public class EmailTemplateRestController {
    
    @Autowired
    EmailService emailService;

    @GetMapping("/find-all")
    public List<UserEmailTemplateDTO> getAllEmailTemplate(@RequestParam(defaultValue = "", value = "searchString") String stringSearch){
    	return emailService.getAllEmailTemplate(stringSearch);
    }
    
    @PostMapping("/update")
    public void getAllEmailTemplate(@RequestBody UserEmailTemplateDTO body){
    	emailService.updateEmailTemplate(body);
    }
    
    @GetMapping("/find-by-id")
    public UserEmailTemplateDTO findById(@RequestParam long id){
    	return emailService.findById(id);
    }
    
    @GetMapping("/validatedName")
    public boolean validatedTemplateName
    (@RequestParam(value = "templateName") String templateName, @RequestParam(value = "id") long id) {
        return emailService.findOneByName(templateName, id);
	}
}
