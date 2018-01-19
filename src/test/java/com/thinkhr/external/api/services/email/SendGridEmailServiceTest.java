package com.thinkhr.external.api.services.email;

import static com.thinkhr.external.api.ApplicationConstants.EMAIL_BODY;
import static com.thinkhr.external.api.ApplicationConstants.EMAIL_SUBJECT;
import static com.thinkhr.external.api.ApplicationConstants.WELCOME_EMAIL_TYPE;
import static com.thinkhr.external.api.services.utils.CommonUtil.generateHashedValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.EmailConfiguration;
import com.thinkhr.external.api.db.entities.EmailTemplate;
import com.thinkhr.external.api.db.entities.SetPasswordRequest;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.EmailTemplateRepository;
import com.thinkhr.external.api.repositories.SetPasswordRequestRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junits to test the methods of EmailService.
 * 
 * 
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class SendGridEmailServiceTest {

    @Mock
    private EmailTemplateRepository emailRepository;

    @Mock
    private SetPasswordRequestRepository setPasswordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private SendGridEmailService emailService;

    @Value("${sendgrid_api_key}")
    private String apiKey;

    @Value("${sendgrid_auth_template_id}")
    private String authTemplateId;

    @Value("${default_support_email}")
    private String defaultSupportEmail;

    @Value("${default_support_phone}")
    private String defaultSupportPhone;

    @Value("${login_url}")
    private String loginUrl;

    /**
     * Test to verify saveSetPasswordRequest method
     * 
     */
    @Test
    public void testSaveSetPasswordRequest() {
        Integer userId = 1;
        String generatedHashValue = generateHashedValue(userId);

        SetPasswordRequest passwordRequest = new SetPasswordRequest();
        passwordRequest.setContactId(userId);
        passwordRequest.setId(generatedHashValue);

        // Mock setPasswordRepository
        Mockito.when(setPasswordRepository.save(Mockito.any(SetPasswordRequest.class))).thenReturn(passwordRequest);

        SetPasswordRequest foundsetPasswordRequest = emailService.saveSetPasswordRequest(userId, generatedHashValue);

        assertNotNull(foundsetPasswordRequest.getId());
        assertEquals(passwordRequest.getContactId(), foundsetPasswordRequest.getContactId());
        assertEquals(passwordRequest.getId(), foundsetPasswordRequest.getId());
    }

    /**
     * 
     * Test to verify createEmailRequest for brokerId and userName
     * 
     */
    @Test
    public void testCreateEmailRequest() {
        Integer brokerId = 8148;
        User user = ApiTestDataUtil.createUser();
        List<User> users = new ArrayList<User>(Arrays.asList(user));

        Company broker = ApiTestDataUtil.createCompany();
        EmailTemplate emailTemplate = ApiTestDataUtil.createEmailTemplate(1, brokerId,
                WELCOME_EMAIL_TYPE);

        List<EmailConfiguration> configurations = new ArrayList<EmailConfiguration>();

        EmailConfiguration emailConfiguration1 = ApiTestDataUtil.createEmailConfiguration(
                EMAIL_BODY,
                "Dear Ajay, Congratulations");
        configurations.add(emailConfiguration1);

        EmailConfiguration emailConfiguration2 = ApiTestDataUtil.createEmailConfiguration(
                EMAIL_SUBJECT,
                "welcomes you to ThinkHR!");
        configurations.add(emailConfiguration2);

        EmailConfiguration emailConfiguration3 = ApiTestDataUtil.createEmailConfiguration(
                ApplicationConstants.FROM_EMAIL,
                "welcome@myhrworkplace.com");
        configurations.add(emailConfiguration3);

        emailTemplate.setEmailConfigurations(configurations);

        // Mocking repository methods
        Mockito.when(userRepository.findByUserName(user.getUserName())).thenReturn(user);
        Mockito.when(companyRepository.findOne(brokerId)).thenReturn(broker);
        Mockito.when(emailRepository.findFirstByBrokerIdAndType(brokerId, WELCOME_EMAIL_TYPE))
                .thenReturn(emailTemplate);
        Mockito.when(setPasswordRepository.save(Matchers.any(SetPasswordRequest.class)))
                .thenReturn(new SetPasswordRequest());

        emailService.setLoginUrl(loginUrl);
        emailService.setDefaultSupportEmail(defaultSupportEmail);
        emailService.setDefaultSupportPhone(defaultSupportPhone);

        EmailRequest emailRequest = emailService.createEmailRequest(brokerId, users);

        assertFalse(emailRequest.getRecipientToSubstitutionMap().isEmpty());
        assertEquals("welcome@myhrworkplace.com", emailRequest.getFromEmail());
        assertEquals(broker.getCompanyName() + ", welcomes you to ThinkHR!", emailRequest.getSubject());
        assertEquals("Dear Ajay, Congratulations", emailRequest.getBody());
        for (User userToSendEmail : emailRequest.getRecipientToSubstitutionMap().keySet()) {
            assertEquals(user.getUserName(), userToSendEmail.getUserName());
            assertFalse(emailRequest.getRecipientToSubstitutionMap().get(userToSendEmail).isEmpty());
        }

    }

}
