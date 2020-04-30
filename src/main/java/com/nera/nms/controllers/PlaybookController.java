package com.nera.nms.controllers;

import com.nera.nms.components.MenuComponent;
import com.nera.nms.constants.MenuConstants;
import com.nera.nms.enums.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class PlaybookController {

	@Autowired
    MenuComponent menuComponent;
	
    @GetMapping("/menu/masterdata/playbook/detail")
    @PreAuthorize("hasAuthority('CREATE_PLAYBOOK')")
    public String detail(Model model){
    	menuComponent.initMenuModalView(model, Menu.PLAYBOOK);
        return "playbook/playbook-details";
    }
    
    @GetMapping("/menu/masterdata/playbook/detail/{id}")
    @PreAuthorize("hasAuthority('UPDATE_PLAYBOOK')")
    public String detailUpdate(Model model, @PathVariable Long id){
    	menuComponent.initMenuModalView(model, Menu.PLAYBOOK);
    	model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, false);
    	model.addAttribute(MenuConstants.ACTIVE_WORKFLOW_ENGINE_MENU_BAR, false);
        model.addAttribute("playbookid", id);
    	return "playbook/playbook-details";
    }

    @GetMapping(value = "/menu/masterdata/playbook/history/{id}")
    @PreAuthorize("hasAnyAuthority('VIEW_HISTORY_PLAYBOOK')")
    public String historyPlaybook(Model model, @PathVariable Long id){
        menuComponent.initMenuModalView(model, Menu.PLAYBOOK);
        model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, false);
        model.addAttribute(MenuConstants.ACTIVE_WORKFLOW_ENGINE_MENU_BAR, false);
        model.addAttribute("playbookId", id);
        return "playbook/playbook-history";
    }
    
    @GetMapping(value = "/menu/masterdata/playbook-approved/detail/{id}")
    @PreAuthorize("hasAuthority('UPDATE_APPROVED_PLAYBOOK')")
    public String approveddetailUpdate(Model model, @PathVariable Long id){
        model.addAttribute("playbookid", id);
    	return "playbook/playbookapproved-details";
    }

    @GetMapping(value = "/menu/masterdata/playbook/historydetail/{id}")
    @PreAuthorize("hasAuthority('VIEW_HISTORY_PLAYBOOK')")
    public String historyDetailPlaybook(Model model, @PathVariable Long id){
        model.addAttribute("playbookHistoryId", id);
        return "playbook/playbook-history-details";
    }
}
