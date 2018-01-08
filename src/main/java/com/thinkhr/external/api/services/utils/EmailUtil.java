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
 * Class contains utility methods used for Email feature
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public class EmailUtil {

    public static final Integer DEFAULT_EMAIL_TEMPLATE_BROKERID = 8148;

    // Adding constants for setting key value pairs in personalization
    public static final String SET_LOGIN_LINK = "%SET_LOGIN_LINK%";
    public static final String FIRST_NAME = "%FIRSTNAME%";
    public static final String BROKER_NAME = "%BROKER_NAME%";
    public static final String USER_NAME = "%USERNAME%";
    public static final String SUPPORT_PHONE = "%SUPPORT_PHONE%";
    public static final String SUPPORT_EMAIL = "%SUPPORT_EMAIL%";
    public static final String SET_PASSWORD_LINK = "%SET_PW_LINK%";

    public static final String SENDGRID_END_POINT = "mail/send";

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
        parameters.stream().forEach(keyValuePair -> {
            personalization.addSubstitution(keyValuePair.getKey(), keyValuePair.getValue());
        });

        personalization.addTo(emailTo);
        mail.addPersonalization(personalization);

        Content content = new Content("text/html", "<HTML>" + emailRequest.getBody() + "</HTML>");
        mail.addContent(content);
        mail.setFrom(emailFrom);
        mail.setSubject(emailRequest.getSubject());


        return mail;
    }
    
}
