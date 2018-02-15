package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMPANY_TYPE_BROKER;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.db.entities.EmailTemplate;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.ConfigurationRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.repositories.EmailTemplateRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
import com.thinkhr.external.api.response.APIMessageUtil;
import com.thinkhr.external.api.services.utils.FileImportUtil;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to test all the methods of BrokerService.
 * 
 * @author Ajay Jain
 * @since 2018-01-23
 *
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { FileImportUtil.class, APIMessageUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.crypto.*" })
public class BrokerServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private LearnCompanyService learnCompanyService;

    @InjectMocks
    private BrokerService brokerService;

    @Mock
    private CustomFieldsRepository customFieldRepository;

    @Mock
    private ModelConvertor modelConvertor;

    @Mock
    private ConfigurationRepository configurationRepository;
    
    @Mock
    private EmailTemplateRepository emailTemplateRepository;

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private MessageResourceHandler resourceHandler;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * To verify getAllBroker method. 
     * 
     */
    @Test
    public void testGetAllBroker() {
        List<Company> companyList = new ArrayList<Company>();
        companyList.add(createCompany(1, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10"));
        companyList.add(createCompany(2, "ASI", "Software", "345345435", new Date(), "Special", "This is search help",
                "Other", "10"));
        companyList.add(createCompany(3, "ThinkHR", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10"));

        BrokerService brokerServiceSpy = spy(brokerService);
        doReturn(companyList).when(brokerServiceSpy).getAllCompany(any(), any(), any(), any(), any());

        try {
            List<Company> result = brokerServiceSpy.getAllBroker(null, null, null, null, null);
            assertEquals(3, result.size());
        } catch (ApplicationException ex) {
            fail("Not expected exception");
        }
    }

    /**
     * To verify getBroker when no broker company exists for given Id method
     * 
     */
    @Test(expected = com.thinkhr.external.api.exception.ApplicationException.class)
    public void testGetBrokerNotExists() {
        Integer companyId = 1;
        when(companyRepository.findOne(companyId))
                .thenReturn(null);
        Company result = brokerService.getBroker(companyId);
    }

    /**
     * To verify updateBroker method
     * @throws Exception 
     * 
     */
    @Test
    public void testUpdateBroker() throws Exception {
        Company company = createCompany(1, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        Company company2 = createCompany(1, "ThinkHr", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        when(companyRepository.findOne(company.getCompanyId()))
                        .thenReturn(company);

        BrokerService brokerServiceSpy = spy(brokerService);
        Company updatedCompany = null;
        try {
            String companyJson = ApiTestDataUtil.getJsonString(company);
            doReturn(company2).when(brokerServiceSpy).updateCompany(companyJson, company.getCompanyId(), company);

            updatedCompany = brokerServiceSpy.updateBroker(company.getCompanyId(), companyJson);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }

        assertEquals(company2.getCompanyName(), updatedCompany.getCompanyName());
    }

    /**
     * To verify addBroker method
     * 
     */
    @Test
    public void test_AddBroker() {
        //When all data is correct, it should assert true 
        Integer brokerId = 10;
        Company company = createCompany(1, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        BrokerService brokerServiceSpy = spy(brokerService);
        Configuration config = brokerService.createMasterConfiguration(null);
        
        EmailTemplate emailTemplate = new EmailTemplate();

        doReturn(company).when(brokerServiceSpy).addCompany(company, brokerId);
        when(configurationRepository.save(any(Configuration.class))).thenReturn(config);
        when(companyRepository.save(company)).thenReturn(company);
        when(emailTemplateRepository.save(emailTemplate)).thenReturn(null);

        Company result = brokerServiceSpy.addBroker(company,"subject","email");

        assertEquals(company.getCompanyId(), result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("345345435", result.getCompanyPhone());
    }

    /**
     * To verify updateBroker method when companyRepository doesn't find a match for given companyId.
     * @throws Exception 
     * 
     * 
     */
    @Test
    public void testUpdateBrokerForEntityNotFound() throws Exception {
        Company company = createCompany(1, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        when(companyRepository.findOne(company.getCompanyId()))
                        .thenReturn(null);
        try {
            String companyJson = ApiTestDataUtil.getJsonString(company);
            brokerService.updateBroker(company.getCompanyId(), companyJson);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }

    /**
     * To verify deleteBroker method
     * 
     */
    @Test
    public void testDeleteBroker() {
        Company  company = createCompany();
        when(companyRepository.findOne(company.getCompanyId()))
                        .thenReturn(company);

        BrokerService brokerServiceSpy =  spy(brokerService);
        doReturn(1).when(brokerServiceSpy).deleteCompany(company.getCompanyId(), company);
        
        try {
            brokerServiceSpy.deleteBroker(company.getCompanyId());
        } catch (ApplicationException e) {
            fail("Should be executed properly without any error");
        }
    }

    
    /**
     * To verify deleteCompany method throws ApplicationException when internally companyRepository.delete method throws exception.
     * 
     */
    @Test(expected = com.thinkhr.external.api.exception.ApplicationException.class)
    public void testDeleteCompanyForEntityNotFound() {
        int companyId = 1;
        when(companyRepository.findOne(companyId))
                .thenReturn(null);

        brokerService.deleteBroker(companyId);
    }

    /**
     * Test to verify when DB has no duplicate Broker Company.
     * 
     */
    @Test
    public void testCheckDuplicateForNoDuplicates() {
        String brokerName = "Pepcus";
        Company company = createCompany();
        when(companyRepository.findFirstByCompanyNameAndCompanyType(brokerName, COMPANY_TYPE_BROKER)).thenReturn(null);

        // when no duplicate record exists in DB.
        boolean isDuplicate = brokerService.isDuplicateCompany(company);
        assertTrue(!isDuplicate);
    }

    /**
     * Test to verify when DB has duplicate Broker Company.
     * 
     */
    @Test
    public void test_isDublicateCompany_True() {
        String brokerName = "Pepcus";
        Company company = createCompany();
        company.setCompanyType(COMPANY_TYPE_BROKER); 
        when(companyRepository.findFirstByCompanyNameAndCompanyType(brokerName, COMPANY_TYPE_BROKER))
                .thenReturn(company);

        boolean isDuplicate = brokerService.isDuplicateCompany(company);
        assertTrue(isDuplicate);
    }
}
