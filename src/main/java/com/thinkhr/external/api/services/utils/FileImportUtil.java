package com.thinkhr.external.api.services.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileImportUtil {
    /**
     * Finds if any required header is missing from given set of headers
     * 
     * @param String[] headersInFile
     * @param String[] requiredHeaders
     * @return String[] Array of missing headers 
     */
    public static String[] getMissingHeaders(String[] presentHeaders, String[] requiredHeaders) {
        Set<String> headersInFileSet = new HashSet<String>(Arrays.asList(presentHeaders));
        Set<String> requiredHeadersSet = new HashSet<String>(Arrays.asList(requiredHeaders));
        requiredHeadersSet.removeAll(headersInFileSet);

        String[] missingHeaders = new String[requiredHeadersSet.size()];
        return requiredHeadersSet.toArray(missingHeaders);
    }

    public static boolean hasValidExtension(String fileName, String... validExtention) {
        return FilenameUtils.isExtension(fileName, validExtention);
    }

    public static List<String> readFileContent(MultipartFile file) throws IOException {
        BufferedReader br;
        List<String> result = new ArrayList<>();
        String line;
        InputStream is = file.getInputStream();
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    public static Map<String, List<String>> getFieldsToHeaderMapForCompanyCSV() {
        Map<String, List<String>> fieldsToHeaderMap = new LinkedHashMap<String, List<String>>();

        //clientName
        fieldsToHeaderMap.put("companyName", Arrays.asList(new String[] { "CLIENT_NAME" }));

        //displayName
        fieldsToHeaderMap.put("displayName", Arrays.asList(new String[] { "DISPLAY_NAME" }));

        //companyPhone
        fieldsToHeaderMap.put("companyPhone", Arrays.asList(new String[] { "PHONE" }));

        ///officeLocation
        fieldsToHeaderMap.put("officeLocation", Arrays.asList(new String[] { "ADDRESS", "ADDRESS2", "CITY", "STATE", "ZIP" }));

        //industry
        fieldsToHeaderMap.put("industry", Arrays.asList(new String[] { "INDUSTRY" }));

        //companySize
        fieldsToHeaderMap.put("companySize", Arrays.asList(new String[] { "COMPANY_SIZE" }));

        //producer
        fieldsToHeaderMap.put("producer", Arrays.asList(new String[] { "PRODUCER" }));

        //custom1
        fieldsToHeaderMap.put("custom1", Arrays.asList(new String[] { "BUSINESS_ID" }));

        //custom2
        fieldsToHeaderMap.put("custom2", Arrays.asList(new String[] { "BRANCH_ID" }));

        //custom3
        fieldsToHeaderMap.put("custom3", Arrays.asList(new String[] { "CLIENT_ID" }));

        //custom4
        fieldsToHeaderMap.put("custom4", Arrays.asList(new String[] { "CLIENT_TYPE" }));

        return fieldsToHeaderMap;
    }

    /**
     * This method returns a map for columns in table for Company entity to headers in csv.
     * Key in map is the name of column in db.
     * Value in map is name corresponding column in CSV file.
     */
    public static Map<String, String> getColumnsToHeaderMapForCompanyRecord() {
        Map<String, String> columnsToHeaderMap = new LinkedHashMap<String, String>();

        //clientName
        columnsToHeaderMap.put("client_name", "CLIENT_NAME");

        //displayName
        columnsToHeaderMap.put("display_name", "DISPLAY_NAME");

        //companyPhone
        columnsToHeaderMap.put("client_phone", "PHONE");

        //industry
        columnsToHeaderMap.put("industry", "INDUSTRY");

        //companySize
        columnsToHeaderMap.put("companysize", "COMPANY_SIZE");

        //producer
        columnsToHeaderMap.put("producer", "PRODUCER");

        // get this map from app_throne_custom_fields table
        //custom1
        columnsToHeaderMap.put("custom1", "BUSINESS_ID");

        //custom2
        columnsToHeaderMap.put("custom2", "BRANCH_ID");

        //custom3
        columnsToHeaderMap.put("custom3", "CLIENT_ID");

        //custom4
        columnsToHeaderMap.put("custom4", "CLIENT_TYPE");

        return columnsToHeaderMap;
    }

    /**
     * This method returns a map for columns in locations table to headers in csv.
     * Key in map is the name of column in db for locations table.
     * Value in map is name of corresponding column in CSV file.
     */
    public static Map<String, String> getColumnsToHeaderMapForLocationRecord() {
        Map<String, String> columnsToHeaderMap = new LinkedHashMap<String, String>();

        //address
        columnsToHeaderMap.put("address", "ADDRESS");

        //address2
        columnsToHeaderMap.put("address2", "ADDRESS2");

        //city
        columnsToHeaderMap.put("city", "CITY");

        //state
        columnsToHeaderMap.put("state", "STATE");

        //zip
        columnsToHeaderMap.put("zip", "ZIP");

        return columnsToHeaderMap;
    }

    /**
     * This method populates the given array columnValues from 
     * columns,columnToHeaderMap,splitValues,headerIndexMap
     */
    public static void populateColumnValues(Object[] columnValues, String[] columns, Map<String, String> columnToHeaderMap,
            String[] splitValues, Map<String, Integer> headerIndexMap) {
        int k = 0;
        for (String column : columns) {
            String headerinCsv = columnToHeaderMap.get(column);
            if (headerinCsv != null) {
                Integer indexTolookInSplitRecord = headerIndexMap.get(headerinCsv);
                String columnValueInCsv = splitValues[indexTolookInSplitRecord];
                columnValues[k++] = columnValueInCsv;
            }
        }
    }

}
