package com.thinkhr.external.api.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class FileDataRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    /**
     * Saves company & location records in database
     * 
     * @param companyColumns
     * @param companyColumnsValues
     * @param locationColumns
     * @param locationColumnValues
     */
  
    public void saveCompanyRecord(String[] companyColumns, Object[] companyColumnsValues, String[] locationColumns,
            Object[] locationColumnValues) {
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

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(insertClientSql.toString(), Statement.RETURN_GENERATED_KEYS);
                int i;
                for (i = 0; i < companyColumnsValues.length; i++) {
                    statement.setString(i + 1, (String) companyColumnsValues[i]);
                }
                statement.setString(++i, "");
                statement.setString(++i, "");
                statement.setString(++i, "");
                statement.setDate(++i, new java.sql.Date(System.currentTimeMillis()));
                statement.setInt(++i,1); //default all clients are active
                return statement;
            }
        }, keyHolder);

        int clientId = keyHolder.getKey().intValue();

        try {
            jdbcTemplate.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(insertLocationSql.toString());
                    int i;
                    for (i = 0; i < locationColumnValues.length; i++) {
                        statement.setString(i + 1, (String) locationColumnValues[i]);
                    }
                    statement.setInt(++i, clientId);
                    return statement;
                }
            });
        } catch (Exception ex) {
            //rollback client table  insert if location table insert fails
            String deleteSql = "Delete from clients where clientId=?";
            jdbcTemplate.update(deleteSql, clientId);
            throw ex;
        }
    }

    
    /**
     * Returns a map of custom fields for given broker
     * @param brokerId broker id
     * @return
     */
    public Map<String, String> getCustomFields(int brokerId) {
        String customFieldsLookupQuery = "Select customFieldDisplayLabel,customFieldColumnName from app_throne_custom_fields  where companyId = ?";

        return jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(customFieldsLookupQuery);
                statement.setInt(1, brokerId);
                return statement;
            }
        }, new ResultSetExtractor<Map<String, String>>() {
            @Override
            public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, String> customFieldsMap = new HashMap<String, String>();
                while (rs.next()) {
                    String key = rs.getString(1);
                    String value = rs.getString(2);
                    customFieldsMap.put(key, value);
                }
                return customFieldsMap;
            }
        });
    }
}
