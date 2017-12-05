package com.thinkhr.external.api;

/**
 * Class to keep all the constants used by application
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
public class ApplicationConstants {

    //GENERIC CONSTANTS
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 50;
    public static final String DESENDING = "-";
    public static final String ASCENDING = "+";
    public static final String DEFAULT_SORT_BY_COMPANY_NAME = "+companyName";
    public static final String DEFAULT_SORT_BY_USER_NAME = "+userName";
    public static final String SUCCESS_DELETED = "SUCCESSFULLY_DELETED";
    public static final String TOTAL_RECORDS = "totalRecords";
    public static final String LIMIT_PARAM = "limit";
    public static final String OFFSET_PARAM = "offset";
    public static final String SORT_PARAM = "sort";
    public static final String VALID_FORMAT_YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";
    public static final String VALID_FILE_EXTENSION_IMPORT = "csv";
    public static final String[] REQUIRED_HEADERS_COMPANY_CSV_IMPORT = { "CLIENT_NAME", "DISPLAY_NAME", "PHONE", "ADDRESS", "ADDRESS2",
            "CITY", "STATE", "ZIP", "INDUSTRY", "COMPANY_SIZE", "PRODUCER" };
    
    public static final String[] REQUIRED_HEADERS_USER_CSV_IMPORT = { "FIRST_NAME", "LAST_NAME", "USER_NAME", "EMAIL", "CLIENT_NAME",
            "PHONE", "BUSINESS_ID" };
    
    public static final String COMPANY = "COMPANY";
    public static final String USER = "USER";
    public static final String CONTACT = "CONTACT";
    
    public static final int MAX_RECORDS_COMPANY_CSV_IMPORT = 3500;
    public static final int MAX_RECORDS_USER_CSV_IMPORT = 500;
    public static final String SPACE = " ";
    public static final String DEFAULT_BROKERID_FOR_FILE_IMPORT = "187624";
    public static final String FILE_IMPORT_RESULT_MSG = "FILE_IMPORT_RESULT";

    public static final String COMMA_SEPARATOR = ",";
    public static final String QUERY_SEPARATOR = "?";
    public static final String FAILED_COLUMN_TO_IMPORT = "FAILURE_REASON";

    //Paychex has special treatment for determining duplicate records
    public static final Integer SPECIAL_CASE_BROKER1 = 187624;
    public static final Integer SPECIAL_CASE_BROKER2 = 205111;
    public static final String NEW_LINE = "\n";
    
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"  
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";  
    public static final String DEFAULT_ACTIVE_STATUS = "1";
    public static final String DEFAULT_COLUMN_VALUE = "";
    
    public static final String AES_PKC_PADDING = "AES/CBC/PKCS5PADDING";
    public static final String AES_ALGO = "AES";
    
    public static final String BLOWFISH_ALGO = "Blowfish";
    public static final String BLOWFISH_PKC_PADDING = "Blowfish/CBC/PKCS5PADDING";
    
    public static final String UTF8 = "UTF-8";
    public static final String COMPANY_CUSTOM_COLUMN_PREFIX = "custom";
    public static final String USER_CUSTOM_COLUMN_PREFIX = "t1_customfield";
    public static final String USER_COLUMN_CLIENT_ID = "client_id";
    public static final String USER_COLUMN_PASSWORD = "password_enc";
    public static final String DEFAULT_PASSWORD = "";
    



}
