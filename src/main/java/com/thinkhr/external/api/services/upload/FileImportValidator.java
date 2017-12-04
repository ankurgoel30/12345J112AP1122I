package com.thinkhr.external.api.services.upload;

import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_USER_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_USER_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FILE_EXTENSION_IMPORT;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.utils.FileImportUtil;

/**
 * To valid file import
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-26
 *
 */
public class FileImportValidator {


    /**
     * This function validates fileToimport and populates fileContens
     * 
     * @param fileToImport
     * @param resource
     * @throws ApplicationException
     * 
     */
    public static List<String> validateAndGetFileContent (MultipartFile fileToImport, String resource) throws ApplicationException {

        String fileName = fileToImport.getOriginalFilename();

        // Validate if file has valid extension
        if (!FilenameUtils.isExtension(fileName,VALID_FILE_EXTENSION_IMPORT)) {
            throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_FILE_EXTENTION, fileName, VALID_FILE_EXTENSION_IMPORT);
        }

        //validate if files has no records
        if (fileToImport.isEmpty()) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }

        // Read all records from file
        List<String> fileContents = FileImportUtil.readFileContent(fileToImport);

        validateFileContents(fileContents, fileName, resource);

        return fileContents;
    }


    /**
     * Validate file contents
     * 
     * @param fileContents
     * @param fileName
     * @param resource
     */
    public static void validateFileContents(List<String> fileContents, String fileName, String resource) {

        if (fileContents == null || fileContents.isEmpty() || fileContents.size() < 2) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }
        
        int maxRecord = getMaxRecords(resource);

        if (fileContents.size() > maxRecord) {
            throw ApplicationException.createFileImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                    String.valueOf(maxRecord));
        }

        String headerLine = fileContents.get(0);

        // Validate for missing headers. File must container all expected columns, if not, return from here.
        String[] headers = headerLine.split(",");

        String[] requiredHeaders = getRequiredHeaders(resource) ;
        String[] missingHeadersIfAny = FileImportUtil.getMissingHeaders(headers, requiredHeaders);

        if (missingHeadersIfAny != null && missingHeadersIfAny.length > 0) {

            String requiredHeadersStr = String.join(",", requiredHeaders);

            String missingHeaders = String.join(",", missingHeadersIfAny);

            throw ApplicationException.createFileImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS, fileName, missingHeaders,
                    requiredHeadersStr);
        }

    }


    /**
     * @param resource
     * @return
     */
    private static String[] getRequiredHeaders(String resource) {
        
        switch(resource) {
        case "COMPANY" : return REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
        case "USER" : return REQUIRED_HEADERS_USER_CSV_IMPORT;
        }
        
        return REQUIRED_HEADERS_COMPANY_CSV_IMPORT; //Let's make it default
    }


    /**
     * @param resource
     * @return
     */
    private static int getMaxRecords(String resource) {
        
        switch(resource) {
        case "COMPANY" : return MAX_RECORDS_COMPANY_CSV_IMPORT;
        case "USER" : return MAX_RECORDS_USER_CSV_IMPORT;
        }
        
        return MAX_RECORDS_COMPANY_CSV_IMPORT; //Let's make it default
    }

}
