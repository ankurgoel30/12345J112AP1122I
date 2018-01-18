package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.ApplicationConstants.RESET_PASSWORD_PREFIX;
import static com.thinkhr.external.api.services.utils.CommonUtil.generateHashedValue;
import static com.thinkhr.external.api.services.utils.EmailUtil.BROKER_NAME;
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
import org.springframework.beans.factory.annotation.Value;

import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.KeyValuePair;
import com.thinkhr.external.api.services.utils.EmailUtil;

import lombok.Data;

/**
 * EmailService class to work with email entities, repositories and SendGrid APIs.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Data
public class SendGridEmailService extends EmailService {
    
    private Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
 
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
     * Setting emilRequest to sendgrid.
     * 
     * @param emailRequest
     * @throws Exception
     */
    @Override
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
        // SendGrid sg = new SendGrid("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");
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
     * @param broker
     * @param user
     * @return
     */
    @Override
    protected List<KeyValuePair> getEmailSubstituions(Company broker, User user) {
        List<KeyValuePair> substitutions = new ArrayList<KeyValuePair>();
        
        //String sendgridTemplateId = emailTemplate.getSendgridTemplateId();
        String generatedHashedCode = RESET_PASSWORD_PREFIX + generateHashedValue(user.getUserId());
        String resetPasswordLink = prepareResetPasswordlink(loginUrl, generatedHashedCode);

        // Saving SetPasswordRequest record for reset password request.
        saveSetPasswordRequest(user.getUserId(),generatedHashedCode);

        substitutions.add(createKeyValue(SET_LOGIN_LINK, loginUrl));
        substitutions.add(createKeyValue(FIRST_NAME, user.getFirstName()));
        substitutions.add(createKeyValue(BROKER_NAME, broker.getCompanyName()));
        substitutions.add(createKeyValue(USER_NAME, user.getUserName()));
        substitutions.add(createKeyValue(SUPPORT_PHONE, defaultSupportPhone));
        substitutions.add(createKeyValue(SUPPORT_EMAIL, defaultSupportEmail));
        substitutions.add(createKeyValue(SET_PASSWORD_LINK, resetPasswordLink));
        return substitutions;
    }
    
}