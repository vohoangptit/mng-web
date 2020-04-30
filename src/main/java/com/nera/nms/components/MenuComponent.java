/**
 * 
 */
package com.nera.nms.components;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.nera.nms.constants.MenuConstants;
import com.nera.nms.enums.Menu;

/**
 * @author Martin Do
 *
 */
@Component
public class MenuComponent {

    /**
     *<h3>highlight selected menu bar</h3> 
     */
    public void initMenuModalView(Model model, Menu menu) {
        switch (menu) {
        	case DASHBOARD:
        		model.addAttribute(MenuConstants.ACTIVE_DASH_BOARD_MENU_BAR, true);
        		break;
            case USER:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_USER_MENU_BAR, true);
                break;
            case USER_GROUP:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_USER_GROUP_MENU_BAR, true);
                break;
            case SETTING:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_SUB_SETTING_MENU_BAR, true);
                break;
            case ACCESS_CONTROL:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_ACCESS_CONTROL_MENU_BAR, true);
                break;
            case AUDIT:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_AUDIT_MENU_BAR, true);
                break;
            case PLAYBOOK:
                model.addAttribute(MenuConstants.ACTIVE_MASTER_DATA_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_PLAYBOOK_MENU_BAR, true);
                break;
            case INVENTORY:
                model.addAttribute(MenuConstants.ACTIVE_MASTER_DATA_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_INVENTORY_MENU_BAR, true);
                break;
            case FILEMANAGEMENT:
                model.addAttribute(MenuConstants.ACTIVE_MASTER_DATA_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_FILE_MANAGEMENT_MENU_BAR, true);
                break;
            case PLAYBOOK_APPROVED:
                model.addAttribute(MenuConstants.ACTIVE_MASTER_DATA_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_APPROVED_PLAYBOOK_MENU_BAR, true);
                break;
            case EMAIL_TEMPLATE:
                model.addAttribute(MenuConstants.ACTIVE_SETTING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_EMAIL_TEMPLATE_MENU_BAR, true);
                break;
            case WORKFLOW:
                model.addAttribute(MenuConstants.ACTIVE_WORKFLOW_ENGINE_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_WORKFLOW_LISTING_MENU_BAR, true);
                break;
            case JOB_MANAGEMENT:
                model.addAttribute(MenuConstants.ACTIVE_JOB_MANAGEMENT_MENU_BAR, true);
                break;
            case JOB_PLAN_LISTING:
            	model.addAttribute(MenuConstants.ACTIVE_JOB_PLANNING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_JOB_PLAN_LISTING_MENU_BAR, true);
                break;
            case JOB_PLAN_CALENDAR:
            	model.addAttribute(MenuConstants.ACTIVE_JOB_PLANNING_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_JOB_PLAN_CALENDAR_MENU_BAR, true);
                break;
            case MY_JOB_LISTING:
                model.addAttribute(MenuConstants.ACTIVE_MY_JOB_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_MY_JOB_LISTING_MENU_BAR, true);
                break;
            case MY_JOB_CALENDAR:
                model.addAttribute(MenuConstants.ACTIVE_MY_JOB_MENU_BAR, true);
                model.addAttribute(MenuConstants.ACTIVE_MY_JOB_CALENDAR_MENU_BAR, true);
                break;
        }
    }
}
