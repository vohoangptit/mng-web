/**
 * 
 */
package com.nera.nms.constants;

/**
 * The type Email template contants.
 */
public class EmailTemplateContants {

	private EmailTemplateContants() {
	}

	/**
	 * RESET_PASSWORD_TEMPLATE_NAME
	 */
	public static final String RESET_PW_TEMPLATE_NAME = "Reset Password Email";

	/**
	 * FORGOT_PASSWORD_TEMPLATE_NAME
	 */
	public static final String FORGOT_PW_TEMPLATE_NAME = "Forgot Password Email";

	/**
	 * CREATE_ACCOUNT_TEMPLATE_NAME
	 */
	public static final String CREATE_ACCOUNT_TEMPLATE_NAME = "Create Account Email";

	/**
	 * NEW_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String NEW_JOB_ASSIGNMENT_TEMPLATE_NAME = "New Job Assignment Email";

	/**
	 * UPDATE_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String UPDATE_JOB_ASSIGNMENT_TEMPLATE_NAME = "Update Job Assignment Email";

	/**
	 * DELETE_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String DELETE_JOB_ASSIGNMENT_TEMPLATE_NAME = "Delete Job Assignment Email";

	/**
	 * ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME = "Accept Job Assignment Email";

	/**
	 * REJECT_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String REJECT_JOB_ASSIGNMENT_TEMPLATE_NAME = "Reject Job Assignment Email";

	/**
	 * HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME
	 */
	public static final String HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME = "Have Not Accept Job Assignment Email";

	/**
	 * CREATE_JOB_TEMPLATE_NAME
	 */
	public static final String CREATE_JOB_TEMPLATE_NAME = "New Job Execution Approval Request";
	/**
	 * APPROVE_JOB_TEMPLATE_NAME
	 */
	public static final String APPROVE_JOB_TEMPLATE_NAME = "Approve Job Execution Approval Request";
	/**
	 * REJECT_JOB_TEMPLATE_NAME
	 */
	public static final String REJECT_JOB_TEMPLATE_NAME = "Reject Job Execution Approval Request";
	/**
	 * FINISH_JOB_TEMPLATE_NAME
	 */
	public static final String FINISH_JOB_TEMPLATE_NAME = "Job Execution Finish";
	/**
	 * RESET_PASSWORD_SUBJECT
	 */
	public static final String RESET_PW_SUBJECT = "[NERA-Network Management Platform]Reset Password Request";

	/**
	 * FORGOT_PASSWORD_SUBJECT
	 */
	public static final String FORGOT_PW_SUBJECT = "[NERA-Network Management Platform]Forgot Password Request";

	/**
	 * CREATE_ACCOUNT_SUBJECT
	 */
	public static final String CREATE_ACCOUNT_SUBJECT = "[NERA-Network Management Platform] Create Account Confirm";

	/**
	 * NEW_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String NEW_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] New Job Assignment - ${jobName}";

	/**
	 * UPDATE_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String UPDATE_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] Update Job Assignment - ${jobName}";

	/**
	 * DELETE_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String DELETE_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] Delete Job Assignment - ${jobName}";

	/**
	 * ACCEPT_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String ACCEPT_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] Accept Job Assignment - ${jobName}";

	/**
	 * REJECT_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String REJECT_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] Reject Job Assignment - ${jobName}";

	/**
	 * HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_SUBJECT
	 */
	public static final String HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_SUBJECT = "[NERA-Network Management Platform] Non-Action Job Assignment - ${jobName}";

	/**
	 * CREATE_NEW_JOB_SUBJECT
	 */
	public static final String CREATE_JOB_SUBJECT = "[NERA-Network Management Platform] Asking for job execution approval - ${jobName}";

	/**
	 * APPROVE_JOB_SUBJECT
	 */
	public static final String APPROVE_JOB_SUBJECT = "[NERA-Network Management Platform] APPROVED for job execution approval - ${jobName}";

	/**
	 * REJECT_JOB_SUBJECT
	 */
	public static final String REJECT_JOB_SUBJECT = "[NERA-Network Management Platform] REJECTED for job execution approval - ${jobName}";

	/**
	 * FINISH_JOB_SUBJECT
	 */
	public static final String FINISH_JOB_SUBJECT = "[NERA-Network Management Platform] FINISHED job Execution - ${jobName}";

	/**
	 * The constant CONST_EMAIL_OPEN_TABLE.
	 */
	public static final String CONST_EMAIL_OPEN_TABLE = " <table align=\"center\" border=\"1\" cellpadding=\"1\" cellspacing=\"2\" style=\"width:70%\"><tbody>\r\n";

	/**
	 * The constant CONST_EMAIL_CLOSE_TABLE.
	 */
	public static final String CONST_EMAIL_CLOSE_TABLE = "</tbody></table>\r\n";

	/**
	 * The constant CONST_DEAR_ENGINEER.
	 */
	public static final String CONST_DEAR_ENGINEER = "<p>Dear ${engineerName},<br/><br/>\r\n";

	/**
	 * The constant CONST_DEAR_PLANNER.
	 */
	public static final String CONST_DEAR_PLANNER = "Dear ${plannerName},<br/><br/>\r\n";

	/**
	 * The constant CONST_REGARDS.
	 */
	public static final String CONST_REGARDS = " Regards,<br/><br/>\r\n";

	/**
	 * The constant CONST_SYSTEM.
	 */
	public static final String CONST_SYSTEM = " System Administrator</p>\r\n";

	/**
	 * The constant CONST_CONTENT_JOB_NAME.
	 */
	public static final String CONST_CONTENT_JOB_NAME = "<tr><td>Job Name</td><td>&nbsp;${jobName}</td></tr>\r\n";

	/**
	 * The constant CONST_CONTENT_JOB_EXECUTE.
	 */
	public static final String CONST_CONTENT_JOB_EXECUTE = "<tr><td>Job Execute Date</td><td>&nbsp;${executeAt_From} - ${executeAt_To} ${executeDate}</td></tr>\r\n";

	/**
	 * The constant CONST_IMAGE_MAIL.
	 */
	public static final String CONST_IMAGE_MAIL =
			" <p><a href=\"data:image/png;base64,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\"><img alt=\"\" src=\"data:image/png;base64,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\" style=\"height:31px; width:200px\" /></a></p>\r\n";

	/**
	 * The constant CREATE_ACCOUNT_CONTENT.
	 */
	public static final String CREATE_ACCOUNT_CONTENT = "<p>Dear ${fullName},<br/><br/>\r\n" +
    		" Thank you for registering with NERA-Network Management Platform.<br/><br/>" +
    		" Your login details are as shown below :<br/>" +
			CONST_EMAIL_OPEN_TABLE +
    		" 		<tr><td>User</td><td>&nbsp;${email}</td></tr>" +
    		" 		<tr><td>Password</td><td>&nbsp;${password}</td></tr>" +
			CONST_EMAIL_CLOSE_TABLE +
    		" <p><br/>" +
    		" You can login in via this URL link: ${serverURL}<br/> <br/>" +
    		" Regards,<br/>" +
    		" <br/>" +
    		" Nera System</p>" +
			CONST_IMAGE_MAIL +
    		" ";

	/**
	 * The constant RESET_PW_CONTENT.
	 */
	public static final String RESET_PW_CONTENT = "<p>Dear ${fullName},</p>\r\n" +
    		" <p>We have received your reset password request, please find your new password as shown below :</p>" +
			CONST_EMAIL_OPEN_TABLE +
    		" 		<tr><td>User</td><td>&nbsp;${email}</td></tr>" +
    		" 		<tr><td>Password</td><td>&nbsp;${newPassword}</td></tr>" +
			CONST_EMAIL_CLOSE_TABLE +
    		" <p>You can login in via this URL link: ${serverURL}</p>" +
    		" <p>Thanks,</p>" +
    		" <p>Nera Team</p>" +
			CONST_IMAGE_MAIL +
    		" ";

	/**
	 * The constant FORGOT_PW_CONTENT.
	 */
	public static final String FORGOT_PW_CONTENT = "<p>&nbsp;</p>" +
    		" <table cellpadding=\"0\" cellspacing=\"0\" style=\"height:100%; width:100%\">" +
    		" 	<tbody>" +
    		" 		<tr><td style=\"text-align:center\">" +
    		" 			<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:100%; width:100%\">" +
    		" 				<tbody>" +
    		" 					<tr>" +
    		" 						<td>" +
    		" 						<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%\">" +
    		" 							<tbody>" +
    		" 								<tr><td><h2><strong>RESET YOUR PASSWORD&nbsp;</strong></h2></td></tr>\r\n" +
    		" 								<tr><td style=\"vertical-align:top; width:100%\"><em>Dear </em><strong>${fullName}</strong>,&nbsp;<em>You&#39;re receiving this e-mail because you requested a password reset for your Nera System account, because you cannot remember your password</em></td></tr>\r\n" +
    		" 								<tr><td style=\"vertical-align:top; width:100%\"><em>&nbsp;please find your new password as shown below :</em></td></tr>\r\n" +
    		" 								<tr><td><strong>${newPassword}</strong></td></tr>\r\n" +
    		" 							</tbody>" +
    		" 						</table>" +
    		" 						</td>" +
    		" 					</tr>" +
    		" 				</tbody>" +
    		" 			</table>" +
    		" 			</td>" +
    		" 		</tr>" +
    		" 	</tbody>" +
    		" </table>" +
    		" <p>Thanks,</p>" +
    		" <p>Nera Team</p>" +
    		" <p><a href=\"data:image/png;base64,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\"><img alt=\"\" src=\"data:image/png;base64,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\" style=\"width: 100px; height: 50px;\" /></a></p>\r\n" +
    		" ";

	/**
	 * The constant NEW_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String NEW_JOB_ASSIGNMENT_CONTENT = "Dear ${engineerName}, <br/><br/>\r\n" +
			"You have been assigned to a new job as shown below:" +
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE+
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant UPDATE_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String UPDATE_JOB_ASSIGNMENT_CONTENT = "Dear ${engineerName}, <br/><br/>\r\n" +
			"Your current job assignment ${jobName} has been updated as shown below:" +
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE +
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant DELETE_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String DELETE_JOB_ASSIGNMENT_CONTENT = "Dear ${engineerName},<br/><br/>\r\n" +
			"Please take note that your assigned job ${jobName} has been deleted"+
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE +
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant ACCEPT_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String ACCEPT_JOB_ASSIGNMENT_CONTENT = CONST_DEAR_PLANNER +
			"Your job assignment has been accepted by ${engineerName} on ${acceptedAt}" +
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE +
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant REJECT_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String REJECT_JOB_ASSIGNMENT_CONTENT = CONST_DEAR_PLANNER +
			"Your job assignment has been rejected by ${engineerName} on ${rejectedAt} <br/>" +
			"Rejected reason is: ${rejectedReason}" +
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE +
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_CONTENT.
	 */
	public static final String HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_CONTENT = CONST_DEAR_PLANNER +
			"Your job assignment has not been acknowledged  by ${engineerName}" +
			CONST_EMAIL_OPEN_TABLE +
			CONST_CONTENT_JOB_NAME +
			CONST_CONTENT_JOB_EXECUTE +
			CONST_EMAIL_CLOSE_TABLE +
			CONST_REGARDS +
			CONST_SYSTEM;

	/**
	 * The constant CREATE_JOB_CONTENT.
	 */
	public static final String CREATE_JOB_CONTENT = "<p>Dear ${approverName},<br/><br/>\r\n" +
			"You have been sent the job execution approval for the ${jobName} at ${requestAt} <br/><br/>\r\n" +
			" Please click on the link below to proceed with this request. <br/><br/>\r\n" +
			" <a href=\"${approvalUrl}\">${approvalUrl}</a> <br/><br/>\r\n" +
			"<p><i>Notes: Our engineer is still waiting for your approval to continue the job execution process.</i></p> <br/>\r\n" +
			CONST_REGARDS +
			CONST_SYSTEM +
			CONST_IMAGE_MAIL +
			" ";

	/**
	 * The constant APPROVE_JOB_CONTENT.
	 */
	public static final String APPROVE_JOB_CONTENT = CONST_DEAR_ENGINEER +
			"Your job execution approval request for the ${jobName} has been approved at ${approvedAt} by ${approverName}. <br/><br/>\n" +
			CONST_REGARDS +
			CONST_SYSTEM +
			CONST_IMAGE_MAIL +
			" ";

	/**
	 * The constant REJECT_JOB_CONTENT.
	 */
	public static final String REJECT_JOB_CONTENT = CONST_DEAR_ENGINEER +
			"Your job execution approval request for the ${jobName} has been rejected at ${rejectedAt} by ${approverName}. <br/><br/>\r\n" +
			CONST_REGARDS +
			CONST_SYSTEM +
			CONST_IMAGE_MAIL +
			" ";

	/**
	 * The constant ENGINEER_EXECUTION_FINISH_CONTENT.
	 */
	public static final String ENGINEER_EXECUTION_FINISH_CONTENT = CONST_DEAR_ENGINEER +
			" The Job ${jobName} finished to execute at ${finishedAt} ${executionStatus} <br/><br/>\n" +
			" For more information, please find out job execution log under attachment. <br/><br/>\r\n" +
			CONST_REGARDS +
			CONST_SYSTEM +
			CONST_IMAGE_MAIL +
			" ";

	/**
	 * The constant PLANNER_EXECUTION_FINISH_CONTENT.
	 */
	public static final String PLANNER_EXECUTION_FINISH_CONTENT = "<p>Dear ${plannerName},<br/><br/>\r\n" +
			" The Job ${jobName} finished to execute at ${finishedAt} ${executionStatus} by ${engineerName}<br/><br/>\r\n" +
			" For more information, please find out job execution log under attachment. <br/><br/>\r\n" +
			CONST_REGARDS +
			CONST_SYSTEM +
			CONST_IMAGE_MAIL +
			" ";
}
