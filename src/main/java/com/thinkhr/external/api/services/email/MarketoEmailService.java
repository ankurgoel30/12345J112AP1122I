package com.thinkhr.external.api.services.email;

import java.util.List;

import com.thinkhr.external.api.model.EmailRequest;

import lombok.Data;

/**
 * EmailService class to work with email entities, repositories and Marketo APIs.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-15
 *
 */
@Data
public class MarketoEmailService implements EmailService {

    @Override
    public EmailRequest createEmailRequest(Integer brokerId, List<String> userNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendEmail(EmailRequest emailRequest) throws Exception {
        // TODO Auto-generated method stub

    }

}
