package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCustomFieldsList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.CustomFields;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class CustomFieldsRepositoryTest {

    @Autowired
    private CustomFieldsRepository customFieldsRepository;

    /**
     * Test to verify findByCompanyId method.
     */
    @Test
    public void testFindByCompanyId() {
        Integer companyId = 1;
        for (CustomFields customFields : getCustomFieldsList()) {
            customFieldsRepository.save(customFields);
        }
        
        List<CustomFields> foundCustomFields = customFieldsRepository.findByCompanyId(companyId);
        
        assertNotNull(foundCustomFields);
        assertEquals(3, foundCustomFields.size());
    }

    /**
     * Test to verify findByCompanyIdAndCustomFieldType method.
     */
    @Test
    public void testFindByCompanyIdAndCustomFieldType() {
        Integer companyId = 1;
        String customFieldType = "COMPANY";
        for (CustomFields customFields : getCustomFieldsList()) {
            customFieldsRepository.save(customFields);
        }

        List<CustomFields> foundCustomFields = customFieldsRepository.findByCompanyIdAndCustomFieldType(companyId,
                customFieldType);

        assertNotNull(foundCustomFields);
        assertEquals(2, foundCustomFields.size());
    }

}
