package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_ACTIVE_STATUS;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_COLUMN_VALUE;

import java.util.ArrayList;
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
    public static final String SELECT_COMPANY_QUERY = "SELECT * FROM clients";;
    public static List<String> companyRequiredFields;
    public static List<Object> defaultCompReqFieldValues;
    public static String REQUIRED_FIELD_FOR_LOCATION = "client_id";
    static {
        companyRequiredFields = new ArrayList<String>();
        companyRequiredFields.add("search_help");
        companyRequiredFields.add("client_type");
        companyRequiredFields.add("special_note");
        companyRequiredFields.add("client_since");
        companyRequiredFields.add("t1_is_active");

        defaultCompReqFieldValues = new ArrayList<Object>();
        defaultCompReqFieldValues.add(DEFAULT_COLUMN_VALUE);
        defaultCompReqFieldValues.add(DEFAULT_COLUMN_VALUE);
        defaultCompReqFieldValues.add(DEFAULT_COLUMN_VALUE);
        defaultCompReqFieldValues.add(CommonUtil.getTodayInUTC());
        defaultCompReqFieldValues.add(DEFAULT_ACTIVE_STATUS); //default all clients are active
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
    private static String buildQuery(String insertQueryType, List<String> columns) {
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
        return buildQuery(INSERT_USER, userColumns);
    }


}
