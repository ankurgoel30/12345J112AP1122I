package com.thinkhr.external.api.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
public class CommonServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CustomFieldsRepository customFieldRepository;

    @InjectMocks
    private CommonService commonService;

    /**
     * Test getCustomFieldMap when customFieldRepository.findByCompanyId(id)
     * return empty list of CustomFields
     */
    @Test
    public void testGetCustomFieldsMap_EmptyCustomFields() {
        int companyId = 187624;
        String customFieldType = "COMPANY";

        Mockito.when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, customFieldType))
                .thenReturn(new ArrayList<CustomFields>());
        
        Map<String, String> customFieldMap = commonService.getCustomFieldsMap(companyId, customFieldType);
        assertNull(customFieldMap);
    }

    /**
     * Test getCustomFieldMap when customFieldRepository.findByCompanyId(id)
     * return list of CustomField
     */
    @Test
    public void testGetCustomFieldsMap_TwoCustomFields() {
        List<CustomFields> customFieldTestData = ApiTestDataUtil.createCustomFieldsList();
        int companyId = 15472;
        String customFieldType = "COMPANY";
        Mockito.when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, customFieldType)).thenReturn(customFieldTestData);
        
        Map<String, String> customFieldMap = commonService.getCustomFieldsMap(companyId, customFieldType);
        assertEquals(2, customFieldMap.size());
        assertTrue(customFieldMap.containsKey("custom1"));
        assertTrue(customFieldMap.containsKey("custom2"));
        assertEquals("CORRELATION_ID", customFieldMap.get("custom1"));
        assertEquals("GROUP_ID", customFieldMap.get("custom2"));
    }

    /**
     * Test validateAndGetBroker when brokerId is valid
     */
    @Test
    public void testValidateAndGetBroker_ValidBrokerId() {
        Company brokerCompanyTestData = ApiTestDataUtil.createCompany();
        Mockito.when(companyRepository.findOne(15472)).thenReturn(brokerCompanyTestData);

        Company brokerCompany = commonService.validateAndGetBroker(15472);

        assertEquals(brokerCompanyTestData.getCompanyName(), brokerCompany.getCompanyName());
    }

    /**
     * Test validateAndGetBroker when brokerId is invalid
     */
    @Test
    public void testValidateAndGetBroker_InvalidBrokerId() {
        Mockito.when(companyRepository.findOne(12345)).thenReturn(null);

        try {
            Company brokerCompany = commonService.validateAndGetBroker(12345);
            fail("Expecting validation exception for invalid extension of invalid broker id");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.INVALID_BROKER_ID, appEx.getApiErrorCode());
        }

    }

}
