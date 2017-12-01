package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.services.upload.FileUploadEnum;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to test all the methods of CompanyService.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */

@RunWith(SpringRunner.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CustomFieldsRepository customFieldRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * To verify getAllCompany method. 
     * 
     */
    @Test
    public void testGetAllCompany(){
        List<Company> companyList = new ArrayList<Company>();
        companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
        companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
        companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));
        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        when(companyRepository.findAll(null, pageable)).thenReturn(new PageImpl<Company>(companyList, pageable, companyList.size()));

        try {
            List<Company> result =  companyService.getAllCompany(null, null, null, null, null);
            assertEquals(3, result.size());
        } catch (ApplicationException ex) {
            fail("Not expected exception");
        }

    }

    /**
     * To verify getAllCompany method specifically for pageable.
     * 
     */
    @Test
    public void testGetAllToVerifyPageable(){

        List<Company> companyList = new ArrayList<Company>();
        companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
        companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
        companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));

        companyService.getAllCompany(null, null, null, null, null);

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        //Verifying that internally pageable arguments is passed to companyRepository's findAll method
        verify(companyRepository, times(1)).findAll(null, pageable);
    }

    /**
     * To verify createCompany method
     * 
     */
    @Test
    public void testGetCompany() {
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(companyId)).thenReturn(company);
        Company result = companyService.getCompany(companyId);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("PEP", result.getDisplayName());
    }

    /**
     * To verify createCompany method
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testGetCompanyNotExists() {
        Integer companyId = 1;
        when(companyRepository.findOne(companyId)).thenReturn(null);
        Company result = companyService.getCompany(companyId);
    }

    /**
     * To verify addCompany method
     * 
     */
    @Test
    public void testAddCompany() {
        //When all data is correct, it should assert true 
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

        when(companyRepository.save(company)).thenReturn(company);

        Company result = companyService.addCompany(company);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("PEP", result.getDisplayName());
    }

    /**
     * To verify updateCompany method
     * 
     */

    @Test
    public void testUpdateCompany(){
        Integer companyId = 1;

        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

        when(companyRepository.save(company)).thenReturn(company);
        when(companyRepository.findOne(companyId)).thenReturn(company);

        // Updating company name 
        company.setCompanyName("Pepcus - Updated");

        Company companyUpdated = null;
        try {
            companyUpdated = companyService.updateCompany(company);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", companyUpdated.getCompanyName());
    }

    /**
     * To verify updateCompany method when companyRepository doesn't find a match for given companyId.
     * 
     * 
     */

    @Test
    public void testUpdateCompanyForEntityNotFound(){
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(companyId)).thenReturn(null);
        try {
            companyService.updateCompany(company);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }


    /**
     * To verify deleteCompany method
     * 
     */
    @Test
    public void testDeleteCompany() {
        Integer companyId = 1;

        when(companyRepository.findOne(companyId)).thenReturn(createCompany());
        try {
            companyService.deleteCompany(companyId);
        } catch (ApplicationException e) {
            fail("Should be executed properly without any error");
        }
        //Verifying that internally companyRepository's delete method executed
        verify(companyRepository, times(1)).softDelete(companyId);
    }

    /**
     * To verify deleteCompany method throws ApplicationException when internally companyRepository.delete method throws exception.
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testDeleteCompanyForEntityNotFound() {
        int companyId = 1 ;
        when(companyRepository.findOne(companyId)).thenReturn(null);
        companyService.deleteCompany(companyId);
    }


    /**
     * Test getCompanyColumnsHeaderMap getCustomFieldsMap(id) return map with
     * some entries
     */
    @Test
    public void testGetCompanyColumnsHeaderMap_TwoCustomFields() {
        int companyId = 15472;
        List<CustomFields> customFieldTestData = ApiTestDataUtil.createCustomFieldsList();
        Mockito.when(customFieldRepository.findByCompanyId(companyId)).thenReturn(customFieldTestData);

        Map<String, String> columnsToHeaderMap = companyService.getCompanyColumnHeaderMap(companyId);
        assertTrue(columnsToHeaderMap.containsKey("custom1"));
        assertTrue(columnsToHeaderMap.containsKey("custom2"));
        assertEquals("CORRELATION_ID", columnsToHeaderMap.get("custom1"));
        assertEquals("GROUP_ID", columnsToHeaderMap.get("custom2"));
    }

    /**
     * Test getCompanyColumnsHeaderMap getCustomFieldsMap(id) return null
     */
    @Test
    public void testGetCompanyColumnsHeaderMap_NoCustomFields() {
        int companyId = 12345;
        Mockito.when(customFieldRepository.findByCompanyId(companyId)).thenReturn(null);

        Map<String, String> columnsToHeaderMap = companyService.getCompanyColumnHeaderMap(companyId);
        assertFalse(columnsToHeaderMap.containsKey("custom1"));
        assertEquals(FileUploadEnum.COMPANY.prepareColumnHeaderMap().size(), columnsToHeaderMap.size());
    }

    /**
     * Test bulkUpload when validateAndGetBroker throws exception for invalid
     * broker id
     */
    @Test
    public void testBulkUpload_InvalidBrokerId() {
        int brokerId = 12345;
        ApplicationException appEx = ApplicationException.createFileImportError(APIErrorCodes.INVALID_BROKER_ID,
                String.valueOf(brokerId));

        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doThrow(appEx).when(companyServiceSpy).validateAndGetBroker(brokerId);

        try {
            MultipartFile fileToImport = null;

            companyServiceSpy.bulkUpload(fileToImport, brokerId);
            fail("Expecting validation exception for Invalid Broker Id");
        } catch (ApplicationException ex) {
            assertNotNull(ex);
            assertEquals(APIErrorCodes.INVALID_BROKER_ID, ex.getApiErrorCode());
        }
    }


    /**
     * Test bulkUpload when process records return fileImportResult with no
     * failed records
     */
    @Test
    public void testBulkUpload_NoFailedRecords() {
        int brokerId = 12345;
        Company testdataBroker = ApiTestDataUtil.createCompany();
        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateAndGetBroker(brokerId);

        FileImportResult fileImportResultTestData = ApiTestDataUtil.createFileImportResultWithNoFailedRecords();

        Mockito.doReturn(fileImportResultTestData).when(companyServiceSpy).processRecords(Matchers.any(),
                Matchers.any());

        MultipartFile fileToImport = null;
        try {
            File file = new File("src/test/resources/testdata/8_Example10Rec.csv");
            FileInputStream input = null;
            input = new FileInputStream(file);
            fileToImport = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e1) {
            fail("IOException is not expected");
        }

        FileImportResult fileImportResult = companyServiceSpy.bulkUpload(fileToImport, brokerId);

        assertNotNull(fileImportResult);
        assertEquals(10, fileImportResult.getTotalRecords());
        assertEquals(10, fileImportResult.getNumSuccessRecords());
        assertEquals(0, fileImportResult.getNumFailedRecords());
    }

    /**
     * Test bulkUpload when process records return fileImportResult with failed
     * records
     */
    @Test
    public void testBulkUpload_FailedRecords() {
        int brokerId = 12345;
        Company testdataBroker = ApiTestDataUtil.createCompany();
        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateAndGetBroker(brokerId);

        FileImportResult fileImportResultTestData = ApiTestDataUtil.createFileImportResultWithFailedRecords();

        Mockito.doReturn(fileImportResultTestData).when(companyServiceSpy).processRecords(Matchers.any(),
                Matchers.any());

        MultipartFile fileToImport = null;
        try {
            File file = new File("src/test/resources/testdata/8_Example10Rec.csv");
            FileInputStream input = null;
            input = new FileInputStream(file);
            fileToImport = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e1) {
            fail("IOException is not expected");
        }

        FileImportResult fileImportResult = companyServiceSpy.bulkUpload(fileToImport, brokerId);


        assertNotNull(fileImportResult);
        assertEquals(10, fileImportResult.getTotalRecords());
        assertEquals(7, fileImportResult.getNumSuccessRecords());
        assertEquals(3, fileImportResult.getNumFailedRecords());
        assertEquals(3, fileImportResult.getFailedRecords().size());
    }

    /**
     * Test bulkUpload when process records throws exception for unmapped custom
     * headers
     * 
     */
    @Test
    public void testBulkUpload_UnmappedCustomHeadersException() {
        int brokerId = 12345;
        Company testdataBroker = ApiTestDataUtil.createCompany();
        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateAndGetBroker(brokerId);

        ApplicationException appEx = ApplicationException.createFileImportError(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS,
                StringUtils.join(new String[] { "NAME", "AGE" }, COMMA_SEPARATOR));

        Mockito.doThrow(appEx).when(companyServiceSpy).processRecords(Matchers.any(),
                Matchers.any());

        MultipartFile fileToImport = null;
        try {
            File file = new File("src/test/resources/testdata/8_Example10Rec.csv");
            FileInputStream input = null;
            input = new FileInputStream(file);
            fileToImport = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e1) {
            fail("IOException is not expected");
        }

        try {
            FileImportResult fileImportResult = companyServiceSpy.bulkUpload(fileToImport, brokerId);
        } catch (ApplicationException ex) {
            assertNotNull(ex);
            assertEquals(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS, ex.getApiErrorCode());
        }
    }
}
