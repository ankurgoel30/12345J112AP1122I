package com.thinkhr.external.api.services.upload;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

@RunWith(SpringRunner.class)
public class FileImportValidatorTest {

    /**
     * Test validateAndGetFileContent when MultipartFile object of a file
     * without csv extension is given as input
     */
    @Test
    public void testValidateAndGetFileContent_ForInvalidFileExtension() {
        File file = new File("src/test/resources/testdata/2_InvalidExtension.xlsx");

        FileInputStream input = null;
        MultipartFile multipartFile = null;

        try {
            input = new FileInputStream(file);

            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        } catch (IOException e1) {
            fail("IOException is not expected");
        }

        try {
            List<String> fileContents = FileImportValidator.validateAndGetFileContent(multipartFile);
            fail("Expecting validation exception for invalid extension of file");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.INVALID_FILE_EXTENTION, appEx.getApiErrorCode());
        }
    }

    /**
     * Test validateAndGetFileContent when MultipartFile object of a file with
     * csv extension but empty file is given as input
     */
    @Test
    public void testValidateAndGetFileContent_ForEmpty() {
        File file = new File("src/test/resources/testdata/1_EmptyCSV.csv");

        FileInputStream input = null;
        MultipartFile multipartFile = null;

        try {
            input = new FileInputStream(file);

            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        } catch (IOException e1) {
            fail("IOException is not expected");
        }

        try {
            List<String> fileContents = FileImportValidator.validateAndGetFileContent(multipartFile);
            fail("Expecting validation exception for invalid extension of file");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, appEx.getApiErrorCode());
        }

    }

    /**
     * Test validateAndGetFileContent when MultipartFile object of a file with
     * csv extension and all headers and 1 record
     */
    @Test
    public void testValidateAndGetFileContent_ValidFileWithRecord() {
        File file = new File("src/test/resources/testdata/7_Example1Rec.csv");

        FileInputStream input;
        try {
            input = new FileInputStream(file);

            MultipartFile multipartFile;

            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

            List<String> fileContents = FileImportValidator.validateAndGetFileContent(multipartFile);
            assertEquals(2, fileContents.size());
        } catch (IOException e1) {
            fail("IOException is not expected");
        }
    }

    /**
     * Test validateAndGetFileContent when MultipartFile object of a file with
     * csv extension and all headers and 100 record
     */
    @Test
    public void testValidateAndGetFileContent_ValidFileWith100Rec() {
        File file = new File("src/test/resources/testdata/9_Example100Rec.csv");

        FileInputStream input;
        try {
            input = new FileInputStream(file);

            MultipartFile multipartFile;

            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

            List<String> fileContents = FileImportValidator.validateAndGetFileContent(multipartFile);
            assertEquals(101, fileContents.size());
        } catch (IOException e1) {
            fail("IOException is not expected");
        }
    }

    /**
     * Test validateFileContents when fileContents is empty
     */
    @Test
    public void testValidateFileContents_ForEmptyContents() {
        List<String> fileContents = new ArrayList<String>();

        try {
            FileImportValidator.validateFileContents(fileContents, "Test.csv");
            fail("Expecting validation exception for no records");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, appEx.getApiErrorCode());
        }
    }

    /**
     * Test validateFileContents when fileContents is null
     */
    @Test
    public void testValidateFileContents_ForNullFileConents() {
        List<String> fileContents = null;

        try {
            FileImportValidator.validateFileContents(fileContents, "Test.csv");
            fail("Expecting validation exception for no records");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, appEx.getApiErrorCode());
        }
    }

    /**
     * Test validateFileContents when fileContents has only header line but no
     * records
     */
    @Test
    public void testValidateFileContents_OnlyHeadersNoRecords() {
        List<String> fileContents = new ArrayList<String>();
        fileContents.add(
                "CLIENT_NAME,DISPLAY_NAME,PHONE,ADDRESS,ADDRESS2,CITY,STATE,ZIP,INDUSTRY,COMPANY_SIZE,PRODUCER,BUSINESS_ID,BRANCH_ID,CLIENT_ID,CLIENT_TYPE");

        try {
            FileImportValidator.validateFileContents(fileContents, "Test.csv");
            fail("Expecting validation exception for no records");
        } catch (ApplicationException appEx) {
            Assert.assertNotNull(appEx);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, appEx.getApiErrorCode());
        }
    }

    /**
     * Test validateFileContents when fileContents has records > Max allowed records
     */
    @Test
    public void testValidateFileContents_MaxRecordExceed() {
        List<String> fileContents = new ArrayList<String>();
        fileContents.add(
                "CLIENT_NAME,DISPLAY_NAME,PHONE,ADDRESS,ADDRESS2,CITY,STATE,ZIP,INDUSTRY,COMPANY_SIZE,PRODUCER,BUSINESS_ID,BRANCH_ID,CLIENT_ID,CLIENT_TYPE");
        
        for (int i = 1; i <= MAX_RECORDS_COMPANY_CSV_IMPORT + 1; i++) {
            fileContents.add("record" + i);
        }

        try {
            FileImportValidator.validateFileContents(fileContents, "Test.csv");
            fail("Expecting validation exception for Max Records Exceed");
        } catch (ApplicationException appEx) {
            assertNotNull(appEx);
            assertEquals(APIErrorCodes.MAX_RECORD_EXCEEDED, appEx.getApiErrorCode());
        }

    }

    /**
     * Test validateFileContents when fileContents has records < Max allowed
     * records
     */
    @Test
    public void testValidateFileContents_RecordLessMaxRecords() {
        List<String> fileContents = new ArrayList<String>();
        fileContents.add(
                "CLIENT_NAME,DISPLAY_NAME,PHONE,ADDRESS,ADDRESS2,CITY,STATE,ZIP,INDUSTRY,COMPANY_SIZE,PRODUCER,BUSINESS_ID,BRANCH_ID,CLIENT_ID,CLIENT_TYPE");

        for (int i = 1; i <= MAX_RECORDS_COMPANY_CSV_IMPORT - 10; i++) {
            fileContents.add("record" + i);
        }

        FileImportValidator.validateFileContents(fileContents, "Test.csv");
    }

    /**
     * Test validateFileContents when fileContents has some missing headers
     */
    @Test
    public void testValidateFileContents_MissingHeaders() {
        List<String> fileContents = new ArrayList<String>();

        String[] requiredHeaders = REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
        String[] missingHeader = (String[]) ArrayUtils.subarray(requiredHeaders, 1, requiredHeaders.length);

        String missingHeaderLine = StringUtils.join(missingHeader, COMMA_SEPARATOR);
        fileContents.add(missingHeaderLine);

        for (int i = 1; i <= MAX_RECORDS_COMPANY_CSV_IMPORT - 10; i++) {
            fileContents.add("record" + i);
        }

        try {
            FileImportValidator.validateFileContents(fileContents, "Test.csv");
            fail("Expecting validation exception for Missing Headers");
        } catch (ApplicationException appEx) {
            assertNotNull(appEx);
            assertEquals(APIErrorCodes.MISSING_REQUIRED_HEADERS, appEx.getApiErrorCode());
        }
    }
}
