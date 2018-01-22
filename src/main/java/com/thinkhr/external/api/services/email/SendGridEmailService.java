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
import com.thinkhr.external.api.exception.ApplicationException;
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
    
    /**
     * Setting emilRequest to sendgrid.
     * 
     * @param emailRequest
     * @throws Exception
     */
    @Override
    public void sendEmail(EmailRequest emailRequest) throws ApplicationException {
        Mail mail = EmailUtil.build(emailRequest);
        try {
            sendEmail(mail);
        } catch(Exception ex) {
           // TODO: throw ApplicationException.createException(APIErrorCodes.SEND_EMAIL, ex);
        }
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
        mail.setTemplateId(authTemplateId);
        request.setMethod(Method.POST);
        request.setEndpoint(SENDGRID_END_POINT);
        request.setBody(mail.build());
        Response response = sg.api(request);
        logger.debug("**************Email Status Code: " + response.getStatusCode());
    }
    
}