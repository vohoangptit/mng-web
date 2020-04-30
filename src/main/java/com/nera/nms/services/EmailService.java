/**
 * 
 */
package com.nera.nms.services;

import com.nera.nms.components.MailComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.VariableEmailTemplateConstants;
import com.nera.nms.dto.SystemSettingDto;
import com.nera.nms.dto.UserEmailTemplateDTO;
import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.SystemSetting;
import com.nera.nms.models.User;
import com.nera.nms.models.UserEmailTemplate;
import com.nera.nms.repositories.IUserEmailTemplateRepository;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.BeanConvertUtils;
import com.nera.nms.utils.UrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Martin Do
 *
 */
@Service
public class EmailService {

    @Autowired
    public MailComponent mailComponent;
        
    @Value("${message.email.new.account.title}")
    private String newAccountEmailTitle;
    
    @Value("${message.email.forgot.password.title}")
    private String forgotPasswordEmailTitle;
    
    @Value("${message.email.reset.password.title}")
    private String resetPasswordEmailTitle;

    @Autowired
    MessageSource messageSource;

    @Autowired
    IUserEmailTemplateRepository emailTemplateRepository;

    @Autowired
    UserService userService;

    private final DateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy");

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void sendEmail(String newPassword, String email) {
        JavaMailSender javaMailSender = mailComponent.getJavaMailSender();
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText(StringUtils.join("your new password is: ", newPassword));
 
        // Send Message!
        javaMailSender.send(message);
    }
    
    public void testSendEmail(SystemSettingDto systemSettingDto) {
        JavaMailSender javaMailSender = mailComponent.getJavaMailSender(systemSettingDto);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("vohoangptit@gmail.com");
        message.setSubject("Test send email");
        message.setText("Test send email");
 
        // Send Message!
        javaMailSender.send(message);
    }
    
    public void testSendEmail2(String to, SystemSettingDto systemSettingDto){
    	try {
    		Email email = EmailBuilder.startingBlank()
        		    .from(systemSettingDto.getUsername().trim(), systemSettingDto.getEmail())
        		    .to(to)
        		    .withSubject("Test Mail")
        		    .withPlainText("This is text email")
        		    .buildEmail();
        	MailerBuilder
            	.withSMTPServer(systemSettingDto.getSmtpHost(), systemSettingDto.getSmtpPort(), systemSettingDto.getEmail(), systemSettingDto.getPassword())
            	.withTransportStrategy(systemSettingDto.getSmtpPort()== 587 ? TransportStrategy.SMTP_TLS :TransportStrategy.SMTPS)
            	.buildMailer()
            	.sendMail(email);
    	} catch(Exception e) {
    	    logger.error("EmailService.testSendEmail2 : ", e);
    	}
    }
    
    private void simpleSendMail(String to, String subject, String content, SystemSetting systemSetting, Mailer mailer){
    	try {
            Email email = EmailBuilder.startingBlank()
        		    .from(systemSetting.getUsername().trim(), systemSetting.getEmail())
        		    .to(to)
        		    .withSubject(subject)
        		    .withHTMLText(content)
        		    .buildEmail();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> mailer.sendMail(email));
            executorService.shutdown();
    	} catch(Exception e) {
    	    logger.error("EmailService.simpleSendMail : ", e);
    	}
    }

    private void attachmentSendMail(String to, String subject, String content, SystemSetting systemSetting, Mailer mailer, DataSource source, String nameFile){
        try {
            Email email = EmailBuilder.startingBlank()
                    .from(systemSetting.getUsername().trim(), systemSetting.getEmail())
                    .to(to)
                    .withSubject(subject)
                    .withHTMLText(content)
                    .withAttachment(nameFile, source)
                    .buildEmail();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> mailer.sendMail(email));
            executorService.shutdown();
        } catch(Exception e) {
            logger.error("EmailService.attachmentSendMail : ", e);
        }
    }

    public boolean sendResetPasswordEmail(String newPassword, String email, String fullName, HttpServletRequest request) {
        try {
            SystemSetting javaSender = mailComponent.getPropertiesMailSender();
            UserEmailTemplate template = emailTemplateRepository.getById(1L);
            String serverUrl = UrlBuilder.getServerUrl(request);
            String content = template.getTemplateContent();
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_FULL_NAME,fullName);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_NEW_PD,newPassword);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_SERVER_URL,serverUrl);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_EMAIL,email);
            Mailer mailer = getMailer(javaSender);
            mailer.testConnection();
            this.simpleSendMail(email, template.getSubject(), content, javaSender, mailer);
        } catch (Exception e) {
            logger.error("EmailService.sendResetPasswordEmail ", e);
            return false;
        }
        return true;
    }

    public boolean sendForgetPasswordEmail(String newPassword, String email, String fullName, HttpServletRequest request) {
        try {
            SystemSetting javaSender = mailComponent.getPropertiesMailSender();
            UserEmailTemplate template = emailTemplateRepository.getById(2L);
            String serverUrl = UrlBuilder.getServerUrl(request);
            String content = template.getTemplateContent();
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_FULL_NAME,fullName);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_NEW_PD,newPassword);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_SERVER_URL,serverUrl);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_EMAIL,email);
            Mailer mailer = getMailer(javaSender);
            mailer.testConnection();
            this.simpleSendMail(email, template.getSubject(), content, javaSender, mailer);
        } catch(Exception e) {
            logger.error("EmailService.sendForgetPasswordEmail ", e);
            return false;
        }
        return true;
    }
    
    public boolean sendEmailForNewAccount(String newPassword, String email, String fullName, HttpServletRequest request) {
        try {
            SystemSetting javaSender = mailComponent.getPropertiesMailSender();
            UserEmailTemplate template = emailTemplateRepository.getById(3L);
            String serverUrl = UrlBuilder.getServerUrl(request);
            String content = template.getTemplateContent();
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_FULL_NAME,fullName);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_PD,newPassword);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_SERVER_URL,serverUrl);
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_EMAIL,email);
            Mailer mailer = getMailer(javaSender);
            mailer.testConnection();
            this.simpleSendMail(email, template.getSubject(), content, javaSender, mailer);
        } catch (Exception e) {
            logger.error("EmailService.sendEmailForNewAccount ", e);
            return false;
        }
        return true;
    }

    public void sendJobAssignmentEmail(JobAssignment jobAssignment, User assignee, User planner, Long idMailTemplate, boolean isPlanner) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template = emailTemplateRepository.getById(idMailTemplate);
        String content = template.getTemplateContent();
        String jobName = jobAssignment.getJobManagement().getName();
        String mailTo = assignee.getEmail();
        if(isPlanner) {
            mailTo = planner.getEmail();
        }
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ACCEPTED_AT, dateFormat.format(new Date()));
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_REJECTED_AT, dateFormat.format(new Date()));
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ENGINEER_NAME, assignee.getFullName());
        if(StringUtils.isNotBlank(jobAssignment.getReason())) {
            content = content.replace(VariableEmailTemplateConstants.VARIABLE_REJECTED_REASON, jobAssignment.getReason());
        }
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_PLANNER_NAME, planner.getFullName());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_EXECUTE_AT_FROM, jobAssignment.getStartTime());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_EXECUTE_AT_TO, jobAssignment.getEndTime());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_EXECUTE_DATE, dateFormat.format(jobAssignment.getExecutionDate()));
        Mailer mailer = getMailer(javaSender);
        this.simpleSendMail(mailTo, template.getSubject().replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName), content, javaSender, mailer);
    }

    public void sendRequestJobExecutionEmail(String jobName, String requestAt, String approvalUrl, String email) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template = emailTemplateRepository.getById(10L);
        String content = template.getTemplateContent();
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME,jobName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_REQUEST_AT,requestAt);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVAL_URL,approvalUrl);
        String subject = StringUtils.replace(template.getSubject(), VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName);
        String[] emails = StringUtils.split(email, CommonConstants.COMMA);
        User user;
        for (String em : emails) {
            if (StringUtils.isNotBlank(em)) {
                user = userService.findUserByEmail(em.trim());
                if (user != null) {
                    content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVER_NAME, user.getFullName());
                }
                Mailer mailer = getMailer(javaSender);
                this.simpleSendMail(em.trim(), subject, content, javaSender, mailer);
            }
        }
    }

    /**
     * @implNote can be replace by sendApproveOrRejectJobEmail
     */
    public void sendApproveJobEmail(String jobName, String engineerName, String approverName, String approvedAt, String email) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template = emailTemplateRepository.getById(11L);
        String content = template.getTemplateContent();
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME,jobName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ENGINEER_NAME,engineerName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVER_NAME,approverName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVED_AT,approvedAt);
        String subject = StringUtils.replace(template.getSubject(), VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName);
        Mailer mailer = getMailer(javaSender);
        this.simpleSendMail(email, subject, content, javaSender, mailer);
    }

    /**
     * @implNote can be replace by sendApproveOrRejectJobEmail
     */
    public void sendRejectJobEmail(String jobName, String engineerName, String approverName, String rejectedAt, String email) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template = emailTemplateRepository.getById(12L);
        String content = template.getTemplateContent();
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME,jobName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ENGINEER_NAME,engineerName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVER_NAME,approverName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_REJECTED_AT,rejectedAt);
        String subject = StringUtils.replace(template.getSubject(), VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName);
        Mailer mailer = getMailer(javaSender);
        this.simpleSendMail(email, subject, content, javaSender, mailer);
    }

    public void executionFinishSendEmail(JobAssignment jobAssignment, String pathFile, String nameFile, boolean checkUser) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template;
        String emailTo;
        if(checkUser) {
            template = emailTemplateRepository.getById(13L);
            emailTo = jobAssignment.getAssignee().getEmail();
        } else {
            template = emailTemplateRepository.getById(14L);
            emailTo = jobAssignment.getPlanner().getEmail();
        }
        String content = template.getTemplateContent();
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME,jobAssignment.getJobName());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ENGINEER_NAME, jobAssignment.getAssignee().getFullName());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_PLANNER_NAME, jobAssignment.getPlanner().getFullName());
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_FINISHED_AT, BEDateUtils.convertDateToStringByFormatOn(new Date()));
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_EXECUTION_STATUS, jobAssignment.getStatus());
        String subject = StringUtils.replace(template.getSubject(), VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobAssignment.getJobName());
        Mailer mailer = getMailer(javaSender);
        DataSource source = new FileDataSource(pathFile);
        this.attachmentSendMail(emailTo, subject, content, javaSender, mailer, source, nameFile);
    }

    /**
     *
     *
     * @param jobName
     * @param engineerName
     * @param approverName
     * @param rejectedAt put rejectedAt = "" ==> mail will be sent by Approve email template
     * @param approvedAt put approvedAt = "" ==> mail will be sent by Reject email template
     * @param emailReceive
     *
     */
    public void sendApproveOrRejectJobEmail(String jobName, String engineerName, String approverName, String rejectedAt, String approvedAt, String emailReceive) {
        SystemSetting javaSender = mailComponent.getPropertiesMailSender();
        UserEmailTemplate template;
        if (StringUtils.isNotBlank(rejectedAt)) {
            approvedAt = StringUtils.EMPTY;
            template = emailTemplateRepository.getById(12L);
        }else {
            rejectedAt = StringUtils.EMPTY;
            template = emailTemplateRepository.getById(11L);
        }
        String content = template.getTemplateContent();
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_JOB_NAME,jobName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_ENGINEER_NAME,engineerName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVER_NAME,approverName);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_REJECTED_AT,rejectedAt);
        content = content.replace(VariableEmailTemplateConstants.VARIABLE_APPROVED_AT,approvedAt);
        String subject = StringUtils.replace(template.getSubject(), VariableEmailTemplateConstants.VARIABLE_JOB_NAME, jobName);
        Mailer mailer = getMailer(javaSender);
        this.simpleSendMail(emailReceive, subject, content, javaSender, mailer);
    }
    
    public List<UserEmailTemplateDTO> getAllEmailTemplate(String stringSearch){
    	List<UserEmailTemplateDTO> data = BeanConvertUtils.copyList(emailTemplateRepository.findLikeSearchString(stringSearch), UserEmailTemplateDTO.class);
    	AtomicLong numIncrement = new AtomicLong(1);
		data.forEach(object -> object.setNo(numIncrement.getAndIncrement()+1));
		return data;
    }
    
    public void updateEmailTemplate(UserEmailTemplateDTO data){
    	UserEmailTemplate entity = emailTemplateRepository.getById(data.getId());
    	if(entity!=null) {
    		entity.setTemplateName(data.getTemplateName());
    		entity.setTemplateContent(data.getTemplateContent());
    		entity.setSubject(data.getSubject());
    		emailTemplateRepository.save(entity);
    	}
    }
    
    public UserEmailTemplateDTO findById(long id) {
    	UserEmailTemplate entity = null;
    	try {
    		entity = new UserEmailTemplate();
    		entity = emailTemplateRepository.findOneById(id);
    	} catch(Exception e) {
    	    logger.error("EmailService.findById : ", e);
    	}
    	return BeanConvertUtils.createAndCopy(entity, UserEmailTemplateDTO.class);
    }
    
    public boolean findOneByName(String nameTemplate, long id) {
    	UserEmailTemplate entity = emailTemplateRepository.findOneByTemplateName(nameTemplate);
    	return (entity!=null && entity.getId()!=id);
    }

    private Mailer getMailer(SystemSetting systemSetting) {
        return MailerBuilder
                .withSMTPServer(systemSetting.getSmtpHost(), Integer.parseInt(systemSetting.getSmtpPort()), systemSetting.getEmail(), systemSetting.getPassword())
                .withTransportStrategy(Integer.parseInt(systemSetting.getSmtpPort().trim())== 587 ? TransportStrategy.SMTP_TLS :TransportStrategy.SMTPS)
                .buildMailer();
    }
}
