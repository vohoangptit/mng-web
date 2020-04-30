package com.nera.nms.constants;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RolesConstants {

	// ROLE FOR USER
    private static final String VIEW_LISTING_USER = "VIEW USER LISTING";

    private static final String CREATE_USER = "CREATE USERS";

    private static final String UPDATE_USER = "UPDATE USERS";

    private static final String DELETE_USER = "DELETE USERS";
    
    
    // ROLE FOR USER GROUP
    private static final String VIEW_LISTING_GROUP = "VIEW USER GROUP LISTING";

    private static final String CREATE_GROUP = "CREATE USER GROUP";

    private static final String UPDATE_GROUP = "UPDATE USER GROUP";

    private static final String DELETE_GROUP = "DELETE USER GROUP";
    
    
    // ROLE FOR ACCESS CONTROL
    private static final String VIEW_ACCESS_CONTROL = "VIEW ACCESS CONTROL";

    private static final String UPDATE_ACCESS_CONTROL = "UPDATE ACCESS CONTROL";
    
    // ROLE FOR SETTING
    private static final String VIEW_ALL_SETTING = "VIEW ALL SETTINGS";

    private static final String UPDATE_GENERAL = "UPDATE GENERAL SETTING";

    // MAIL TEMPLATE
    private static final String VIEW_MAIL_TEMPLATE_LISTING = "VIEW MAIL TEMPLATE LISTING";

    private static final String UPDATE_MAIL_TEMPLATE = "UPDATE MAIL TEMPLATE";
    
    // ROLE FOR AUDIT
    private static final String EXPORT_AUDIT = "EXPORT AUDIT LOG";
    private static final String VIEW_AUDIT = "VIEW AUDIT LOG";
    
    // ROLE FOR PLAYBOOK MANAGEMENT
    private static final String VIEW_PLAYBOOK_LISTING = "VIEW PLAYBOOK LISTING";

    private static final String CREATE_PLAYBOOK = "CREATE PLAYBOOK";

    private static final String UPDATE_PLAYBOOK = "UPDATE PLAYBOOK";

    private static final String DELETE_PLAYBOOK = "DELETE PLAYBOOK(NEW STATUS)";

    private static final String SEND_PLAYBOOK_FOR_APPROVAL = "SEND PLAYBOOK FOR APPROVAL";

    private static final String APPROVE_AND_REJECT_PLAYBOOK = "APPROVE & REJECT PLAYBOOK";

    private static final String VIEW_APPROVED_PLAYBOOK_LISTING = "VIEW APPROVED PLAYBOOK LISTING";

    private static final String UPDATE_APPROVED_PLAYBOOK = "UPDATE APPROVED PLAYBOOK";

    private static final String DELETE_APPROVED_PLAYBOOK = "DELETE APPROVED & REJECT PLAYBOOK";

    private static final String VIEW_HISTORY_PLAYBOOK = "VIEW HISTORY PLAYBOOK";

    private static final String RESTORE_HISTORICAL_PLAYBOOK = "RESTORE HISTORICAL PLAYBOOK";

    // ROLE FOR INVENTORY
    private static final String VIEW_HOST_LISTING = "VIEW HOST LISTING";

    private static final String CREATE_HOST = "CREATE HOST";

    private static final String UPDATE_HOST = "UPDATE HOST";

    private static final String DELETE_HOST = "DELETE HOST";

    private static final String IMPORT_HOST = "IMPORT HOST";

    private static final String VIEW_GROUP_HOST_LISTING = "VIEW GROUP HOST LISTING";

    private static final String CREATE_GROUP_HOST = "CREATE GROUP HOST";

    private static final String UPDATE_GROUP_HOST = "UPDATE GROUP HOST";

    private static final String DELETE_GROUP_HOST = "DELETE GROUP HOST";

    // ROLE FOR FILE MANAGEMENT
    private static final String VIEW_FILE_LISTING = "VIEW FILE LISTING";

    private static final String UPDATE_FILE = "UPDATE FILE";

    private static final String DELETE_FILE = "DELETE FILE";

    private static final String CREATE_FILE = "CREATE FILE";

    private static final String VIEW_HISTORY_FILE = "VIEW HISTORY FILE";

    private static final String RESTORE_HISTORICAL_FILE = "RESTORE HISTORICAL FILE";

    // ROLE FOR WORKFLOW EDITOR
    private static final String VIEW_WORKFLOW_LISTING = "VIEW WORKFLOW LISTING";

    private static final String CREATE_WORKFLOW = "CREATE WORKFLOW";

    private static final String UPDATE_WORKFLOW = "UPDATE WORKFLOW";

    private static final String DELETE_WORKFLOW = "DELETE WORKFLOW";

    private static final String APPROVED_WORKFLOW_PROCESS = "APPROVED WORKFLOW PROCESS";

    private static final String VIEW_HISTORY_WORKFLOW = "VIEW HISTORY WORKFLOW";

    private static final String RESTORE_HISTORICAL_WORKFLOW = "RESTORE HISTORICAL WORKFLOW";

    // ROLE FOR JOB MANAGEMENT
    private static final String VIEW_JOB_LISTING = "VIEW JOB LISTING";

    private static final String CREATE_JOB = "CREATE JOB";

    private static final String UPDATE_JOB = "UPDATE JOB";

    private static final String DELETE_JOB = "DELETE JOB";

    // ROLE FOR JOB PLANNING
    private static final String VIEW_JOB_PLANNING = "VIEW JOB PLANNING";

    private static final String ASSIGN_JOB = "ASSIGN JOB";

    // ROLE FOR MY JOB
    private static final String VIEW_MY_JOBS = "VIEW MY JOBS";

    private static final String ACCEPT_REJECT_JOB = "ACCEPT REJECT JOB";

    private static final String EXECUTE_JOB = "EXECUTE JOB";

    // ROLE FOR MY APPROVAL REQUEST
    private static final String VIEW_MY_APPROVAL_REQUEST = "VIEW MY APPROVAL REQUEST";

    private static final String APPROVE_AND_REJECT_JOB = "APPROVE & REJECT JOB EXECUTION";

    // ROLE DASHBOARD
    private static final String VIEW_ENGINEER_DASHBOARD = "VIEW ENGINEER DASHBOARD";

    private static final String VIEW_TEAM_LEADER_DASHBOARD = "VIEW TEAM LEADER DASHBOARD";

    private static final String VIEW_ADMINISTRATOR_DASHBOARD = "VIEW ADMINISTRATOR DASHBOARD";


    private RolesConstants() {
    }
    
    private static final Map<String,String> USER_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> USER_GROUP_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> AUDIT_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> ACCESS_CONTROL = new LinkedHashMap<>();
    private static final Map<String,String> SETTING = new LinkedHashMap<>();
    private static final Map<String,String> MAIL_TEMPLATE = new LinkedHashMap<>();
    private static final Map<String,String> PLAYBOOK_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> INVENTORY = new LinkedHashMap<>();
    private static final Map<String,String> FILE_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> WORKFLOW_EDITOR = new LinkedHashMap<>();
    private static final Map<String,String> JOB_MANAGEMENT = new LinkedHashMap<>();
    private static final Map<String,String> JOB_PLANNING = new LinkedHashMap<>();
    private static final Map<String,String> MY_JOBS = new LinkedHashMap<>();
    private static final Map<String,String> MY_APPROVAL_REQUEST = new LinkedHashMap<>();
    private static final Map<String,String> DASHBOARD = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> PERMISSION = new LinkedHashMap<>();
    public static final String ROLE_SYSTEM = "System";

    static{
    	//PUSH TO USER
    	USER_MANAGEMENT.put("1.1", VIEW_LISTING_USER);
    	USER_MANAGEMENT.put("1.2", CREATE_USER);
        USER_MANAGEMENT.put("1.3", UPDATE_USER);
    	USER_MANAGEMENT.put("1.4", DELETE_USER);
    	
    	//PUSH TO USER GROUP
    	USER_GROUP_MANAGEMENT.put("2.1", VIEW_LISTING_GROUP);
    	USER_GROUP_MANAGEMENT.put("2.2", CREATE_GROUP);
    	USER_GROUP_MANAGEMENT.put("2.3", UPDATE_GROUP);
    	USER_GROUP_MANAGEMENT.put("2.4", DELETE_GROUP);
    	
    	//PUT TO ACCESS CONTROL
    	ACCESS_CONTROL.put("3.1", VIEW_ACCESS_CONTROL);
    	ACCESS_CONTROL.put("3.2", UPDATE_ACCESS_CONTROL);
    	
    	//PUT TO SETTING
    	SETTING.put("4.1", VIEW_ALL_SETTING);
    	SETTING.put("4.2", UPDATE_GENERAL);
    	//PUT TO MAIL TEMPLATE
    	MAIL_TEMPLATE.put("5.1", VIEW_MAIL_TEMPLATE_LISTING);
    	MAIL_TEMPLATE.put("5.2", UPDATE_MAIL_TEMPLATE);

    	//PUT TO AUDIT
    	AUDIT_MANAGEMENT.put("6.1", VIEW_AUDIT);
    	AUDIT_MANAGEMENT.put("6.2", EXPORT_AUDIT);
    	

    	//PUT TO PLAYBOOK MANAGEMENT
    	PLAYBOOK_MANAGEMENT.put("7.1", VIEW_PLAYBOOK_LISTING);
    	PLAYBOOK_MANAGEMENT.put("7.2", CREATE_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.3", UPDATE_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.4", DELETE_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.5", SEND_PLAYBOOK_FOR_APPROVAL);
    	PLAYBOOK_MANAGEMENT.put("7.6", APPROVE_AND_REJECT_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.7", VIEW_APPROVED_PLAYBOOK_LISTING);
    	PLAYBOOK_MANAGEMENT.put("7.8", UPDATE_APPROVED_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.9", DELETE_APPROVED_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.10", VIEW_HISTORY_PLAYBOOK);
    	PLAYBOOK_MANAGEMENT.put("7.11", RESTORE_HISTORICAL_PLAYBOOK);

    	//PUT TO INVENTORY
        INVENTORY.put("8.1", VIEW_HOST_LISTING);
        INVENTORY.put("8.2", CREATE_HOST);
        INVENTORY.put("8.3", UPDATE_HOST);
        INVENTORY.put("8.4", DELETE_HOST);
        INVENTORY.put("8.5", IMPORT_HOST);
        INVENTORY.put("8.6", VIEW_GROUP_HOST_LISTING);
        INVENTORY.put("8.7", CREATE_GROUP_HOST);
        INVENTORY.put("8.8", UPDATE_GROUP_HOST);
        INVENTORY.put("8.9", DELETE_GROUP_HOST);
    	
    	//PUT TO FILE MANAGEMENT
    	FILE_MANAGEMENT.put("9.1", VIEW_FILE_LISTING);
    	FILE_MANAGEMENT.put("9.2", CREATE_FILE);
    	FILE_MANAGEMENT.put("9.3", UPDATE_FILE);
    	FILE_MANAGEMENT.put("9.4", DELETE_FILE);
    	FILE_MANAGEMENT.put("9.5", VIEW_HISTORY_FILE);
    	FILE_MANAGEMENT.put("9.6", RESTORE_HISTORICAL_FILE);

    	//PUT TO WORKFLOW EDITOR
    	WORKFLOW_EDITOR.put("10.1", VIEW_WORKFLOW_LISTING);
    	WORKFLOW_EDITOR.put("10.2", CREATE_WORKFLOW);
    	WORKFLOW_EDITOR.put("10.3", UPDATE_WORKFLOW);
    	WORKFLOW_EDITOR.put("10.4", DELETE_WORKFLOW);
    	WORKFLOW_EDITOR.put("10.5", APPROVED_WORKFLOW_PROCESS);
    	WORKFLOW_EDITOR.put("10.6", VIEW_HISTORY_WORKFLOW);
    	WORKFLOW_EDITOR.put("10.7", RESTORE_HISTORICAL_WORKFLOW);

        //PUT TO JOB MANAGEMENT
        JOB_MANAGEMENT.put("11.1", VIEW_JOB_LISTING);
        JOB_MANAGEMENT.put("11.2", CREATE_JOB);
        JOB_MANAGEMENT.put("11.3", UPDATE_JOB);
        JOB_MANAGEMENT.put("11.4", DELETE_JOB);

        //PUT TO JOB PLANNING
        JOB_PLANNING.put("12.1", VIEW_JOB_PLANNING);
        JOB_PLANNING.put("12.2", ASSIGN_JOB);

        //PUT TO MY JOBS
        MY_JOBS.put("13.1", VIEW_MY_JOBS);
        MY_JOBS.put("13.2", ACCEPT_REJECT_JOB);
        MY_JOBS.put("13.3", EXECUTE_JOB);

        //PUT TO MY APPROVAL REQUEST
        MY_APPROVAL_REQUEST.put("14.1", VIEW_MY_APPROVAL_REQUEST);
        MY_APPROVAL_REQUEST.put("14.2", APPROVE_AND_REJECT_JOB);

        //DASHBOARD
        DASHBOARD.put("15.1", VIEW_ENGINEER_DASHBOARD);
        DASHBOARD.put("15.2", VIEW_TEAM_LEADER_DASHBOARD);
        DASHBOARD.put("15.3", VIEW_ADMINISTRATOR_DASHBOARD);

        //PUT PERMISSION
    	PERMISSION.put("User Management", USER_MANAGEMENT);
    	PERMISSION.put("User Group Management", USER_GROUP_MANAGEMENT);
    	PERMISSION.put("Access Control", ACCESS_CONTROL);
    	PERMISSION.put("Setting", SETTING);
    	PERMISSION.put("Mail Template", MAIL_TEMPLATE);
    	PERMISSION.put("Audit Log", AUDIT_MANAGEMENT);
    	PERMISSION.put("Playbook Management", PLAYBOOK_MANAGEMENT);
    	PERMISSION.put("Inventory", INVENTORY);
    	PERMISSION.put("File Management", FILE_MANAGEMENT);
    	PERMISSION.put("Workflow Editor", WORKFLOW_EDITOR);
        PERMISSION.put("Job Management", JOB_MANAGEMENT);
        PERMISSION.put("Job Planning", JOB_PLANNING);
        PERMISSION.put("My Jobs", MY_JOBS);
        PERMISSION.put("My Approval Request", MY_APPROVAL_REQUEST);
        PERMISSION.put("Dashboard", DASHBOARD);
    }

    public static Map<String, Map<String, String>> getPERMISSION() {
        return PERMISSION;
    }
}
