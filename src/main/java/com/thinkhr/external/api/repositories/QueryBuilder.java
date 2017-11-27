package com.thinkhr.external.api.repositories;

import org.apache.commons.lang.StringUtils;

/**
 * Query build to build queries
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-27
 *
 */
public class QueryBuilder {

    /**
     * 
     * @param locationColumns
     * @return
     */
    public static String buildLocationInsertQuery(String[] locationColumns) {
        //INSERT INTO locations(address,address2,city,state,zip,client_id) values(?,?,?,?,?,?);
        StringBuffer insertLocationSql = new StringBuffer();
        insertLocationSql.append("INSERT INTO locations(");
        insertLocationSql.append(StringUtils.join(locationColumns, ","));
        insertLocationSql.append(",client_id");
        insertLocationSql.append(")");
        insertLocationSql.append(" Values(");
        for (int i = 0; i < locationColumns.length; i++) {
            insertLocationSql.append(" ?,");
        }
        insertLocationSql.append("?)");
        return insertLocationSql.toString();
    }

    /**
     * @param companyColumns
     * @return
     */
    public static String buildCompanyInsertQuery(String[] companyColumns) {

        // INSERT INTO clients(client_name,display_name, client_phone,industry,companysize,producer,custom1,custom2,custom3,custom4,
        // search_help,client_type,client_since,special_note) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)

        StringBuffer insertClientSql = new StringBuffer();
        insertClientSql.append("INSERT into clients(");
        insertClientSql.append(StringUtils.join(companyColumns, ","));
        insertClientSql.append(",search_help,client_type,special_note,client_since, t1_is_active)");
        insertClientSql.append(" Values(");
        for (int i = 0; i < companyColumns.length; i++) {
            insertClientSql.append(" ?,");
        }
        insertClientSql.append("?,?,?,?,?)"); //to set other defaults
        return insertClientSql.toString();
    }

    /**
     * @return
     */
    public static String buildDeleteQuery() {
        return "Delete from clients where clientId=?";
    }

}
