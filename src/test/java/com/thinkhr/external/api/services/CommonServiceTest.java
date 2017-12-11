package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.CONTACT;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.RESOURCE_USER;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCustomFieldsForUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createStandardFieldsForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

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
import com.thinkhr.external.api.db.entities.StandardFields;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.repositories.StandardFieldsRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
public class CommonServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private StandardFieldsRepository standardFieldsRepository;

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
    
    /**
     * Test to verify if customFields map returns some entries.
     * 
     */
    @Test
    public void testAppendRequiredAndCustomHeaderMapWithCustomFields() {
        Integer companyId = 2;
        List<CustomFields> customFields = createCustomFieldsForUser();
        
        when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, RESOURCE_USER)).thenReturn(customFields);
        
        Map<String, String> reqAndCusHeaderMap = commonService.appendRequiredAndCustomHeaderMap(companyId, RESOURCE_USER);
        
        // checking some entries from custom fields.
        assertEquals("GROUP", reqAndCusHeaderMap.get("t1_customfield1"));
        assertEquals("CORRELATION_ID", reqAndCusHeaderMap.get("t1_customfield2"));
        assertEquals("BRANCH_ID", reqAndCusHeaderMap.get("t1_customfield3"));
        
    }
    
    /**
     * Test to verify if customFields map returns no entries.
     * 
     */
    @Test
    public void testAppendRequiredAndCustomHeaderMapWithNoCustomFields() {
        Integer companyId = 2;
        List<CustomFields> customFields = createCustomFieldsForUser();
        
        when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, RESOURCE_USER)).thenReturn(null);
        
        Map<String, String> reqAndCusHeaderMap = commonService.appendRequiredAndCustomHeaderMap(companyId, RESOURCE_USER);
        
        // no entries exists in custom fields from this map.
        assertEquals(null, reqAndCusHeaderMap.get("t1_customfield1"));
        assertEquals(null, reqAndCusHeaderMap.get("t1_customfield2"));
        assertEquals(null, reqAndCusHeaderMap.get("t1_customfield3"));
        
    }

    /**
     * Test to verify if type is null.
     * 
     */
    @Test
    public void testGetRequiredHeadersFromStdFieldsForTypeNull() {
        String type = null;
        
        List<String> stdFields = commonService.getRequiredHeadersFromStdFields(type);
        assertNull(stdFields);
    }
    
    /**
     * Test to verify if no records found in DB for given type.
     * 
     */
    @Test
    public void testGetRequiredHeadersFromStdFieldsForInvalidType() {
        String type = "ABC";

        when(standardFieldsRepository.findByType(type)).thenReturn(null);

        List<String> stdFields = commonService.getRequiredHeadersFromStdFields(type);
        assertNull(stdFields);
    }
    
    /**
     * Test to verify if records found in DB for given type contact.
     * 
     */
    @Test
    public void testGetRequiredHeadersFromStdFieldsForValidType() {
        String type = CONTACT; 
        List<StandardFields> list = createStandardFieldsForUser();
        
        when(standardFieldsRepository.findByType(type)).thenReturn(list);

        List<String> stdFields = commonService.getRequiredHeadersFromStdFields(type);

        assertNotNull(stdFields);
        assertFalse(stdFields.isEmpty());
        assertEquals("FIRST_NAME", stdFields.get(0));
        assertEquals("LAST_NAME", stdFields.get(1));
        assertEquals("CLIENT_NAME", stdFields.get(2));
    }

}
