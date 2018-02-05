package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_CONFIGURATION_ID;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createConfiguration;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createSku;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.db.entities.Sku;
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
     * To verify getConfiguration method. 
     * @throws Exception 
     * 
     */
    @Test
    public void test_GetConfiguration() throws Exception {
        //TestData
        Integer configId = 1;
        Integer brokerId = 2;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configuration);
        
        //Method Call
        Configuration configurationInDb = configurationService.getConfiguration(configId, brokerId);
        
        //Assertions
        assertEquals(configuration.getCompanyId(), configurationInDb.getCompanyId());
        assertEquals(configuration.getConfigurationKey(), configurationInDb.getConfigurationKey());
        assertEquals(configuration.getDescription(), configurationInDb.getDescription());
    }
    
    /**
     * To verify getConfiguration method for invalid configuration id. 
     * @throws Exception 
     * 
     */
    @Test(expected = ApplicationException.class)
    public void test_GetConfigurationInvalidConfigurationId() throws Exception {
        //TestData
        Integer configId = 1;
        Integer brokerId = 12345;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config");

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configuration);
        
        //Method Call
        try{
            Configuration configurationInDb = configurationService.getConfiguration(configId, brokerId);
        }catch(ApplicationException ex) {
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.UNAUTHORIZED_CONFIGURATION_ACCESS);
            throw ex;
        }
    }

    /**
     * To verify deleteConfiguration method. 
     */
    @Test
    public void test_DeleteConfiguration() {
        //TestData
        Integer brokerId = 2;
        Integer configId = 1;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0);

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configuration);

        //Method Call
        Integer config = configurationService.deleteConfiguration(configId, brokerId);

        //Assertions
        assertEquals(configId, config);
    }

    /**
     * To verify deleteConfiguration method when master configuration provided. 
     */
    @Test(expected = ApplicationException.class)
    public void test_DeleteConfiguration_MasterConfiguration() {
        //TestData
        Integer brokerId = 2;
        Integer configId = 1;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0);
        configuration.setMasterConfiguration(1);

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configuration);

        try {
            //Method Call
            Integer config = configurationService.deleteConfiguration(configId, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.MASTER_CONFIGURATION_NOT_CHANGEABLE);
            throw ex;
        }
    }
    
    /**
     * To verify deleteConfiguration method when configuration provided is already linked to a company. 
     */
    @Test(expected = ApplicationException.class)
    public void test_DeleteConfiguration_LinkedConfiguration() {
        //TestData
        Integer brokerId = 2;
        Integer configId = 1;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0);
        Company company = createCompany();

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configuration);
        ApplicationException mockExp = ApplicationException.createBadRequest(APIErrorCodes.UNREMOVABLE_LINKED_CONFIGURATION, 
                String.valueOf(configId),String.valueOf(company.getCompanyId()));
        when(companyRepository.findFirstByConfigurationId(configId)).thenThrow(mockExp);

        try {
            //Method Call
            Integer config = configurationService.deleteConfiguration(configId, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.UNREMOVABLE_LINKED_CONFIGURATION);
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
        Integer brokerId = 2;
        Integer configId = 1;
        Configuration configurationInDb = createConfiguration(1, 2, "ABC", "test config", 0);
        Configuration updatedConfiguration = createConfiguration(1, 2, "ABC", "Updatedname", 0);
        String configJson = "{\"name\": \"Updatename\"}";
        Company company = createCompany();

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configurationInDb);
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
        when(configurationRepository.findOne(configId)).thenReturn(null);

        try {
            //Method Call
            Configuration config = configurationService.updateConfiguration(configId, configJson, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.ENTITY_NOT_FOUND);
            throw ex;
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    /**
     * To verify updateConfiguration method while configuring Skus not in Master Sku List. 
     */
    @Test(expected = ApplicationException.class)
    public void test_UpdateConfiguration_InvalidSkus() {
      //TestData
        Integer brokerId = 2;
        Integer configId = 1;
        Configuration configurationInDb = createConfiguration(1, 2, "ABC", "test config", 0);
        Configuration updatedConfiguration = createConfiguration(1, 2, "ABC", "Updatedname", 0);
        Configuration masterConfiguration = createConfiguration(1, 2, "ABC2", "masterConfig", 1);
        String configJson = "{\"name\": \"Updatename\",\"skus\": [{\"skuId\": 1}]}";
        
        List<Sku> masterSkus = new ArrayList<Sku>();
        masterSkus.add(createSku(2));
        masterConfiguration.setSkus(masterSkus);

        //Stub Functions for this test
        when(configurationRepository.findOne(configId)).thenReturn(configurationInDb);
        when(configurationRepository.findFirstByCompanyIdAndMasterConfiguration(brokerId,1)).thenReturn(masterConfiguration);
        when(configurationRepository.save(any(Configuration.class))).thenReturn(updatedConfiguration);

        try {
            //Method Call
            Configuration config = configurationService.updateConfiguration(configId, configJson, brokerId);
        } catch (ApplicationException ex) {
            //Assertions
            assertEquals(ex.getApiErrorCode(), APIErrorCodes.INVALID_SKU_IDS);
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
        Configuration masterConfiguration = createConfiguration(1, 2, "ABC2", "masterConfig", 1);

        //Spy
        ConfigurationService configServiceSpy = spy(configurationService);

        //Stub Functions for this test
        when(configurationRepository.findFirstByCompanyIdAndMasterConfiguration(brokerId,1)).thenReturn(masterConfiguration);
        when(configurationRepository.save(any(Configuration.class))).thenReturn(configuration);

        //Method Call
        Configuration configurationSaved = configServiceSpy.addConfiguration(configuration, brokerId);
        
        //Assertions
        assertEquals(configuration.getCompanyId(), configurationSaved.getCompanyId());
        assertEquals(configuration.getConfigurationKey(), configurationSaved.getConfigurationKey());
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
            assertEquals(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS, ae.getApiErrorCode());
        }
    }

    /**
     * To verify getConfigurations method. 
     * 
     */
    @Test
    public void test_GetConfigurations(){
        Integer brokerId = 2;
        List<Configuration> configList = new ArrayList<Configuration>();
        configList.add(createConfiguration(1, 2, "ABC", "test config"));
        configList.add(createConfiguration(1, 2, "AB1C", "test1 config"));
        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_CONFIGURATION_ID);

        when(configurationRepository.findAll(Matchers.any(Specification.class), Matchers.any(Pageable.class))).
            thenReturn(new PageImpl<Configuration>(configList, pageable, configList.size())); 

        try {
            List<Configuration> result =  configurationService.getConfigurations(brokerId, null, null, null, null, null);
            assertEquals(2, result.size());
        } catch (ApplicationException ex) {
            fail("Not expected exception");
        }

    }
}
