package com.thinkhr.external.api.services.upload;

import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
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
     * @param fileToImport
     * @param fileContents
     * @param headers
     * @throws ApplicationException
     * 
     */
    public static List<String> validateAndGetFileContent (MultipartFile fileToImport) throws ApplicationException {

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

        validateFileContents(fileContents, fileName);

        return fileContents;
    }


    /**
     * Validate file contents
     * @param fileContents
     * @param fileName
     */
    public static void validateFileContents(List<String> fileContents, String fileName) {
        
        if (fileContents == null || fileContents.isEmpty() || fileContents.size() < 2) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }

        if (fileContents.size() - 1 > MAX_RECORDS_COMPANY_CSV_IMPORT) {
            throw ApplicationException.createFileImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                    String.valueOf(MAX_RECORDS_COMPANY_CSV_IMPORT));
        }

        String headerLine = fileContents.get(0);
        
        // Validate for missing headers. File must container all expected columns, if not, return from here.
        String[] headers = headerLine.split(",");

        String[] missingHeadersIfAny = FileImportUtil.getMissingHeaders(headers, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);

        if (missingHeadersIfAny != null && missingHeadersIfAny.length > 0) {

            String requiredHeaders = String.join(",", REQUIRED_HEADERS_COMPANY_CSV_IMPORT);

            String missingHeaders = String.join(",", missingHeadersIfAny);

            throw ApplicationException.createFileImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS, fileName, missingHeaders,
                    requiredHeaders);
        }

    }

}
