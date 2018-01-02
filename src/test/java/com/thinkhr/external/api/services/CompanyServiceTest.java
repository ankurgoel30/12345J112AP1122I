package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCustomFieldsList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getFileRecordForCompanyWithCustom1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.ConfigurationRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
import com.thinkhr.external.api.response.APIMessageUtil;
import com.thinkhr.external.api.services.upload.FileUploadEnum;
import com.thinkhr.external.api.services.utils.FileImportUtil;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to test all the methods of CompanyService.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { FileImportUtil.class, APIMessageUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.crypto.*" })
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private LearnCompanyService learnCompanyService;

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CustomFieldsRepository customFieldRepository;

    @Mock
    private ModelConvertor modelConvertor;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private MessageResourceHandler resourceHandler;

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
    public void testAddCompany_whenLearnCompanySaved() {
        //When all data is correct, it should assert true 
        Integer brokerId = 10;
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes",
                "PepcusHelp");

        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, companyId, company.getCompanyName(),
                company.getCompanyType());

        when(companyRepository.save(company)).thenReturn(company);
        when(learnCompanyService.addLearnCompany(company)).thenReturn(learnCompany);

        Company result = companyService.addCompany(company,1);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("PEP", result.getDisplayName());
        assertEquals(result.getCompanyName(), learnCompany.getCompanyName());
        assertEquals(result.getCompanyId(), learnCompany.getCompanyId());
        assertEquals(result.getCompanyType(), learnCompany.getCompanyType());
    }

    /**
     * To verify updateCompany method
     * @throws Exception 
     * 
     */

    @Test
    public void testUpdateCompany() throws Exception {

        Integer brokerId = 10;
        Integer companyId = 1;

        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, 10, "Pepcus", "IT");

        when(companyRepository.save(company)).thenReturn(company);
        when(companyRepository.findOne(companyId)).thenReturn(company);

        when(learnCompanyService.updateLearnCompany(company)).thenReturn(learnCompany);

        // Updating company name 
        company.setCompanyName("Pepcus - Updated");

        Company companyUpdated = null;
        try {
            String companyJson = ApiTestDataUtil.getJsonString(company);
            companyUpdated = companyService.updateCompany(company.getCompanyId(), companyJson,1);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", companyUpdated.getCompanyName());
    }

    /**
     * To verify updateCompany method when companyRepository doesn't find a match for given companyId.
     * @throws Exception 
     * 
     * 
     */

    @Test
    public void testUpdateCompanyForEntityNotFound() throws Exception {
        Integer brokerId = null;
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(companyId)).thenReturn(null);
        try {
            String companyJson = ApiTestDataUtil.getJsonString(company);
            companyService.updateCompany(company.getCompanyId(), companyJson,1);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }

    /**
     * To verify updateCompany method throws exception when trying to update 
     * a NotNull field with null value
     * 
     */

    @Test
    public void testUpdateCompany_UpdateNotNullFieldWithNull() {
        Integer companyId = 1;

        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes",
                "PepcusHelp");

        when(companyRepository.findOne(companyId)).thenReturn(company);

        // Updating company name with null
        String companyJson = "{\"companyName\": null}";

        Company companyUpdated = null;
        try {
            companyUpdated = companyService.updateCompany(company.getCompanyId(), companyJson, 1);
            fail("Expecting Exception");
        } catch (Exception ex) {
            assertTrue(ex instanceof ConstraintViolationException);
            ConstraintViolationException cv = (ConstraintViolationException) ex;
            assertEquals(1, cv.getConstraintViolations().size());
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
        String customFieldType = COMPANY;
        List<CustomFields> customFieldTestData = createCustomFieldsList();

        Mockito.when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, customFieldType)).thenReturn(customFieldTestData);

        Map<String, String> columnsToHeaderMap = companyService.appendRequiredAndCustomHeaderMap(companyId, customFieldType);
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
        String customFieldType = COMPANY;
        Mockito.when(customFieldRepository.findByCompanyIdAndCustomFieldType(companyId, customFieldType)).thenReturn(null);

        Map<String, String> columnsToHeaderMap = companyService.appendRequiredAndCustomHeaderMap(companyId, customFieldType);
        assertFalse(columnsToHeaderMap.containsKey("custom1"));
        assertEquals(FileUploadEnum.prepareColumnHeaderMap(customFieldType).size(), columnsToHeaderMap.size());
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
        Mockito.doThrow(appEx).when(companyServiceSpy).validateBrokerId(brokerId);

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
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateBrokerId(brokerId);

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
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateBrokerId(brokerId);

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
        Mockito.doReturn(testdataBroker).when(companyServiceSpy).validateBrokerId(brokerId);

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

    /**
     * Test to verify when DB has no duplicate record.
     * 
     */
    @Test
    public void testCheckDuplicateForNoDuplicates() {
        int recCount = 1;
        String record = getFileRecordForCompanyWithCustom1();
        FileImportResult result = new FileImportResult();
        Integer brokerId = 1;
        String companyName = "Pepcus Software Services";

        when(companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId)).thenReturn(null);

        // when no duplicate record exists in DB.
        boolean isDuplicate = companyService.checkDuplicate(recCount, record, result, brokerId);
        assertTrue(!isDuplicate);
    }

    /**
     * Test to verify when company exists in DB and broker id has no special
     * case.
     * 
     */
    @Test
    public void testCheckDuplicateForExistsAndNoSpecialCase() {
        int recCount = 1;
        String record = getFileRecordForCompanyWithCustom1();
        FileImportResult result = new FileImportResult();
        int failedRecords = result.getNumFailedRecords();
        Integer brokerId = 2;
        String companyName = "Pepcus Software Services";
        Company company = createCompany(1, companyName, "Software", "PEP", new Date(), "Special", null, "This is search help", brokerId);

        when(companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId)).thenReturn(company);

        // when some duplicate record exists in DB.
        boolean isDuplicate = companyService.checkDuplicate(recCount, record, result, brokerId);
        assertTrue(isDuplicate);
        assertEquals(failedRecords + 1, result.getNumFailedRecords());
    }

    /**
     * Test to verify when company exists in DB and broker id has a special case
     * and company exists in DB with custom1 field value.
     * 
     */
    @Test
    public void testCheckDuplicateForExistsAndSpecialCaseWithCustom1() {
        int recCount = 1;
        String record = getFileRecordForCompanyWithCustom1();
        FileImportResult result = new FileImportResult();
        int failedRecords = result.getNumFailedRecords();
        Integer brokerId = ApplicationConstants.SPECIAL_CASE_BROKER2;
        String companyName = "Pepcus Software Services";
        String custom1Value = "12";
        Company company = createCompany(1, companyName, "Software", "PEP", new Date(), "Special", custom1Value, "This is search help",
                brokerId);

        when(companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId)).thenReturn(company);
        when(companyRepository.findFirstByCompanyNameAndCustom1AndBroker(companyName, custom1Value, brokerId)).thenReturn(company);

        // when some duplicate record exists in DB.
        boolean isDuplicate = companyService.checkDuplicate(recCount, record, result, brokerId);
        assertTrue(isDuplicate);
        assertEquals(failedRecords + 1, result.getNumFailedRecords());
    }

    /**
     * Test to verify when company exists in DB and broker id has a special case
     * and no company exists in DB with custom1 field value.
     * 
     */
    @Test
    public void testCheckDuplicateForExistsAndSpecialCaseWithNoCustom1() {
        int recCount = 1;
        String record = getFileRecordForCompanyWithCustom1();
        FileImportResult result = new FileImportResult();
        Integer brokerId = ApplicationConstants.SPECIAL_CASE_BROKER2;
        String companyName = "Pepcus Software Services";
        String custom1Value = "12";
        Company company = createCompany(1, companyName, "Software", "PEP", new Date(), "Special", custom1Value, "This is search help",
                brokerId);

        when(companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId)).thenReturn(company);
        when(companyRepository.findFirstByCompanyNameAndCustom1AndBroker(companyName, custom1Value, brokerId)).thenReturn(null);

        // when no duplicate record exists in DB.
        boolean isDuplicate = companyService.checkDuplicate(recCount, record, result, brokerId);
        assertTrue(!isDuplicate);
    }


    /**
     * Test PopulateAndSaveToDB when record is successfully saved
     */
    @Test
    public void testPopulateAndSaveToDB_RecordSave_Success() {
        FileImportResult fileImportResult = new FileImportResult();
        int recCount = 13;
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        List<Object> companyColumnValues = ApiTestDataUtil.getCompanyColumnValuesList();
        Map<String, String> companyColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForComapny();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForCompany();

        List<Object> locationColumnValues = ApiTestDataUtil.getLocationsColumnValuesList();
        Map<String, String> locationColumnsToHeaderMap = ApiTestDataUtil.getLocationColumnsToHeadersMap();

        try {
            PowerMockito.doReturn(companyColumnValues).when(FileImportUtil.class, "populateColumnValues", record, companyColumnsToHeaderMap,
                    headerIndexMap);

            PowerMockito.doReturn(locationColumnValues).when(FileImportUtil.class, "populateColumnValues", record,
                    locationColumnsToHeaderMap,
                    headerIndexMap);

        } catch (Exception e) {
            fail("Exeption not expected");
        }
        
        int expectedSuccessCount = fileImportResult.getNumSuccessRecords() + 1;

        Mockito.doReturn(1).when(fileDataRepository).saveCompanyRecord(Mockito.anyListOf(String.class), Mockito.anyListOf(Object.class),
                Mockito.anyListOf(String.class), Mockito.anyListOf(Object.class));

        Company company = createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(1)).thenReturn(company);

        //Mock call to  add LearnCompany for bulk
        when(learnCompanyService.addLearnCompanyForBulk(Matchers.any())).thenReturn(1);

        companyService.populateAndSaveToDB(record, companyColumnsToHeaderMap, locationColumnsToHeaderMap, headerIndexMap,
                fileImportResult, recCount);

        assertEquals(1, fileImportResult.getNumSuccessRecords());
    }

    /**
     * Test PopulateAndSaveToDB when record saved is failed
     * 
     */
    @Test
    public void testPopulateAndSaveToDB_RecordSave_Failed() {
        FileImportResult fileImportResult = new FileImportResult();
        int recCount = 13;
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        List<Object> companyColumnValues = ApiTestDataUtil.getCompanyColumnValuesList();
        Map<String, String> companyColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForComapny();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForCompany();

        List<Object> locationColumnValues = ApiTestDataUtil.getLocationsColumnValuesList();
        Map<String, String> locationColumnsToHeaderMap = ApiTestDataUtil.getLocationColumnsToHeadersMap();

        try {
            PowerMockito.doReturn(companyColumnValues).when(FileImportUtil.class, "populateColumnValues", record, companyColumnsToHeaderMap,
                    headerIndexMap);

            PowerMockito.doReturn(locationColumnValues).when(FileImportUtil.class, "populateColumnValues", record,
                    locationColumnsToHeaderMap, headerIndexMap);

        } catch (Exception e) {
            fail("Exeption not expected");
        }

        int expectedSuccessCount = fileImportResult.getNumSuccessRecords();
        int expectedFailureCount = fileImportResult.getNumFailedRecords() + 1;
        int expectedFailedRecordsListSize = fileImportResult.getFailedRecords().size() + 1;

        RuntimeException ex = new RuntimeException();
        ex.initCause(new DataTruncation(0, true, true, 12, 13));
        Mockito.doThrow(ex).when(fileDataRepository).saveCompanyRecord(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        companyService.populateAndSaveToDB(record, companyColumnsToHeaderMap, locationColumnsToHeaderMap, headerIndexMap, fileImportResult,
                recCount);

        assertEquals(expectedSuccessCount, fileImportResult.getNumSuccessRecords());
        assertEquals(expectedFailureCount, fileImportResult.getNumFailedRecords());
        assertEquals(expectedFailedRecordsListSize, fileImportResult.getFailedRecords().size());
    }

    /**
     * Test PopulateAndSaveToDB when populateColumnsValues throws arrayIndexOutOfBound Exception
     */
    @Test
    public void testPopulateAndSaveToDB_MissingFieldExcepion() {
        FileImportResult fileImportResult = new FileImportResult();
        int recCount = 13;
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        Map<String, String> companyColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForComapny();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForCompany();
        Map<String, String> locationColumnsToHeaderMap = ApiTestDataUtil.getLocationColumnsToHeadersMap();

        ArrayIndexOutOfBoundsException ex = new ArrayIndexOutOfBoundsException("One or more fields Missing");
        try {
            PowerMockito.doThrow(ex).when(FileImportUtil.class, "populateColumnValues", record, companyColumnsToHeaderMap, headerIndexMap);
        } catch (Exception e) {
            fail("Exeption not expected");
        }


        int expectedSuccessCount = fileImportResult.getNumSuccessRecords();
        int expectedFailureCount = fileImportResult.getNumFailedRecords() + 1;
        int expectedFailedRecordsListSize = fileImportResult.getFailedRecords().size() + 1;
        
        companyService.populateAndSaveToDB(record, companyColumnsToHeaderMap, locationColumnsToHeaderMap, headerIndexMap, fileImportResult,
                recCount);
        
        assertEquals(expectedSuccessCount, fileImportResult.getNumSuccessRecords());
        assertEquals(expectedFailureCount, fileImportResult.getNumFailedRecords());
        assertEquals(expectedFailedRecordsListSize, fileImportResult.getFailedRecords().size());
    }

    /**
     * Test process records when validateAndFilterCustomHeaders throws unmapped headers exception
     */
    @Test
    public void testProcessRecords_UnmappedHeaderException() {
        int companyId = 12345;
        List<String> records = ApiTestDataUtil.getCsvRecords();
        Company broker = ApiTestDataUtil.createCompany();
        broker.setCompanyId(companyId);

        String[] customHeaders = new String[] { "CLIENT_TYPE", "BUSINESS_ID" };
        ApplicationException expectedException = ApplicationException.createFileImportError(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS,
                StringUtils.join(customHeaders, COMMA_SEPARATOR));

        // Mock validateAndFilterCustomHeaders to throw exception for UNMAPPED_CUSTOM_HEADERS
        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doThrow(expectedException).when(FileImportUtil.class, "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        // Mock appendRequiredAndCustomHeaderMap to return desired test data
        Map<String, String> columnToHeaderMap = ApiTestDataUtil.getColumnsToHeadersMapForComapny();
        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doReturn(columnToHeaderMap).when(companyServiceSpy)
                .appendRequiredAndCustomHeaderMap(Matchers.anyInt(),
                        Matchers.anyString());

        // Call processRecords and expect the mocked exception thrown by validateAndFilterCustomHeaders
        try {
            companyServiceSpy.processRecords(records, broker);
        } catch (ApplicationException ex) {
            assertNotNull(ex);
            assertEquals(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS, ex.getApiErrorCode());
        }
    }

    /**
     * Test process records for bank csv records
     */
    @Test
    public void testProcessRecords_BlankRecords() {
        List<String> records = ApiTestDataUtil.getBlankCsvRecordsForCompany();
        Company broker = ApiTestDataUtil.createCompany();
        int companyId = 12345;
        broker.setCompanyId(companyId);

        // Mock validateAndFilterCustomHeaders to doNothing
        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class, "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        Map<String, String> columnToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForComapny();
        CompanyService companyServiceSpy = Mockito.spy(new CompanyService());
        Mockito.doReturn(columnToHeaderMap).when(companyServiceSpy)
                .appendRequiredAndCustomHeaderMap(Matchers.anyInt(),
                        Matchers.anyString());

        // call processRecords
        FileImportResult fileImportResult = companyServiceSpy.processRecords(records, broker);

        assertEquals(3, fileImportResult.getNumBlankRecords());
    }

    /**
     * Test process records for Null Broker
     */
    @Test
    public void testProcessRecords_NullBroker() {
        List<String> records = ApiTestDataUtil.getBlankCsvRecordsForCompany();
        Company broker = null;

        try {
            companyService.processRecords(records, broker);
        } catch (ApplicationException appExp) {
            assertNotNull(appExp);
            assertEquals(APIErrorCodes.INVALID_BROKER_ID, appExp.getApiErrorCode());
        }
    }

    /**
     * Test processRecords for Null Records
     */
    @Test
    public void testProcessRecords_NullRecords() {
        List<String> records = null;
        Company broker = ApiTestDataUtil.createCompany();
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            companyService.processRecords(records, broker);
        } catch (ApplicationException appExp) {
            assertNotNull(appExp);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, appExp.getApiErrorCode());
        }
    }
    
    /**
     * Test SaveCompanyRecord
     */
    @Test
    public void testSaveCompanyRecord() {
        Integer companyId = 1;
        Integer learnCompanyId = 2;
        Company company = createCompany();

        List<String> companyColumns = ApiTestDataUtil.getCompanyColumnList();
        List<Object> companyColumnsValues = ApiTestDataUtil.getCompanyColumnValuesList();
        List<String> locationColumns = ApiTestDataUtil.getLocationColumnList();
        List<Object> locationColumnValues = ApiTestDataUtil.getLocationsColumnValuesList();

        Mockito.when(fileDataRepository.saveCompanyRecord(companyColumns, companyColumnsValues, locationColumns,
                locationColumnValues)).thenReturn(companyId);

        when(companyRepository.findOne(companyId)).thenReturn(company);

        Mockito.when(learnCompanyService.addLearnCompanyForBulk(company)).thenReturn(learnCompanyId);

        companyService.saveCompanyRecord(companyColumnsValues, locationColumnValues, companyColumns, locationColumns);
    }
    
    /**
     * Test validateConfigurationIdFromDB method.
     */
    @Test
    public void testValidateConfigurationIdFromDB() {
        Integer roleId = 5;
        Integer configurationId = 1;
        Integer companyId = 2;
        Configuration configuration = ApiTestDataUtil.createConfiguration(1, 2, "ABC", "test config");

        when(configurationRepository.findFirstByConfigurationIdAndCompanyId(configurationId, companyId))
                .thenReturn(configuration);

        boolean isValid = companyService.validateConfigurationIdFromDB(configurationId, companyId);

        assertTrue(isValid);
    }

}
