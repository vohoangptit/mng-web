package com.nera.nms.constants;

import com.nera.nms.models.EmailTemplateVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Variable email template constants.
 */
public class VariableEmailTemplateConstants {

	private VariableEmailTemplateConstants(){}

	/**
	 * The constant VARIABLE_EMAIL.
	 */
	public static final String VARIABLE_EMAIL = "${email}";

	/**
	 * The constant VARIABLE_FULL_NAME.
	 */
	public static final String VARIABLE_FULL_NAME = "${fullName}";

	/**
	 * The constant VARIABLE_SERVER_URL.
	 */
	public static final String VARIABLE_SERVER_URL = "${serverURL}";

	/**
	 * The constant VARIABLE_REJECTED_AT.
	 */
	public static final String VARIABLE_REJECTED_AT = "${rejectedAt}";

	/**
	 * The constant VARIABLE_JOB_NAME.
	 */
	public static final String VARIABLE_JOB_NAME = "${jobName}";

	/**
	 * The constant VARIABLE_ENGINEER_NAME.
	 */
	public static final String VARIABLE_ENGINEER_NAME = "${engineerName}";

	/**
	 * The constant VARIABLE_APPROVER_NAME.
	 */
	public static final String VARIABLE_APPROVER_NAME = "${approverName}";

	/**
	 * The constant VARIABLE_APPROVE_AT.
	 */
	public static final String VARIABLE_APPROVE_AT = "${approveAt}";

	/**
	 * The constant VARIABLE_APPROVED_AT.
	 */
	public static final String VARIABLE_APPROVED_AT = "${approvedAt}";

	/**
	 * The constant VARIABLE_PLANNER_NAME.
	 */
	public static final String VARIABLE_PLANNER_NAME = "${plannerName}";

	/**
	 * The constant VARIABLE_FINISHED_AT.
	 */
	public static final String VARIABLE_FINISHED_AT = "${finishedAt}";

	/**
	 * The constant VARIABLE_EXECUTION_STATUS.
	 */
	public static final String VARIABLE_EXECUTION_STATUS = "${executionStatus}";

	/**
	 * The constant VARIABLE_EXECUTE_AT_FROM.
	 */
	public static final String VARIABLE_EXECUTE_AT_FROM = "${executeAt_From}";

	/**
	 * The constant VARIABLE_EXECUTE_AT_TO.
	 */
	public static final String VARIABLE_EXECUTE_AT_TO = "${executeAt_To}";

	/**
	 * The constant VARIABLE_EXECUTE_DATE.
	 */
	public static final String VARIABLE_EXECUTE_DATE = "${executeDate}";

	/**
	 * The constant VARIABLE_REJECTED_REASON.
	 */
	public static final String VARIABLE_REJECTED_REASON = "${rejectedReason}";

	/**
	 * The constant VARIABLE_ACCEPTED_AT.
	 */
	public static final String VARIABLE_ACCEPTED_AT = "${acceptedAt}";

	/**
	 * The constant VARIABLE_APPROVAL_URL.
	 */
	public static final String VARIABLE_APPROVAL_URL = "${approvalUrl}";

	/**
	 * The constant VARIABLE_REQUEST_AT.
	 */
	public static final String VARIABLE_REQUEST_AT = "${requestAt}";

	/**
	 * The constant VARIABLE_NEW_PD.
	 */
	public static final String VARIABLE_NEW_PD = "${newPassword}";

	/**
	 * The constant VARIABLE_PD.
	 */
	public static final String VARIABLE_PD = "${password}";

	/**
	 * The constant FULL_NAME_DESCRIPTION.
	 */
	public static final String FULL_NAME_DESCRIPTION = "FullName of Recipient";

	/**
	 * The constant NEW_PD_DESCRIPTION.
	 */
	public static final String NEW_PD_DESCRIPTION = "New password generate for User";

	/**
	 * The constant ASSIGNED_JOB_DESCRIPTION.
	 */
	public static final String ASSIGNED_JOB_DESCRIPTION = "Assigned job name";

	/**
	 * The constant NAME_ENGINEER_ASSIGNED_DESCRIPTION.
	 */
	public static final String NAME_ENGINEER_ASSIGNED_DESCRIPTION = "Full name of the engineer is assigned the job";

	/**
	 * The constant TIME_START_DESCRIPTION.
	 */
	public static final String TIME_START_DESCRIPTION = "Time to start executing the job";

	/**
	 * The constant TIME_FINISH_DESCRIPTION.
	 */
	public static final String TIME_FINISH_DESCRIPTION = "Time to finish executing the job";

	/**
	 * The constant DATE_EXECUTE_DESCRIPTION.
	 */
	public static final String DATE_EXECUTE_DESCRIPTION = "Date to execute the job";

	/**
	 * The constant JOB_NAME_DESCRIPTION.
	 */
	public static final String JOB_NAME_DESCRIPTION = "Job name";

	/**
	 * The constant REJECTED_REASON_DESCRIPTION.
	 */
	public static final String REJECTED_REASON_DESCRIPTION = "Rejected reason";

	/**
	 * The constant ACCEPTED_TIME_DATE_DESCRIPTION.
	 */
	public static final String ACCEPTED_TIME_DATE_DESCRIPTION = "Accepted time & date";

	/**
	 * The constant NAME_APPROVER_DESCRIPTION.
	 */
	public static final String NAME_APPROVER_DESCRIPTION = "FullName of the approver";

	/**
	 * The constant TIME_REQUEST_DESCRIPTION.
	 */
	public static final String TIME_REQUEST_DESCRIPTION = "Time Request";

	/**
	 * The constant URL_APPROVAL_DESCRIPTION.
	 */
	public static final String URL_APPROVAL_DESCRIPTION = "Url that link to the approval request list";

	/**
	 * The constant NAME_ENGINEER_DESCRIPTION.
	 */
	public static final String NAME_ENGINEER_DESCRIPTION = "FullName of Engineer";

	/**
	 * The constant TIME_APPROVAL_DESCRIPTION.
	 */
	public static final String TIME_APPROVAL_DESCRIPTION = "Time approval";

	/**
	 * The constant TIME_REJECTED_DESCRIPTION.
	 */
	public static final String TIME_REJECTED_DESCRIPTION = "Time rejected";

	/**
	 * The constant TIME_EXECUTE_DESCRIPTION.
	 */
	public static final String TIME_EXECUTE_DESCRIPTION = "Time execute";

	/**
	 * The constant ENGINEER_NAME_DESCRIPTION.
	 */
	public static final String ENGINEER_NAME_DESCRIPTION = "Engineer name";

	/**
	 * The constant EXECUTION_STATUS_DESCRIPTION.
	 */
	public static final String EXECUTION_STATUS_DESCRIPTION = "Execution status";

	/**
	 * The constant PLANNER_NAME_DESCRIPTION.
	 */
	public static final String PLANNER_NAME_DESCRIPTION = "Planner name";

	/**
	 * RESET_PASSWORD_VARIABLE
	 */
	private static final List<EmailTemplateVariable> RESET_PASSWORD_VARIABLE  = new ArrayList<>();
	/**
	 * FORGOT_PASSWORD_VARIABLE
	 */
	private static final List<EmailTemplateVariable> FORGOT_PASSWORD_VARIABLE  = new ArrayList<>();
	/**
	 * CREATE_ACCOUNT_VARIABLE
	 */
	private static final List<EmailTemplateVariable> CREATE_ACCOUNT_VARIABLE  = new ArrayList<>();
	/**
	 * NEW_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> NEW_JOB_ASSIGN_VARIABLE  = new ArrayList<>();
	/**
	 * UPDATE_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> UPDATE_JOB_ASSIGN_VARIABLE  = new ArrayList<>();
	/**
	 * DELETE_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> DELETE_JOB_ASSIGN_VARIABLE  = new ArrayList<>();
	/**
	 * ACCEPT_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> ACCEPT_JOB_ASSIGN_VARIABLE  = new ArrayList<>();
	/**
	 * REJECT_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> REJECT_JOB_ASSIGN_VARIABLE = new ArrayList<>();
	/**
	 * HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE
	 */
	private static final List<EmailTemplateVariable> HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE = new ArrayList<>();
	/**
	 * CREATE_JOB_VARIABLE
	 */
	private static final List<EmailTemplateVariable> CREATE_JOB_VARIABLE  = new ArrayList<>();

	/**
	 * APPROVE_JOB_VARIABLE
	 */
	private static final List<EmailTemplateVariable> APPROVE_JOB_VARIABLE  = new ArrayList<>();
	/**
	 * REJECT_JOB_VARIABLE
	 */
	private static final List<EmailTemplateVariable> REJECT_JOB_VARIABLE  = new ArrayList<>();
	/**
	 * ENGINEER_EXECUTE_FINISH_VARIABLE
	 */
	private static final List<EmailTemplateVariable> ENGINEER_EXECUTE_FINISH_VARIABLE  = new ArrayList<>();
	/**
	 * PLANNER_EXECUTE_FINISH_VARIABLE
	 */
	private static final List<EmailTemplateVariable> PLANNER_EXECUTE_FINISH_VARIABLE  = new ArrayList<>();
    static {
    	//reset
    	RESET_PASSWORD_VARIABLE.add(new EmailTemplateVariable(1,VARIABLE_FULL_NAME, FULL_NAME_DESCRIPTION));
    	RESET_PASSWORD_VARIABLE.add(new EmailTemplateVariable(2,VARIABLE_NEW_PD, NEW_PD_DESCRIPTION));
    	RESET_PASSWORD_VARIABLE.add(new EmailTemplateVariable(3,VARIABLE_SERVER_URL, "URL of server"));
    	RESET_PASSWORD_VARIABLE.add(new EmailTemplateVariable(4,VARIABLE_EMAIL, "Email of Recipient"));
    	//forgot
    	FORGOT_PASSWORD_VARIABLE.add(new EmailTemplateVariable(5,VARIABLE_FULL_NAME, FULL_NAME_DESCRIPTION));
    	FORGOT_PASSWORD_VARIABLE.add(new EmailTemplateVariable(6,VARIABLE_NEW_PD, NEW_PD_DESCRIPTION));
    	//create
    	CREATE_ACCOUNT_VARIABLE.add(new EmailTemplateVariable(7,VARIABLE_FULL_NAME, FULL_NAME_DESCRIPTION));
    	CREATE_ACCOUNT_VARIABLE.add(new EmailTemplateVariable(8,VARIABLE_NEW_PD, NEW_PD_DESCRIPTION));
    	CREATE_ACCOUNT_VARIABLE.add(new EmailTemplateVariable(9,VARIABLE_SERVER_URL, "URL of server"));
    	CREATE_ACCOUNT_VARIABLE.add(new EmailTemplateVariable(10,VARIABLE_EMAIL, "Email of Recipient"));
    	//new job assign
		NEW_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(11,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		NEW_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(12,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		NEW_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(13,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		NEW_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(14,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		NEW_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(15,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));
		//update job assign
		UPDATE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(16,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		UPDATE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(17,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		UPDATE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(18,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		UPDATE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(19,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		UPDATE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(20,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));
		//delete job assign
		DELETE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(21,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		DELETE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(22,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		DELETE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(23,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		DELETE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(24,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		DELETE_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(25,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));
		//accept job assign
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(26,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(27,VARIABLE_PLANNER_NAME, ASSIGNED_JOB_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(28,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(29,VARIABLE_ACCEPTED_AT, ACCEPTED_TIME_DATE_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(30,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(31,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(32,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));
		//reject job assign
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(33,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(34,VARIABLE_PLANNER_NAME, ASSIGNED_JOB_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(35,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(36,VARIABLE_REJECTED_AT, ACCEPTED_TIME_DATE_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(37,VARIABLE_REJECTED_REASON, REJECTED_REASON_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(38,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(39,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		REJECT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(40,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));
		//have not accept job assign
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(41,VARIABLE_JOB_NAME, ASSIGNED_JOB_DESCRIPTION));
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(42,VARIABLE_PLANNER_NAME, ASSIGNED_JOB_DESCRIPTION));
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(43,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_ASSIGNED_DESCRIPTION));
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(44,VARIABLE_EXECUTE_AT_FROM, TIME_START_DESCRIPTION));
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(45,VARIABLE_EXECUTE_AT_TO, TIME_FINISH_DESCRIPTION));
		HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE.add(new EmailTemplateVariable(46,VARIABLE_EXECUTE_DATE, DATE_EXECUTE_DESCRIPTION));

		//create job
		CREATE_JOB_VARIABLE.add(new EmailTemplateVariable(47,VARIABLE_JOB_NAME, JOB_NAME_DESCRIPTION));
		CREATE_JOB_VARIABLE.add(new EmailTemplateVariable(48,VARIABLE_APPROVER_NAME, NAME_APPROVER_DESCRIPTION));
		CREATE_JOB_VARIABLE.add(new EmailTemplateVariable(49,VARIABLE_REQUEST_AT, TIME_REQUEST_DESCRIPTION));
		CREATE_JOB_VARIABLE.add(new EmailTemplateVariable(50,VARIABLE_APPROVAL_URL, URL_APPROVAL_DESCRIPTION));
		//approve job
		APPROVE_JOB_VARIABLE.add(new EmailTemplateVariable(51,VARIABLE_JOB_NAME, JOB_NAME_DESCRIPTION));
		APPROVE_JOB_VARIABLE.add(new EmailTemplateVariable(52,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_DESCRIPTION));
		APPROVE_JOB_VARIABLE.add(new EmailTemplateVariable(53,VARIABLE_APPROVER_NAME, NAME_APPROVER_DESCRIPTION));
		APPROVE_JOB_VARIABLE.add(new EmailTemplateVariable(54,VARIABLE_APPROVE_AT, TIME_APPROVAL_DESCRIPTION));
		//reject job
		REJECT_JOB_VARIABLE.add(new EmailTemplateVariable(55,VARIABLE_JOB_NAME, JOB_NAME_DESCRIPTION));
		REJECT_JOB_VARIABLE.add(new EmailTemplateVariable(56,VARIABLE_ENGINEER_NAME, NAME_ENGINEER_DESCRIPTION));
		REJECT_JOB_VARIABLE.add(new EmailTemplateVariable(57,VARIABLE_APPROVER_NAME, NAME_APPROVER_DESCRIPTION));
		REJECT_JOB_VARIABLE.add(new EmailTemplateVariable(58,VARIABLE_REJECTED_AT, TIME_REJECTED_DESCRIPTION));
		//engineer execute job finish
		ENGINEER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(59,VARIABLE_ENGINEER_NAME, ENGINEER_NAME_DESCRIPTION));
		ENGINEER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(60,VARIABLE_JOB_NAME, JOB_NAME_DESCRIPTION));
		ENGINEER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(61,VARIABLE_FINISHED_AT, TIME_EXECUTE_DESCRIPTION));
		ENGINEER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(62,VARIABLE_EXECUTION_STATUS, EXECUTION_STATUS_DESCRIPTION));
		//planner execute job finish
		PLANNER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(63,VARIABLE_PLANNER_NAME, PLANNER_NAME_DESCRIPTION));
		PLANNER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(64,VARIABLE_JOB_NAME, JOB_NAME_DESCRIPTION));
		PLANNER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(65,VARIABLE_FINISHED_AT, TIME_EXECUTE_DESCRIPTION));
		PLANNER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(66,VARIABLE_EXECUTION_STATUS, EXECUTION_STATUS_DESCRIPTION));
		PLANNER_EXECUTE_FINISH_VARIABLE.add(new EmailTemplateVariable(67,VARIABLE_ENGINEER_NAME, ENGINEER_NAME_DESCRIPTION));
    }


	public static List<EmailTemplateVariable> getResetPasswordVariable() {
		return RESET_PASSWORD_VARIABLE;
	}

	public static List<EmailTemplateVariable> getForgotPasswordVariable() {
		return FORGOT_PASSWORD_VARIABLE;
	}

	public static List<EmailTemplateVariable> getCreateAccountVariable() {
		return CREATE_ACCOUNT_VARIABLE;
	}

	public static List<EmailTemplateVariable> getNewJobAssignVariable() {
		return NEW_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getUpdateJobAssignVariable() {
		return UPDATE_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getDeleteJobAssignVariable() {
		return DELETE_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getAcceptJobAssignVariable() {
		return ACCEPT_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getRejectJobAssignVariable() {
		return REJECT_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getHaveNotAcceptJobAssignVariable() {
		return HAVE_NOT_ACCEPT_JOB_ASSIGN_VARIABLE;
	}

	public static List<EmailTemplateVariable> getCreateJobVariable() {
		return CREATE_JOB_VARIABLE;
	}

	public static List<EmailTemplateVariable> getApproveJobVariable() {
		return APPROVE_JOB_VARIABLE;
	}

	public static List<EmailTemplateVariable> getRejectJobVariable() {
		return REJECT_JOB_VARIABLE;
	}

	public static List<EmailTemplateVariable> getEngineerExecuteFinishVariable() {
		return ENGINEER_EXECUTE_FINISH_VARIABLE;
	}

	public static List<EmailTemplateVariable> getPlannerExecuteFinishVariable() {
		return PLANNER_EXECUTE_FINISH_VARIABLE;
	}
}
