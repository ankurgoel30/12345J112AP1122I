package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createEmailRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.sendgrid.Mail;
import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.model.EmailRequest;

/**
 * Junits to test the methods of EmailUtil.
 * 
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class EmailUtilTest {

    /**
     * Test to verify EmailUtil.build method
     */
    @Test
    public void testBuild() {
        EmailRequest emailRequest = createEmailRequest();

        Mail mail = EmailUtil.build(emailRequest);

        assertFalse(mail.getPersonalization().isEmpty());
        assertFalse(mail.getContent().isEmpty());
        assertEquals(emailRequest.getFromEmail(), mail.getFrom().getEmail());
        assertEquals(emailRequest.getSubject(), mail.getSubject());
    }

}
