package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.services.utils.EmailUtil.prepareResetPasswordlink;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.KeyValuePair;
import com.thinkhr.external.api.repositories.EmailTemplateRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.services.utils.EmailUtil;

/**
 * Send email functionality
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Service
public class EmailService {
 
    @Autowired
    private EmailTemplateRepository emailRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    //TODO: Autowired sendGrid configurations
    
    
    /**
     * To get email template for given parameters
     * 
     * @param brokerId
     * @param type
     * @return
     */
    public String getEmailTemplate(Integer brokerId, String type) {
        //TODO: Add implementation
        return null;
    }
    
    /**
     * Fetch email template for default configurations
     * 
     * @return
     */
    public String getDefaultEmailTemplate() {
        return getEmailTemplate(Integer.parseInt(ApplicationConstants.DEFAULT_BROKER_ID),
                ApplicationConstants.WELCOME_EMAIL_TYPE);
    }
    
    public void sendEmail(Integer brokerId) {
        
    }
    
    
    /**
     * @param brokerId
     * @param username
     */
    public EmailRequest createEmailRequest(Integer brokerId, String username) {
        
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "username", username);
        }

        String sendgridTemplateId = getEmailTemplate(brokerId, ApplicationConstants.WELCOME_EMAIL_TYPE);
        
        String resetPasswordLink = prepareResetPasswordlink(user);
        
        //TODO: Add an entry into set_reset_password
        
        List<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(createKeyValue("SET_LOGIN_LINK", resetPasswordLink));
        parameters.add(createKeyValue("FIRSTNAME", user.getFirstName()));
        parameters.add(createKeyValue("BROKER_NAME", resetPasswordLink));
        parameters.add(createKeyValue("USERNAME", resetPasswordLink));
        parameters.add(createKeyValue("SUPPORT_PHONE", resetPasswordLink));
        parameters.add(createKeyValue("SUPPORT_EMAIL", resetPasswordLink));
        parameters.add(createKeyValue("SET_PW_LINK", resetPasswordLink));
        //TODO: 
        //createEmailRequest();
        
        return null;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    private KeyValuePair createKeyValue(String key,
            String value) {
        return new KeyValuePair(key, value);
    }

    
    public void sendEmail(EmailRequest request) {
        //TODO: implement
        
        
    }
}


