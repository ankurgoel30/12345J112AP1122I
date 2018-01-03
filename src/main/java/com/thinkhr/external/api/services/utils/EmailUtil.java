package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.services.utils.CommonUtil.getHashedValue;

import com.thinkhr.external.api.db.entities.User;
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
     * @return
     */
    public static String prepareResetPasswordlink(User user) {
        
        String appUrl = APIRequestHelper.getApplicationUrl();
        
        return new StringBuffer().append(appUrl)
                .append("/reset-password/").append("C").append(getHashedValue(user.getUserId())).toString();
        
    }
    
 /*   public static Mail build(EmailRequest emailRequest) {

        Email emailFrom = new Email(emailRequest.getFromEmail());

        //TODO: identify a way to send email to multiple recipients
        Email emailTo = new Email(emailRequest.getToEmail().get(0));

        Mail mail = new Mail();

        Personalization personalization = new Personalization();
        List<KeyValuePair> parameters = null;

        List<KeyValuePair>  parameters = emailRequest.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            KeyValuePair keyValuePair = parameters.get(i);
            personalization.addSubstitution(keyValuePair.getKey(), keyValuePair.getValue());
        }

        personalization.addTo(emailTo);
        mail.addPersonalization(personalization);

        Content content = new Content("text/html","<HTML> </HTML>");
        mail.addContent(content);
        mail.setFrom(emailFrom);



        return mail;
    }
    
    
    @Override
    public void sendEmail(String templateId,
            EmailRequest sendEmailRequest) throws Exception {
        
        Mail mail = EmailUtil.build(sendEmailRequest);
        
        sendEmail(templateId, mail);
        
    }

    @Override
    public void sendEmail(String templateID, Mail mail) throws Exception {
        SendGrid sg = new SendGrid(this.sendGridApiKey);
        Request request = new Request();
        try {
            mail.templateId=templateID;
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            logger.info("response from senndgrid mail: " + response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw ex;
        }
    } */

}
