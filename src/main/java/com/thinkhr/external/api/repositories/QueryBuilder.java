package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_ACTIVE_STATUS;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_COLUMN_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.services.utils.CommonUtil;

/**
 * Query build to build queries
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-27
 *
 */
public class QueryBuilder {

    private static final String INSERT_COMPANY = "INSERT INTO clients";
    private static final String INSERT_LOCATION = "INSERT INTO locations";
    private static final String INSERT_USER = "INSERT INTO CONTACTS";
    private static final String VALUES = "VALUES";
    private static final String START_BRACES = "(";
    private static final String END_BRACES = ") ";
    public static final String DELETE_COMPANY_QUERY = "DELETE FROM clients WHERE clientId=?";
    public static final String SELECT_PORTAL_COMPANY_QUERY = "SELECT * FROM clients";
    public static final String SELECT_PORTAL_USER_QUERY = "SELECT * FROM contacts";
    public static final String SELECT_LEARN_USER_QUERY = "SELECT * FROM mdl_user";
    public static final String SELECT_LEARN_COMPANY_QUERY = "SELECT * FROM mdl_company";
    public static final String INSERT_LEARN_COMPANY = "INSERT INTO MDL_COMPANY ";
    public static final String INSERT_LEARN_PKG_COMPANY = "INSERT INTO MDL_PACKAGE_COMPANY(packageid, companyid) VALUES (?, ?)";
    public static final String SELECT_LEARN_PACAKGE_COMPANY_QUERY = "SELECT * FROM MDL_PACKAGE_COMPANY";
    private static final String INSERT_LEARN_USER = "INSERT INTO MDL_USER";
    public static final String INSERT_LEARN_USER_ROLE = "INSERT INTO MDL_ROLE_ASSIGNMENTS(roleid,timemodified,contextid,component,modifierid,itemid,sortorder,userid) Values (?,?,?,?,?,?,?,?)";

    public static List<String> companyRequiredFields;
    public static List<Object> defaultCompReqFieldValues;

    public static List<String> userRequiredFields;
    public static List<Object> defaultUserReqFieldValues;
    public static List<String> learnCompanyFields;
    public static String REQUIRED_FIELD_FOR_LOCATION = "client_id";
    static {
        companyRequiredFields = new ArrayList<String>(Arrays.asList("search_help", 
                                                                    "client_type", 
                                                                    "special_note", 
                                                                    "client_since", 
                                                                    "t1_is_active"));

        defaultCompReqFieldValues = new ArrayList<Object>(Arrays.asList
                                                           (DEFAULT_COLUMN_VALUE, 
                                                            DEFAULT_COLUMN_VALUE, 
                                                            DEFAULT_COLUMN_VALUE, 
                                                            CommonUtil.getTodayInUTC(), 
                                                            DEFAULT_ACTIVE_STATUS)); //default all clients are active

        userRequiredFields = new ArrayList<String>(Arrays.asList("search_help", 
                                                                 "mkdate", 
                                                                  "codevalid", 
                                                                  "update_password", 
                                                                  "blockedaccount"));

        defaultUserReqFieldValues =  new ArrayList<Object>(Arrays.asList (DEFAULT_COLUMN_VALUE, 
                                                                          DEFAULT_COLUMN_VALUE, 
                                                                          DEFAULT_COLUMN_VALUE, 
                                                                          DEFAULT_COLUMN_VALUE, 
                                                                          new Integer(0)));
        learnCompanyFields = new ArrayList<String>(Arrays.asList(
                "thrclientid", "company_name", "company_type","company_key", 
                "address", "address2", "city", "state", "zip",
                "partnerid", "phone", "createdby", "timecreated", "timemodified"));

    }
    /**
     *   //INSERT INTO locations(address,address2,city,state,zip,client_id) values(?,?,?,?,?,?);
     * 
     * @param locationColumns
     * @return
     */
    public static String buildLocationInsertQuery(List<String> locationColumns) {
        locationColumns.add(REQUIRED_FIELD_FOR_LOCATION);
        return buildQuery(INSERT_LOCATION, locationColumns);
    }

    /**
     * Build query
     * 
     * @param locationColumns
     * @return
     */
    public static String buildQuery(String insertQueryType, List<String> columns) {
        StringBuffer insertLocationSql = new StringBuffer();
        insertLocationSql.append(insertQueryType)
                .append(START_BRACES).append(StringUtils.join(columns, COMMA_SEPARATOR))
        .append(END_BRACES)
        .append(VALUES)
                .append(START_BRACES).append(getQueryParaSpecifiers(columns.size()))
        .append(END_BRACES);
        return insertLocationSql.toString();
    }

    /**
     *    INSERT INTO clients(client_name,display_name, client_phone,industry,companySize,producer,custom1,custom2,custom3,custom4,
     *    search_help,client_type,client_since,special_note) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
     *
     * @param companyColumns
     * @return
     */
    public static String buildCompanyInsertQuery(List<String> companyColumns) {
        companyColumns.addAll(companyRequiredFields);
        return buildQuery(INSERT_COMPANY, companyColumns);
    }
    
    /**
     * Generate string having query parameters for given count
     * 
     * @param locationColumns
     * @return
     */
    public static String getQueryParaSpecifiers(Integer count) {
        if (count == 0) {
            return "";
        }
        StringBuffer params = new StringBuffer();
        for (int i = 0; i < count; i++) {
            if ( i > 0) {
                params.append(ApplicationConstants.COMMA_SEPARATOR);
            }
            params.append(ApplicationConstants.QUERY_SEPARATOR);
        }
        
        return params.toString();
    }
   
    
    /**
     * @param userColumns
     * @return
     */
    public static String buildUserInsertQuery(List<String> userColumns) {
        userColumns.addAll(userRequiredFields);
        return buildQuery(INSERT_USER, userColumns);
    }
    
    /**
     * Builds insert query for inserting records into mdl_user table for given list of columns
     * 
     * @param userColumns
     * @return
     */
    public static String buildLearnUserInsertQuery(List<String> userColumns) {
        return buildQuery(INSERT_LEARN_USER, userColumns);
    }

}
