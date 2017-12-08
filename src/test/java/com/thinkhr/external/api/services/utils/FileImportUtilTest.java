package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_COLUMN_VALUE;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createFileImportResultWithFailedRecords;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createFileImportResultWithNoFailedRecords;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getAllColumnsToHeadersMap;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getAllHeadersForCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getAllHeadersForCompanyWithExtraHeaders;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getAvailableHeadersForCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getColumnsToHeadersMap;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCustomHeadersForCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getExtraCustomHeadersForCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getFileRecordWithCustomFields;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getHeaderIndexMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.response.APIMessageUtil;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { FileImportUtil.class, APIMessageUtil.class })
@PowerMockIgnore({ "javax.management.*" })
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class FileImportUtilTest {

    @Autowired
    private MessageResourceHandler resourceHandler ;
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to verify when there is at least one missing header. 
     */
    @Test
    public void testGetMissingHeadersIfMissing() {
        String[] availableHeaders = getAvailableHeadersForCompany();
        String[] requiredHeaders = REQUIRED_HEADERS_COMPANY_CSV_IMPORT;

        String[] missingHeaders = FileImportUtil.getMissingHeaders(availableHeaders, requiredHeaders);
        assertEquals(3, missingHeaders.length);
    }

    /**
     * Test to verify when both arguments are same and no missing headers.
     */
    @Test
    public void testGetMissingHeadersIfNotMissing() {
        String[] requiredHeaders = REQUIRED_HEADERS_COMPANY_CSV_IMPORT;

        /*
         * Passing requiredHeaders as available headers to verify that method should return expected
         * result when both the arguments are same
         */
        String[] missingHeaders = FileImportUtil.getMissingHeaders(requiredHeaders, requiredHeaders);
        assertEquals(0, missingHeaders.length);
    }

    /**
     * Test to verify when all the required headers are missing.
     */
    @Test
    public void testGetMissingHeadersIfNotAvailable() {
        String[] availableHeaders = new String[0];
        String[] requiredHeaders = REQUIRED_HEADERS_COMPANY_CSV_IMPORT;

        String[] missingHeaders = FileImportUtil.getMissingHeaders(availableHeaders, requiredHeaders);
        assertEquals(requiredHeaders.length, missingHeaders.length);
    }

    /**
     * Test to verify when file is not available.
     * 
     */
    @Test
    public void testReadFileContentsIfFileNotAvailable() {
        //When read
        String fileContent = "abcc";
        MockMultipartFile file = new MockMultipartFile("test", fileContent.getBytes());
        List<String> fileContents = FileImportUtil.readFileContent(file);
        assertNotEquals("Line1", fileContents.get(0));
    }

    /**
     * Test to verify when the test succeeds and reading file contents properly.
     * 
     */
    @Test
    public void testReadFileContents(){
        File file = new File("src/test/resources/testdata/testReadFileContent.csv");
        FileInputStream input;
        MultipartFile multipartFile = null;
        try {
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e) {
            fail("Exception not expected");
        }
        List<String> fileContents = FileImportUtil.readFileContent(multipartFile);
        assertEquals("Line1", fileContents.get(0));
        assertEquals("Line2", fileContents.get(1));
        assertEquals("Line3", fileContents.get(2));
    }
    
    /**
     * Test to verify when file is empty.
     * 
     */
    @Test
    public void testReadFileContentsIfEmptyFile(){
        File file = new File("src/test/resources/testdata/testReadEmptyFile.csv");
        FileInputStream input;
        MultipartFile multipartFile = null;
        try {
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e) {
            fail("Exception not expected");
        }
        List<String> fileContents = FileImportUtil.readFileContent(multipartFile);
        assertNotNull(fileContents); 
        assertNotEquals("Line1", fileContents.get(0));  
    }

    /**
     * Test to verify when MultipartFile is null.
     */
    @Test
    public void testReadContentForNull() {
        List<String> fileContent = FileImportUtil.readFileContent(null);
        assertEquals(null, fileContent);
    }

    /**
     * Test to verify when populating all the column values. 
     */
    @Test
    public void testPopulateColumnValues() {
        String fileRow = getFileRecordWithCustomFields();

        String[] fileRecords = fileRow.split(COMMA_SEPARATOR);

        Map<String, String> columnToHeaderMap = getColumnsToHeadersMap();
        Map<String, Integer> headerIndexMap = getHeaderIndexMap();

        List<Object> columnValues = FileImportUtil.populateColumnValues(fileRow, columnToHeaderMap, 
                headerIndexMap);
        assertEquals(fileRecords.length, columnValues.size());
        assertTrue(columnValues.contains("Pepcus Software Services"));
        assertTrue(columnValues.contains("pepcus"));
        assertTrue(columnValues.contains("9213234567"));
        assertTrue(columnValues.contains("IT"));
        assertTrue(columnValues.contains("20"));
        assertTrue(columnValues.contains("Ajay Jain"));
    }

    /**
     * Test to verify when the header index is empty.
     */
    @Test
    public void testPopulateColumnValuesForEmptyHeaderIndex() {
        String fileRow = getFileRecordWithCustomFields();
        Map<String, String> columnToHeaderMap = getColumnsToHeadersMap();
        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();

        List<Object> columnValues = FileImportUtil.populateColumnValues(fileRow, columnToHeaderMap, 
                headerIndexMap);
        assertTrue(columnValues.isEmpty());
        assertEquals(headerIndexMap.size(), columnValues.size());
    }

    /**
     * Test to verify when file record is not available.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPopulateColumnValuesWithNoFileRecord() {
        String fileRow = DEFAULT_COLUMN_VALUE;
        Map<String, String> columnToHeaderMap = getColumnsToHeadersMap();
        Map<String, Integer> headerIndexMap = getHeaderIndexMap();

        List<Object> columnValues = FileImportUtil.populateColumnValues(fileRow, columnToHeaderMap, headerIndexMap);
        assertNotNull(columnValues);
        assertTrue(columnValues.isEmpty());
    }

    /**
     * Test to verify when response file has no failed records. 
     * 
     */
    @Test
    public void testCreateResponseFileWithNoFailedRecords() {
        FileImportResult fileImportResult = createFileImportResultWithNoFailedRecords();
        File responseFile = null;
        List<String> fileContents = null;
        try {
            responseFile = FileImportUtil.createReponseFile(fileImportResult, resourceHandler);
            BufferedReader reader = new BufferedReader(new FileReader(responseFile));
            fileContents = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            fail("Exception not expected."); 
        }
        assertNotNull(responseFile);
        assertTrue(responseFile.exists());
        assertEquals("Total Number of Records: 10", fileContents.get(1));
        assertEquals("Total Number of Successful Records: 10", fileContents.get(2));
        assertEquals("Total Number of Failure  Records: 0", fileContents.get(3));
    }
    
    /**
     * Test to verify when response file has some failed records.
     */
    @Test
    public void testCreateResponseFileWithFailedRecords() {
        FileImportResult fileImportResult = createFileImportResultWithFailedRecords();
        File responseFile = null;
        List<String> fileContents = null;
        try {
            responseFile = FileImportUtil.createReponseFile(fileImportResult, resourceHandler);
            BufferedReader reader = new BufferedReader(new FileReader(responseFile));
            fileContents = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            fail("Exception not expected.");
        }
        assertNotNull(responseFile);
        assertTrue(responseFile.exists());
        assertEquals("Total Number of Records: 10", fileContents.get(1));
        assertEquals("Total Number of Successful Records: 7", fileContents.get(2));
        assertEquals("Total Number of Failure  Records: 3", fileContents.get(3));
    }

    /**
     * Test to verify when all customHeaders in csv has a corresponding mapped database field.
     */
    @Test
    public void testValidateAndFilterCustomHeaders() {
        String[] allHeaders = getAllHeadersForCompany();
        Map<String, String> columnsToHeaderMap = getAllColumnsToHeadersMap();
        Set<String> customHeaders = getCustomHeadersForCompany();
        
        PowerMockito.mockStatic(FileImportUtil.class);
        PowerMockito.doReturn(customHeaders).when(FileImportUtil.class);
        FileImportUtil.filterCustomFieldHeaders(allHeaders, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);

        try {
            FileImportUtil.validateAndFilterCustomHeaders(allHeaders, columnsToHeaderMap.values(), allHeaders, resourceHandler);
        } catch (ApplicationException e) {
            fail("Exception not expected");
        }
    }

    /**
     * Test to verify when customHeaders in csv does not have a mapping field in
     * DB.
     * 
     */
    @Test
    public void testValidateAndFilterCustomHeadersForFailure() {
        String[] allHeaders = getAllHeadersForCompanyWithExtraHeaders();
        Map<String, String> columnsToHeaderMap = getAllColumnsToHeadersMap();
        Set<String> customHeaders = getExtraCustomHeadersForCompany();

        PowerMockito.mockStatic(FileImportUtil.class);
        PowerMockito.doReturn(customHeaders).when(FileImportUtil.class);
        FileImportUtil.filterCustomFieldHeaders(allHeaders, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);

        try {
            FileImportUtil.validateAndFilterCustomHeaders(allHeaders, columnsToHeaderMap.values(), allHeaders, resourceHandler);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.FILE_READ_ERROR, ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify when the list of customHeaders is filtered and returned.
     */
    @Test
    public void testFilterCustomFieldHeaders() {
        String[] allHeaders = getAllHeadersForCompany();

        Set<String> customHeaders = FileImportUtil.filterCustomFieldHeaders(allHeaders, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
        // Should be equal.
        assertEquals(4, customHeaders.size());
    }
    
    /**
     * Test to verify when the list of customHeaders is empty.
     */
    @Test
    public void testFilterCustomFieldHeadersWithEmpty() {
        String[] allHeaders = null;

        Set<String> customHeaders = FileImportUtil.filterCustomFieldHeaders(allHeaders, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
        // Should be empty.
        assertTrue(customHeaders.isEmpty());
        assertEquals(0, customHeaders.size());
    }

}