package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to verify methods of ConfigurationRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class ConfigurationRepositoryTest {

    @Autowired
    private ConfigurationRepository configurationRepository;

    /**
     * Test to verify repository.save method
     * 
     */
    @Test
    public void testSaveConfiguration() {

        Configuration configuration = ApiTestDataUtil.createConfiguration(null, 2, "ABC", "test config");

        // Saving record into H2 DB
        Configuration savedConfiguration = configurationRepository.save(configuration);

        assertNotNull(savedConfiguration.getConfigurationId());
        assertEquals(configuration.getCompanyId(), savedConfiguration.getCompanyId());
        assertEquals(configuration.getConfigurationKey(), savedConfiguration.getConfigurationKey());
        assertEquals(configuration.getName(), savedConfiguration.getName());
    }

    /**
     * Test to verify findFirstByConfigurationIdAndCompanyId method.
     * 
     */
    @Test
    public void testFindFirstByConfigurationIdAndCompanyId() {

        Configuration configuration1 = ApiTestDataUtil.createConfiguration(null, 1, "ABC", "test config1");

        // Saving record into H2 DB
        configurationRepository.save(configuration1);

        Configuration configuration2 = ApiTestDataUtil.createConfiguration(null, 3, "POR", "test config2");

        // Saving record into H2 DB
        configurationRepository.save(configuration2);

        Configuration configuration3 = ApiTestDataUtil.createConfiguration(null, 2, "XYZ", "test config3");

        // Saving record into H2 DB
        configurationRepository.save(configuration3);

        Configuration foundConfiguration = configurationRepository
                .findFirstByConfigurationIdAndCompanyId(configuration2.getConfigurationId(), 3);

        assertNotNull(foundConfiguration.getConfigurationId());
        assertEquals(configuration2.getCompanyId(), foundConfiguration.getCompanyId());
        assertEquals(configuration2.getConfigurationKey(), foundConfiguration.getConfigurationKey());
        assertEquals(configuration2.getName(), foundConfiguration.getName());
    }
}
