package com.thinkhr.external.api.services.email;

import java.util.List;

import com.thinkhr.external.api.model.EmailRequest;

/**
 * Generic Interface for sending welcome email.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-15
 *
 */
public interface EmailService {

    public EmailRequest createEmailRequest(Integer brokerId, List<String> userName);

    public void sendEmail(EmailRequest emailRequest) throws Exception;

}
