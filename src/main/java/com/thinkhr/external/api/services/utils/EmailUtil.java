package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.ApplicationConstants.RESET_PASSWORD_LINK;
import static com.thinkhr.external.api.services.utils.CommonUtil.generateHashedValue;

import java.util.List;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Personalization;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.KeyValuePair;

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
    public static String prepareResetPasswordlink(User user, String appUrl) {
        return new StringBuffer().append(appUrl).append(RESET_PASSWORD_LINK).append("C")
                .append(generateHashedValue(user.getUserId())).toString();
    }
    
    /**
     * Building Email from emailRequest.
     * 
     * @param emailRequest
     * @return
     */
    public static Mail build(EmailRequest emailRequest) {

        Email emailFrom = new Email(emailRequest.getFromEmail());

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
