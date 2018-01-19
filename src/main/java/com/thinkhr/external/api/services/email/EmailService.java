package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.ApplicationConstants.EMAIL_BODY;
import static com.thinkhr.external.api.ApplicationConstants.EMAIL_SUBJECT;
import static com.thinkhr.external.api.ApplicationConstants.FROM_EMAIL;
import static com.thinkhr.external.api.services.utils.EmailUtil.DEFAULT_EMAIL_TEMPLATE_BROKERID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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

/**
 * Generic Interface for sending welcome email.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-15
 *
 */
public abstract class EmailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmailTemplateRepository emailRepository;

    @Autowired
    private SetPasswordRequestRepository setPasswordRepository;

    public abstract void sendEmail(EmailRequest emailRequest) throws Exception;

    public EmailRequest createEmailRequest(Integer brokerId, List<User> users) {
        if (users == null) {
            return null;
        }

        EmailRequest emailRequest = new EmailRequest();
        Company broker = null;
        if (brokerId != null) {
            broker = companyRepository.findOne(brokerId);
        }

        EmailTemplate emailTemplate = getEmailTemplate(brokerId, ApplicationConstants.WELCOME_EMAIL_TYPE);
        if (emailTemplate == null) {
            emailTemplate = getDefaultEmailTemplate();
        }

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
            if (user == null) {
                continue;
            }

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

    protected abstract List<KeyValuePair> getEmailSubstituions(Company broker, User user);

}
