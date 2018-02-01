package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.ConfigurationRepository;
import com.thinkhr.external.api.response.APIMessageUtil;

/**
 * Junit to test all the methods of CompanyService.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-25
 *
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { APIMessageUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.crypto.*" })
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class ConfigurationServiceTest {


    @Mock
    private MessageResourceHandler resourceHandler;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ConfigurationService configurationService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * To verify deleteConfiguration method. 
     * 
     */
    @Test
    public void test_GetConfiguration() {
        //TestData
        Integer configId = 1;
        Integer brokerId = 12345;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");

        //Stub Functions for this test
        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configId, brokerId)).thenReturn(configuration);

        //Method Call
        Configuration configurationInDb = configurationService.getConfiguration(configId, brokerId);

        //Assertions
        assertEquals(configuration.getCompanyId(), configurationInDb.getCompanyId());
        assertEquals(configuration.getConfigurationKey(), configurationInDb.getConfigurationKey());
        assertEquals(configuration.getDescription(), configurationInDb.getDescription());
    }

    /**
     * To verify deleteConfiguration method. 
     */
    @Test
    public void test_DeleteConfiguration() {
        //TestData
        Integer brokerId = 12345;
        Integer configId = 1;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0);

        //Stub Functions for this test
        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configId, brokerId)).thenReturn(configuration);

        //Method Call
        Integer config = configurationService.deleteConfiguration(configId, brokerId);

        //Assertions
        assertEquals(configId, config);
    }

    /**
     * To verify deleteConfiguration method when no configuration found for given configurationId. . 
     */
    @Test(expected = ApplicationException.class)
    public void test_DeleteConfiguration_NoRecord() {
        //TestData
        Integer brokerId = 12345;
        Integer configId = 1;

        //Stub Functions for this test
        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configId, brokerId)).thenReturn(null);

        try {
            //Method Call
            Integer config = configurationService.deleteConfiguration(configId, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.INVALID_CONFIGURATION_ID);
            throw ex;
        }
    }


    /**
     * To verify updateConfiguration method. 
     * @throws Exception 
     */
    @Test
    public void test_UpdateConfiguration() throws Exception {
        //TestData
        Integer brokerId = 12345;
        Integer configId = 1;
        Configuration configurationInDb = createConfiguration(1, 2, "ABC", "test config", 0);
        Configuration updatedConfiguration = createConfiguration(1, 2, "ABC", "Updatedname", 0);
        String configJson = "{\"name\": \"Updatename\"}";

        //Stub Functions for this test
        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configId, brokerId)).thenReturn(configurationInDb);
        when(configurationRepository.save(any(Configuration.class))).thenReturn(updatedConfiguration);

        Configuration configuration = null;
        try {
            //Method Call
            configuration = configurationService.updateConfiguration(configId, configJson, brokerId);
        } catch (Exception ex) {
            fail("Exception not expected");
        }

        //Assertions
        assertEquals(updatedConfiguration.getName(), configuration.getName());
    }

    /**
     * To verify updateConfiguration method when no configuration found for given configurationId. 
     */
    @Test(expected = ApplicationException.class)
    public void test_UpdateConfiguration_NoRecord() {
        //TestData
        Integer brokerId = 12345;
        Integer configId = 1;
        String configJson = null;

        //Stub Functions for this test
        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configId, brokerId)).thenReturn(null);

        try {
            //Method Call
            Configuration config = configurationService.updateConfiguration(configId, configJson, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.INVALID_CONFIGURATION_ID);
            throw ex;
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    /**
     * To verify addConfiguration method. 
     */
    @Test
    public void test_AddConfiguration() {
        //TestData
        Integer brokerId = 12345;
        Company broker = createCompany();
        broker.setConfigurationId(123);
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");

        //Spy
        ConfigurationService configServiceSpy = spy(configurationService);

        //Stub Functions for this test
        when(companyRepository.findOne(configuration.getCompanyId())).thenReturn(broker);
        when(configurationRepository.save(any(Configuration.class))).thenReturn(configuration);

        //Method Call
        Configuration configurationSaved = configServiceSpy.addConfiguration(configuration, brokerId);
        
        //Assertions
        assertEquals(configuration.getCompanyId(), configurationSaved.getCompanyId());
        assertEquals(configuration.getConfigurationKey(), configurationSaved.getConfigurationKey());
        assertTrue(configurationSaved.getMasterConfiguration() == 0);
        assertEquals(configuration.getDescription(), configurationSaved.getDescription());
    }

    /**
     * To verify addConfiguration method when brokerId is invalid. 
     */
    @Test
    public void test_AddConfiguration_InvalidBroker() {
        //TestData
        Integer brokerId = 12345;
        Company broker = createCompany();
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");

        //Stub Functions for this test
        when(companyRepository.findOne(configuration.getCompanyId())).thenReturn(null);

        try {
            //Method Call
            Configuration configurationSaved = configurationService.addConfiguration(configuration, brokerId);
        } catch (ApplicationException ae) {
            //Assertions
            assertNotNull(ae);
            assertEquals(APIErrorCodes.INVALID_BROKER_ID, ae.getApiErrorCode());
        }
    }

    /**
     * To verify addConfiguration method when no master configuration exists for broker
     */
    @Test
    public void test_AddConfiguration_NoMasterConfigExists() {
        //TestData
        Integer brokerId = 12345;
        Company broker = createCompany();
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");
        broker.setConfigurationId(null); // Broker does not have any Configuration Set

        //Stub Functions for this test
        when(companyRepository.findOne(configuration.getCompanyId())).thenReturn(broker);

        try {
            //Method Call
            Configuration configurationSaved = configurationService.addConfiguration(configuration, brokerId);
        } catch (ApplicationException ae) {
            //Assertions
            assertNotNull(ae);
            assertEquals(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS, ae.getApiErrorCode());
        }
    }

    /**
     * To verify addConfiguration method when configurationId for broker is not a master configuration
     */
    @Test
    public void test_AddConfiguration_InvalidMasterConfig() {
        //TestData
        Integer brokerId = 12345;
        Company broker = createCompany();
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");
        broker.setConfigurationId(123);// Broker has configuration set
        
        //Spy
        ConfigurationService configServiceSpy = spy(configurationService);

        //Stub Functions for this test
        when(companyRepository.findOne(configuration.getCompanyId())).thenReturn(broker);

        try {
            //Method Call
            Configuration configurationSaved = configServiceSpy.addConfiguration(configuration, brokerId);
        } catch (ApplicationException ae) {
            //Assertions
            assertNotNull(ae);
            assertEquals(APIErrorCodes.INVALID_MASTER_CONFIGURATION_ID, ae.getApiErrorCode());
        }
    }

}
