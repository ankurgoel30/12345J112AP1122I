package com.thinkhr.external.api.services.upload;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.EMAIL_PATTERN;
import static com.thinkhr.external.api.ApplicationConstants.MAX_PHONE_LENGTH;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FILE_EXTENSION_IMPORT;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getMissingHeaders;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getRequiredHeaders;
import static com.thinkhr.external.api.services.utils.FileImportUtil.readFileContent;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.FileImportResult;

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
    public static List<String> validateAndGetFileContent (MultipartFile fileToImport, String resource, int maxRecord) throws ApplicationException {

        String fileName = fileToImport.getOriginalFilename();

        // Validate if file has valid extension
        if (!FilenameUtils.isExtension(fileName,VALID_FILE_EXTENSION_IMPORT)) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.INVALID_FILE_EXTENTION, fileName, VALID_FILE_EXTENSION_IMPORT);
        }

        //validate if files has no records
        if (fileToImport.isEmpty()) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }

        // Read all records from file
        List<String> fileContents = readFileContent(fileToImport);

        validateFileContents(fileContents, fileName, resource, maxRecord);

        return fileContents;
    }


    /**
     * Validate file contents
     * 
     * @param fileContents
     * @param fileName
     * @param resource
     */
    public static void validateFileContents(List<String> fileContents, String fileName, String resource, int maxRecord) {
        
        if (fileContents == null || fileContents.isEmpty() || fileContents.size() < 2) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }

        if (fileContents.size() - 1 > maxRecord) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                    String.valueOf(maxRecord));
        }

        String headerLine = fileContents.get(0);
        
        // Validate for missing headers. File must container all expected columns, if not, return from here.
        String[] headers = headerLine.split(",");

        String[] requiredHeaders = getRequiredHeaders(resource) ;
        String[] missingHeadersIfAny = getMissingHeaders(headers, requiredHeaders);

        if (missingHeadersIfAny != null && missingHeadersIfAny.length > 0) {

            String requiredHeadersStr = String.join(",", requiredHeaders);

            String missingHeadersStr = String.join(",", missingHeadersIfAny);
            
            if(fileName != null){
                throw ApplicationException.createBulkImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS, fileName, missingHeadersStr,
                        requiredHeadersStr);
            }else{
                
                missingHeadersStr = removeUnderscoreAndApplyCamelCase(missingHeadersStr.toLowerCase());
                requiredHeadersStr = removeUnderscoreAndApplyCamelCase(requiredHeadersStr.toLowerCase());
                throw ApplicationException.createBulkImportError(APIErrorCodes.MISSING_REQUIRED_FIELDS, resource, missingHeadersStr,
                        requiredHeadersStr);
            }

            
        }

    }
    
    /**
     * Create Camel Casing for strings with underscore
     * 
     * @param str
     * @return
     */
    private static String removeUnderscoreAndApplyCamelCase(String str) {
        
        StringBuilder strBuild = new StringBuilder(str);
        int index = strBuild.indexOf(UNDERSCORE);
        while (index >= 0) {
            strBuild.replace(index, index+2, Character.toString(Character.toUpperCase(strBuild.charAt(index+1))));
            index = strBuild.indexOf(UNDERSCORE, index + 1);
        }
        
        return strBuild.toString();
    }


    /**
     * To validate email field
     * @param record
     * @param email
     * @param fileImportResult
     * @param resourceHandler
     */
    public static boolean validateEmail(String record, String email,
            FileImportResult fileImportResult,
            MessageResourceHandler resourceHandler) {
        
        Pattern pattern = Pattern.compile(EMAIL_PATTERN); 
        Matcher matcher = pattern.matcher(email);  
        if (!matcher.matches()) {  
            fileImportResult.addFailedRecord(record, 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.INVALID_EMAIL, email), 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return false;
        }  
        
        return true;
    }

    /**
     * To Validate required fields
     * 
     * @param record
     * @param requiredFields
     * @param headerIndexMap
     * @param fileImportResult
     */
    public static boolean validateRequired(String record,
            List<String> requiredFields,
            Map<String, Integer> headerIndexMap,
            FileImportResult fileImportResult,
            MessageResourceHandler resourceHandler) {

        if (record == null) {
            return true; //Do nothing
        }

        if (requiredFields == null || requiredFields.isEmpty()) {
            //No required fields
            return true;
        }
        String [] colValues = record.split(COMMA_SEPARATOR);

        //Username is generated if not available so skipping it from required fields
        requiredFields.remove(FileUploadEnum.USER_USER_NAME.getHeader());// THR-3927

        for (String field : requiredFields) {
            Integer index = headerIndexMap.get(field); 
            if (index == null || index >= colValues.length || StringUtils.isBlank(colValues[index])) {
                fileImportResult.addFailedRecord(record, 
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.MISSING_REQUIRED_FIELD, field), 
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
                return false;
            }
        }

        return true;
    }

    /**
     * To validate email field
     * @param record
     * @param email
     * @param fileImportResult
     * @param resourceHandler
     */
    public static boolean validatePhone(String record, String phoneNo,
            FileImportResult fileImportResult,
            MessageResourceHandler resourceHandler) {

        if (!StringUtils.isBlank(phoneNo) && phoneNo.length() > MAX_PHONE_LENGTH) {
            fileImportResult.addFailedRecord(record,
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.INVALID_PHONE, phoneNo,
                            String.valueOf(MAX_PHONE_LENGTH)),
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return false;
        }

        return true;
    }

}
