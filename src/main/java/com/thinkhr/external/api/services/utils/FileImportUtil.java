package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY_CUSTOM_COLUMN_PREFIX;
import static com.thinkhr.external.api.ApplicationConstants.FILE_IMPORT_RESULT_MSG;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_USER_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.USER;
import static com.thinkhr.external.api.ApplicationConstants.USER_CUSTOM_COLUMN_PREFIX;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateFileContents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.BulkJsonModel;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.request.APIRequestHelper;
import com.thinkhr.external.api.response.APIMessageUtil;

/**
 * A utility class for performing all operations
 * on File Import.
 *  
 * @since 2-17-11-17
 * @author Ajay
 *
 */
public class FileImportUtil {

    /**
     * Finds if any required header is missing from given set of headers
     * 
     * @param String[] headersInFile
     * @param String[] requiredHeaders
     * @return String[] Array of missing headers 
     */
    public static String[] getMissingHeaders(String[] headersInFile, String[] requiredHeaders) {
        Set<String> headersInFileSet = null;
        Set<String> requiredHeadersSet = null;

        if (headersInFile != null) {
            headersInFileSet = new HashSet<String>(Arrays.asList(headersInFile));
        }
        if (requiredHeaders != null) {
            requiredHeadersSet = new HashSet<String>(Arrays.asList(requiredHeaders));
        }
        requiredHeadersSet.removeAll(headersInFileSet);

        String[] missingHeaders = new String[requiredHeadersSet.size()];
        return requiredHeadersSet.toArray(missingHeaders);
    }


    /**
     * Reads file content for given CSV
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String> readFileContent(MultipartFile file) {
        if (file == null) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            return br.lines().collect(Collectors.toList());
        } catch (IOException ioe) {
            throw ApplicationException.createBadRequest(APIErrorCodes.FILE_READ_ERROR, file.getName());
        }
    }

    /**
     * Populate column values
     * 
     * @param fileRow
     * @param columnToHeaderMap
     * @param headerIndexMap
     * @return
     */
    public static List<Object> populateColumnValues(String fileRow, Map<String, String> columnToHeaderMap,
            Map<String, Integer> headerIndexMap) throws ArrayIndexOutOfBoundsException {
        List<Object> columnValues = new ArrayList<Object>();
        String[] rowColValues = fileRow.split(COMMA_SEPARATOR);
        List<String> columns = null;

        if (columnToHeaderMap != null && columnToHeaderMap.size() > 0) {
            columns = new ArrayList<String>(columnToHeaderMap.keySet());
            if (headerIndexMap != null && headerIndexMap.size() > 0) {
                columns.forEach(col -> {
                    String csvHeader = columnToHeaderMap.get(col);
                    if (csvHeader != null && headerIndexMap.containsKey(csvHeader)) {
                        Integer colIndx = headerIndexMap.get(csvHeader);
                        columnValues.add(rowColValues[colIndx].trim());
                    } else {
                        columnValues.add(null);
                    }
                });
            }
        }
        return columnValues;
    }

    /**
     * This Function will create a response csv file from FileImportResult
     * @param FileimportResult fileImportResult
     * @return File
     * @throws IOException
     */
    public static File createReponseFile(FileImportResult fileImportResult, MessageResourceHandler resourceHandler) throws IOException {
        File responseFile = File.createTempFile("fileImportResponse", ".csv");
        FileWriter writer = new FileWriter(responseFile);

        if (fileImportResult != null) {
            String jobId = (String) APIRequestHelper.getRequestAttribute("jobId");

            String msg = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, FILE_IMPORT_RESULT_MSG,
                    String.valueOf(fileImportResult.getTotalRecords()), String.valueOf(fileImportResult.getNumSuccessRecords()),
                    String.valueOf(fileImportResult.getNumFailedRecords()), String.valueOf(fileImportResult.getNumBlankRecords()));

            String printResponse =  fileImportResult.printReport(jobId, msg, 
                    APIMessageUtil.getMessageFromResourceBundle(resourceHandler, APIErrorCodes.FAILED_RECORD),
                    APIMessageUtil.getMessageFromResourceBundle(resourceHandler, APIErrorCodes.FAILURE_CAUSE));
            writer.write(printResponse);
        }

        writer.close();
        return responseFile;
    }


    /**
     * Check if all customHeaders in csv has a database field  to which its value should be mapped.
     * If any custom header does not have mapping field then throw exception.
     * 
     * Not able to use batch processing here due to requirement of
     * dealing with failure records & adding data in more than one tables.
     * 
     * @param allHeadersInCsv
     * @param allMappedHeaders
     * @param resourceHandler
     */
    public static void validateAndFilterCustomHeaders(String[] allHeadersInCsv, 
            Collection<String> allMappedHeaders, String[] requiredHeaders
            , MessageResourceHandler resourceHandler) {

        Set<String> customHeaders = filterCustomFieldHeaders(allHeadersInCsv, requiredHeaders);

        String columnForFailureCause = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.FAILURE_CAUSE);

        customHeaders.remove(columnForFailureCause);// need to remove failedCauseColumn from customHeaders for the case when user tries to import response csv file

        customHeaders.removeAll(allMappedHeaders);// = customHeaders - allMappedHeaders
        if (!customHeaders.isEmpty()) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS,
                    StringUtils.join(customHeaders, COMMA_SEPARATOR));
        }
    }

    /**
     * This  function returns list of custom headers in csv.
     * Custom Headers =  allHeadersInCSV - requiredHeaders.
     * @param allHeaders Array of all headers found in csv.
     * @param nonCustomHeaders Array of required headers.
     * @return List<String>
     */
    public static Set<String> filterCustomFieldHeaders(String[] allHeaders, String[] nonCustomHeaders) {

        Set<String> customHeaders = new HashSet<String>();

        if (allHeaders == null) {
            return customHeaders;
        }
        //First add custom headers to all sets.
        customHeaders.addAll(Arrays.asList(allHeaders)); //Consider all headers as custom headers

        if (nonCustomHeaders != null) { //i.e all headers includes required headers
            customHeaders.removeAll(Arrays.asList(nonCustomHeaders));// after this operation allHeadersInCSVSet will have only custom headers
        }

        return customHeaders;

    }


    
    /**
     * Get value from given row for given index
     * @param row
     * @param index
     * @return
     */
    public static String getValueFromRow(String row, Integer index) {

        if (row == null || index == null || index < 0) {
            return null;
        }

        String[] colValues = row.split(COMMA_SEPARATOR);

        if (colValues == null || colValues.length <= 0 || index >= colValues.length) {
            return null;
        }
        return colValues[index];
    }

    

    /**
     * Get required headers
     * 
     * @param resource
     * @return
     */
    public static String[] getRequiredHeaders(String resource) {
        
        if (resource == null) {
            return null;
        }

        switch(resource) {
            case COMPANY : return REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
            case USER : return REQUIRED_HEADERS_USER_CSV_IMPORT;
        }
        
        return null;
    }
    
    /**
     * @param resource
     * @return
     */
    public static String getCustomFieldPrefix(String resource) {

        if (resource == null) {
            return null;
        }

        switch (resource) {
            case USER: return USER_CUSTOM_COLUMN_PREFIX;
            case COMPANY: return COMPANY_CUSTOM_COLUMN_PREFIX;
        }
        
        return null;
    }

    /**
     * Convert list of objects into list of strings 
     * 
     * @param list
     * @param resource
     * @return
     * @throws Exception 
     */
    public static List<String> validateAndGetContentFromModel(List<BulkJsonModel> list,
            String resource, int maxRecord) {

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> fileContents = new ArrayList<String>();

        fileContents.add((list.get(0)).getAttributeNamesAsCommaSeparated());
        for (BulkJsonModel obj : list) {
            fileContents.add(obj.getAttributeValueAsCommaSeparated());
        }
        validateFileContents(fileContents, null, resource, maxRecord);

        return fileContents;
    }
    
    /**
     * To Set Request Params for creating JSON bulk response from
     * FileImportResult
     * 
     * @param fileImportResult
     */
    public static void setRequestParamsForBulkJsonResponse(
            FileImportResult fileImportResult) {

        setRequestAttribute("totalRecords", fileImportResult.getTotalRecords());
        setRequestAttribute("failedRecords",
                fileImportResult.getNumFailedRecords());
        setRequestAttribute("successRecords",
                fileImportResult.getNumSuccessRecords());
        setRequestAttribute("failedList", fileImportResult.getFailedRecords());

    }


}
