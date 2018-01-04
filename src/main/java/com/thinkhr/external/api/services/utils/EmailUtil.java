package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.services.utils.CommonUtil.getHashedValue;

import java.util.List;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Personalization;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.KeyValuePair;
import com.thinkhr.external.api.request.APIRequestHelper;

/**
 * Email utility
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public class EmailUtil {

    /**
     * Set reset password link for user
     * 
     * @param user
     * @return
     */
    public static String prepareResetPasswordlink(User user) {
        
        String appUrl = APIRequestHelper.getApplicationUrl();
        
        return new StringBuffer().append(appUrl)
                .append("/reset-password/").append("C").append(getHashedValue(user.getUserId())).toString();
        
    }
    
    /**
     * Building Email from emailRequest.
     * 
     * @param emailRequest
     * @return
     */
    public static Mail build(EmailRequest emailRequest) {

        Email emailFrom = new Email(emailRequest.getFromEmail());

        //TODO: identify a way to send email to multiple recipients
        Email emailTo = new Email(emailRequest.getToEmail().get(0));

        Mail mail = new Mail();

        Personalization personalization = new Personalization();

        List<KeyValuePair> parameters = emailRequest.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            KeyValuePair keyValuePair = parameters.get(i);
            personalization.addSubstitution(keyValuePair.getKey(), keyValuePair.getValue());
        }

        personalization.addTo(emailTo);
        mail.addPersonalization(personalization);

        Content content = new Content("text/html", "<HTML>" + emailRequest.getBody() + "</HTML>");
        mail.addContent(content);
        mail.setFrom(emailFrom);
        mail.setSubject(emailRequest.getSubject());


        return mail;
    }
    
}
