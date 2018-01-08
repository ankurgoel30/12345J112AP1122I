package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.ApplicationConstants.EMAIL_BODY;
import static com.thinkhr.external.api.ApplicationConstants.RESET_PASSWORD_PREFIX;
import static com.thinkhr.external.api.services.utils.CommonUtil.generateHashedValue;
import static com.thinkhr.external.api.services.utils.EmailUtil.BROKER_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.DEFAULT_EMAIL_TEMPLATE_BROKERID;
import static com.thinkhr.external.api.services.utils.EmailUtil.FIRST_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.SENDGRID_END_POINT;
import static com.thinkhr.external.api.services.utils.EmailUtil.SET_LOGIN_LINK;
import static com.thinkhr.external.api.services.utils.EmailUtil.SET_PASSWORD_LINK;
import static com.thinkhr.external.api.services.utils.EmailUtil.SUPPORT_EMAIL;
import static com.thinkhr.external.api.services.utils.EmailUtil.SUPPORT_PHONE;
import static com.thinkhr.external.api.services.utils.EmailUtil.USER_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.prepareResetPasswordlink;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.EmailConfiguration;
import com.thinkhr.external.api.db.entities.EmailTemplate;
import com.thinkhr.external.api.db.entities.SetPasswordRequest;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.KeyValuePair;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.EmailTemplateRepository;
import com.thinkhr.external.api.repositories.SetPasswordRequestRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.services.utils.EmailUtil;

import lombok.Data;

/**
 * Service class to work with email entities, repositories and SendGrid APIs.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Service
@Data
public class EmailService {
    
    private Logger logger = LoggerFactory.getLogger(EmailService.class);
 
    @Autowired
    private EmailTemplateRepository emailRepository;
    
    @Autowired
    private SetPasswordRequestRepository setPasswordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CompanyRepository companyRepository;

    @Value("${sendgrid_api_key}")
    private String apiKey;

    @Value("${sendgrid_key_name}")
    private String keyName;

    @Value("${sendgrid_auth_template_id}")
    private String authTemplateId;
    
    @Value("${sendgrid_channel_template_id}")
    private String channelTemplateId;
    
    @Value("${default_support_email}")
    private String defaultSupportEmail;
    
    @Value("${default_support_phone}")
    private String defaultSupportPhone;
    
    @Value("${login_url}")
    private String loginUrl;
    
    /**
     * To get email template for given parameters
     * 
     * @param brokerId
     * @param type
     * @return
     */
    public EmailTemplate getEmailTemplate(Integer brokerId, String type) {
        return emailRepository.findFirstByBrokerIdAndType(brokerId, type); 
    }
    
    /**
     * Fetch email template for default configurations
     * 
     * @return
     */
    public EmailTemplate getDefaultEmailTemplate() {
        return getEmailTemplate(DEFAULT_EMAIL_TEMPLATE_BROKERID,
                ApplicationConstants.WELCOME_EMAIL_TYPE);
    }
    
    /**
     * Create EmailRequest 
     * 
     * @param brokerId
     * @param username
     */
    public EmailRequest createEmailRequest(Integer brokerId, String username) {
        
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "username", username);
        }

        Company broker = null;
        if (brokerId != null) {
            broker = companyRepository.findOne(brokerId);
        }

        EmailTemplate emailTemplate = getEmailTemplate(brokerId, ApplicationConstants.WELCOME_EMAIL_TYPE);
        if (emailTemplate == null) {
            emailTemplate = getDefaultEmailTemplate();
        }
        
        if (emailTemplate == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.ENTITY_NOT_FOUND, "template", String.valueOf(brokerId));
        }
        
        //String sendgridTemplateId = emailTemplate.getSendgridTemplateId();
        String generatedHashedCode = RESET_PASSWORD_PREFIX + generateHashedValue(user.getUserId());
        String resetPasswordLink = prepareResetPasswordlink(loginUrl, generatedHashedCode);
        
        // Saving SetPasswordRequest record for reset password request.
        saveSetPasswordRequest(user.getUserId(),generatedHashedCode);
        
        List<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(createKeyValue(SET_LOGIN_LINK, loginUrl));
        parameters.add(createKeyValue(FIRST_NAME, user.getFirstName()));
        parameters.add(createKeyValue(BROKER_NAME, broker.getCompanyName()));
        parameters.add(createKeyValue(USER_NAME, user.getUserName()));
        parameters.add(createKeyValue(SUPPORT_PHONE, defaultSupportPhone));
        parameters.add(createKeyValue(SUPPORT_EMAIL, defaultSupportEmail));
        parameters.add(createKeyValue(SET_PASSWORD_LINK, resetPasswordLink));
        
        return createEmailRequest(user, broker, parameters, emailTemplate); 
    }
    
    /**
     * Create EmailRequest
     * 
     * @param user
     * @param broker
     * @param parameters
     * @param body
     * @return
     */
    public EmailRequest createEmailRequest(User user, Company broker, List<KeyValuePair> parameters, EmailTemplate emailTemplate) {
        EmailRequest emailRequest = new EmailRequest();
        
        List<String> toEmail = new ArrayList<String>();
        
        toEmail.add(user.getEmail());
        
        if (emailTemplate != null && emailTemplate.getEmailConfigurations() != null && !emailTemplate.getEmailConfigurations().isEmpty()) {
            for(EmailConfiguration emailConfiguration : emailTemplate.getEmailConfigurations()) {
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(EMAIL_BODY)) {
                    emailRequest.setBody(emailConfiguration.getValue());
                }
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(ApplicationConstants.FROM_EMAIL)) {
                    emailRequest.setFromEmail(emailConfiguration.getValue());
                }
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(ApplicationConstants.EMAIL_SUBJECT)) {
                    emailRequest.setSubject(broker.getCompanyName() + ", " + emailConfiguration.getValue());
                }
            }
        }
        emailRequest.setParameters(parameters);
        emailRequest.setToEmail(toEmail);
        return emailRequest;
    }

    /**
     * Create KeyValue pair
     * 
     * @param key
     * @param value
     * @return
     */
    private KeyValuePair createKeyValue(String key,
            String value) {
        return new KeyValuePair(key, value);
    }

    /**
     * Setting emilRequest to sendgrid.
     * 
     * @param emailRequest
     * @throws Exception
     */
    public void sendEmail(EmailRequest emailRequest) throws Exception {
        Mail mail = EmailUtil.build(emailRequest);
        sendEmail(mail);
    }

    /**
     * Sending email through sendgrid.
     * 
     * @param mail
     * @throws Exception
     */
    public void sendEmail(Mail mail) throws Exception {
        SendGrid sg = new SendGrid(this.apiKey);
        Request request = new Request();
        Response response = null;
        mail.setTemplateId(authTemplateId);
        request.setMethod(Method.POST);
        request.setEndpoint(SENDGRID_END_POINT);
        request.setBody(mail.build());
        response = sg.api(request);
        logger.debug("**************Email Status Code: " + response.getStatusCode());
    }
    
    /**
     * Saving set_passwrod_request object in DB
     * 
     * @param userId
     * @param generatedHashCode
     * @return
     */
    public SetPasswordRequest saveSetPasswordRequest(Integer userId, String generatedHashCode) {
        SetPasswordRequest passwordRequest = new SetPasswordRequest();
        passwordRequest.setContactId(userId);
        passwordRequest.setId(generatedHashCode);
        return setPasswordRepository.save(passwordRequest);
    }

}


