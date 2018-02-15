package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.ApplicationConstants.EMAIL_BODY;
import static com.thinkhr.external.api.ApplicationConstants.EMAIL_SUBJECT;
import static com.thinkhr.external.api.ApplicationConstants.FROM_EMAIL;
import static com.thinkhr.external.api.ApplicationConstants.MAX_SENDGRID_PERSONALISATION;
import static com.thinkhr.external.api.ApplicationConstants.RESET_PASSWORD_PREFIX;
import static com.thinkhr.external.api.services.utils.CommonUtil.generateHashedValue;
import static com.thinkhr.external.api.services.utils.EmailUtil.BROKER_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.DEFAULT_EMAIL_TEMPLATE_BROKERID;
import static com.thinkhr.external.api.services.utils.EmailUtil.FIRST_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.SET_LOGIN_LINK;
import static com.thinkhr.external.api.services.utils.EmailUtil.SET_PASSWORD_LINK;
import static com.thinkhr.external.api.services.utils.EmailUtil.SUPPORT_EMAIL;
import static com.thinkhr.external.api.services.utils.EmailUtil.SUPPORT_PHONE;
import static com.thinkhr.external.api.services.utils.EmailUtil.USER_NAME;
import static com.thinkhr.external.api.services.utils.EmailUtil.prepareResetPasswordlink;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

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
import com.thinkhr.external.api.services.CommonService;

import lombok.Data;

/**
 * Abstract classs for sending email.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-15
 *
 */
@Data
public abstract class EmailService extends CommonService {

    public abstract void sendEmail(EmailRequest emailRequest) throws ApplicationException;
    
    @Value("${default_support_email}")
    protected String defaultSupportEmail;
    
    @Value("${default_support_phone}")
    protected String defaultSupportPhone;
    
    @Value("${login_url}")
    protected String loginUrl;
    
    @Async
    public void sendEmailToUsers(Integer companyId, String jobId) {
        List<User> userList = userRepository.findByAddedBy(jobId);

        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // Sendgrid has limit of maximum 1000 personalisation in a single mail request.
        // So here dividing the users in chunks of 1000
        List<User> tempUserList = new ArrayList<User>();
        int counter = 0;
        for (User user : userList) {
            tempUserList.add(user);
            if (tempUserList.size() % MAX_SENDGRID_PERSONALISATION == 0 || counter == userList.size() - 1) {
                createAndSendEmail(companyId, tempUserList);
                tempUserList = new ArrayList<User>();
            }
            counter++;
        }
    }

    /**
     * @param brokerId
     * @param users
     */
    public void createAndSendEmail(Integer brokerId, List<User> users) { 
        EmailRequest emailRequest = createEmailRequest(brokerId, users);
        sendEmail(emailRequest);
    }

    /**
     * 
     * @param brokerId
     * @param users
     * @return
     */
    public EmailRequest createEmailRequest(Integer brokerId, List<User> users) {
        
        if (users == null || brokerId == null) {
            return null;
        }

        EmailRequest emailRequest = new EmailRequest();
        
        Company broker = companyRepository.findOne(brokerId);

        EmailTemplate emailTemplate = getEmailTemplate(brokerId, ApplicationConstants.WELCOME_EMAIL_TYPE);

        if (emailTemplate == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.ENTITY_NOT_FOUND, "template",
                    String.valueOf(brokerId));
        }

        if (emailTemplate != null && emailTemplate.getEmailConfigurations() != null
                && !emailTemplate.getEmailConfigurations().isEmpty()) {
         
            for (EmailConfiguration emailConfiguration : emailTemplate.getEmailConfigurations()) {
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(EMAIL_BODY)) {
                    emailRequest.setBody(emailConfiguration.getValue());
                }
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(FROM_EMAIL)) {
                    emailRequest.setFromEmail(emailConfiguration.getValue());
                }
                if (emailConfiguration.getEmailField().getName().equalsIgnoreCase(EMAIL_SUBJECT)) {
                    emailRequest.setSubject(broker.getCompanyName() + ", " + emailConfiguration.getValue());
                }
            }
        }

        for (User user : users) {

            List<KeyValuePair> substitutions = getEmailSubstituions(broker, user);

            emailRequest.getRecipientToSubstitutionMap().put(user, substitutions);
        }

        return emailRequest;
    }

    /**
     * To get email template for given parameters
     * 
     * @param brokerId
     * @param type
     * @return
     */
    public EmailTemplate getEmailTemplate(Integer brokerId, String type) {
        EmailTemplate emailTemplate = emailRepository.findFirstByBrokerIdAndType(brokerId, type);

        if (emailTemplate == null) {
            return getDefaultEmailTemplate();
        }
        
        return emailTemplate;

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

    /**
     * Create KeyValue pair
     * 
     * @param key
     * @param value
     * @return
     */
    protected KeyValuePair createKeyValue(String key, String value) {
        return new KeyValuePair(key, value);
    }

    /**
     * @param broker
     * @param user
     * @return
     */
    protected List<KeyValuePair> getEmailSubstituions(Company broker, User user) {
        List<KeyValuePair> substitutions = new ArrayList<KeyValuePair>();
        
        //String sendgridTemplateId = emailTemplate.getSendgridTemplateId();
        String generatedHashedCode = RESET_PASSWORD_PREFIX + generateHashedValue(user.getUserId());
        String resetPasswordLink = prepareResetPasswordlink(loginUrl, generatedHashedCode);

        // Saving SetPasswordRequest record for reset password request.
        saveSetPasswordRequest(user.getUserId(), generatedHashedCode);

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
