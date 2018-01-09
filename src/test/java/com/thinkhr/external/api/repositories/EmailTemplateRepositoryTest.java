package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.getEmailTemplateList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.EmailTemplate;

/**
 * Junit to verify methods of EmailTemplateRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class EmailTemplateRepositoryTest {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    /**
     * Test to verify findFirstByBrokerIdAndType method.
     */
    @Test
    public void testFindFirstByBrokerIdAndType() {

        for (EmailTemplate emailTemplate : getEmailTemplateList()) {

            // Saving record into H2 DB one by one
            emailTemplateRepository.save(emailTemplate);
        }

        EmailTemplate foundEmailTemplate = emailTemplateRepository.findFirstByBrokerIdAndType(8148, "issue");

        assertNotNull(foundEmailTemplate.getId());
        assertEquals(8148, foundEmailTemplate.getBrokerId().intValue());
        assertEquals("issue", foundEmailTemplate.getType());
        assertEquals("52d96d96-a8bd-40d7-85c3-65d9272bdf8b", foundEmailTemplate.getSendgridTemplateId());
    }

}
