/**
 * 
 */
package com.nera.nms.services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import com.nera.nms.constants.VariableEmailTemplateConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nera.nms.constants.EmailTemplateContants;
import com.nera.nms.models.UserEmailTemplate;
import com.nera.nms.repositories.IUserEmailTemplateRepository;

@Service
public class EmailTemplateInitService {

    @Autowired
    IUserEmailTemplateRepository emailTemplateRepository;

    @PostConstruct
    private void initMailTemplate() {
    	ArrayList<UserEmailTemplate> emailTemplate = new ArrayList<>();
    	
    	UserEmailTemplate resetPasswordEmailTemplate = new UserEmailTemplate(1,
    			EmailTemplateContants.RESET_PW_TEMPLATE_NAME,
    			EmailTemplateContants.RESET_PW_SUBJECT,
    			EmailTemplateContants.RESET_PW_CONTENT, true, false,
				VariableEmailTemplateConstants.getResetPasswordVariable());
    	UserEmailTemplate forgotPasswordEmailTemplate = new UserEmailTemplate(2,
    			EmailTemplateContants.FORGOT_PW_TEMPLATE_NAME,
    			EmailTemplateContants.FORGOT_PW_SUBJECT,
    			EmailTemplateContants.FORGOT_PW_CONTENT, true, false,
				VariableEmailTemplateConstants.getForgotPasswordVariable());
    	UserEmailTemplate createAccountEmailTemplate = new UserEmailTemplate(3,
    			EmailTemplateContants.CREATE_ACCOUNT_TEMPLATE_NAME, 
    			EmailTemplateContants.CREATE_ACCOUNT_SUBJECT, 
    			EmailTemplateContants.CREATE_ACCOUNT_CONTENT, true, false,
				VariableEmailTemplateConstants.getCreateAccountVariable());
		UserEmailTemplate newJobAssignmentEmailTemplate = new UserEmailTemplate(4,
				EmailTemplateContants.NEW_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.NEW_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.NEW_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getNewJobAssignVariable());
		UserEmailTemplate updateJobAssignmentEmailTemplate = new UserEmailTemplate(5,
				EmailTemplateContants.UPDATE_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.UPDATE_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.UPDATE_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getUpdateJobAssignVariable());
		UserEmailTemplate deleteJobAssignmentEmailTemplate = new UserEmailTemplate(6,
				EmailTemplateContants.DELETE_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.DELETE_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.DELETE_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getDeleteJobAssignVariable());
		UserEmailTemplate acceptJobAssignmentEmailTemplate = new UserEmailTemplate(7,
				EmailTemplateContants.ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.ACCEPT_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.ACCEPT_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getAcceptJobAssignVariable());
		UserEmailTemplate rejectJobAssignmentEmailTemplate = new UserEmailTemplate(8,
				EmailTemplateContants.REJECT_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.REJECT_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.REJECT_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getRejectJobAssignVariable());
		UserEmailTemplate haveNotAcceptJobAssignmentEmailTemplate = new UserEmailTemplate(9,
				EmailTemplateContants.HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_TEMPLATE_NAME,
				EmailTemplateContants.HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_SUBJECT,
				EmailTemplateContants.HAVE_NOT_ACCEPT_JOB_ASSIGNMENT_CONTENT, true, false,
				VariableEmailTemplateConstants.getHaveNotAcceptJobAssignVariable());
		UserEmailTemplate createJobEmailTemplate = new UserEmailTemplate(10,
				EmailTemplateContants.CREATE_JOB_TEMPLATE_NAME,
				EmailTemplateContants.CREATE_JOB_SUBJECT,
				EmailTemplateContants.CREATE_JOB_CONTENT, true, false,
				VariableEmailTemplateConstants.getCreateJobVariable());
		UserEmailTemplate approveJobEmailTemplate = new UserEmailTemplate(11,
				EmailTemplateContants.APPROVE_JOB_TEMPLATE_NAME,
				EmailTemplateContants.APPROVE_JOB_SUBJECT,
				EmailTemplateContants.APPROVE_JOB_CONTENT, true, false,
				VariableEmailTemplateConstants.getApproveJobVariable());
		UserEmailTemplate rejectJobEmailTemplate = new UserEmailTemplate(12,
				EmailTemplateContants.REJECT_JOB_TEMPLATE_NAME,
				EmailTemplateContants.REJECT_JOB_SUBJECT,
				EmailTemplateContants.REJECT_JOB_CONTENT, true, false,
				VariableEmailTemplateConstants.getRejectJobVariable());
		UserEmailTemplate engineerExecutionFinishEmailTemplate = new UserEmailTemplate(13,
				EmailTemplateContants.FINISH_JOB_TEMPLATE_NAME,
				EmailTemplateContants.FINISH_JOB_SUBJECT,
				EmailTemplateContants.ENGINEER_EXECUTION_FINISH_CONTENT, true, false,
				VariableEmailTemplateConstants.getEngineerExecuteFinishVariable());
		UserEmailTemplate plannerExecutionFinishEmailTemplate = new UserEmailTemplate(14,
				EmailTemplateContants.FINISH_JOB_TEMPLATE_NAME,
				EmailTemplateContants.FINISH_JOB_SUBJECT,
				EmailTemplateContants.PLANNER_EXECUTION_FINISH_CONTENT, true, false,
				VariableEmailTemplateConstants.getPlannerExecuteFinishVariable());

    	emailTemplate.add(resetPasswordEmailTemplate);
    	emailTemplate.add(forgotPasswordEmailTemplate);
    	emailTemplate.add(createAccountEmailTemplate);
		emailTemplate.add(newJobAssignmentEmailTemplate);
		emailTemplate.add(updateJobAssignmentEmailTemplate);
		emailTemplate.add(deleteJobAssignmentEmailTemplate);
		emailTemplate.add(acceptJobAssignmentEmailTemplate);
		emailTemplate.add(rejectJobAssignmentEmailTemplate);
		emailTemplate.add(haveNotAcceptJobAssignmentEmailTemplate);
		emailTemplate.add(createJobEmailTemplate);
		emailTemplate.add(approveJobEmailTemplate);
		emailTemplate.add(rejectJobEmailTemplate);
		emailTemplate.add(engineerExecutionFinishEmailTemplate);
		emailTemplate.add(plannerExecutionFinishEmailTemplate);

    	if(emailTemplateRepository.findAll().size() < emailTemplate.size()) {
    		emailTemplateRepository.deleteAll();
    		emailTemplateRepository.saveAll(emailTemplate);
    	}
    }
}
