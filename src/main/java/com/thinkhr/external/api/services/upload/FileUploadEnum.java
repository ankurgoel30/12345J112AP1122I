package com.thinkhr.external.api.services.upload;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum to keep mapping between database columns and file columns for upload
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-26
 *
 */
public enum FileUploadEnum {

    COMPANY("company"),
    LOCATION("location"),
    //COMPANY TABLE COLUMNS/HEADERS
    COMPANY_NAME("client_name", "CLIENT_NAME", "company"),
    COMPANY_DISPLAY_NAME("display_name", "DISPLAY_NAME", "company"),
    COMPANY_PHONE("client_phone", "PHONE", "company"),
    COMPANY_INDUSTRY("industry", "INDUSTRY", "company"),
    COMPANY_SIZE("companysize", "COMPANY_SIZE", "company"),
    COMPANY_PRODUCER("producer", "PRODUCER", "company"),

    //LOCATION COLUMN/HEADERS 
    LOCATION_ADDRESS("address", "ADDRESS", "location"),
    LOCATION_ADDRESS2("address2", "ADDRESS2", "location"),
    LOCATION_CITY("city", "CITY", "location"),
    LOCATION_STATE("state", "STATE", "location"),
    LOCATION_ZIP("zip", "ZIP", "location");

    private String header;
    private String column;
    private String resource;

    /**
     * Constructor
     * 
     * @param resource
     */
    private FileUploadEnum(String resource) {
        this.resource = resource;
    }

    /**
     * Constructor 
     * 
     * @param column
     * @param headerInFile
     * @param resource
     */
    private FileUploadEnum(String column, String headerInFile, String resource) {
        this.header = headerInFile;
        this.column = column;
        this.resource = resource;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(String resource) {
        this.resource = resource;
    }


    /**
     * Construct map
     * 
     * @return
     */
    public Map<String, String> prepareColumnHeaderMap() {
        Map<String, String> columnToHeaderMap = new HashMap<String, String>();
        for (FileUploadEnum value : FileUploadEnum.values()) {
            if (value.getResource().equalsIgnoreCase(this.getResource())) {
                if (value.getColumn() == null) {
                    continue; //As it is not a valid entry for map.
                }
                columnToHeaderMap.put(value.getColumn(),  value.getHeader());
            }
        }
        return columnToHeaderMap;
    }




}
