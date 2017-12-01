package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.repositories.PrepareStatementBuilder.buildPreparedStatementCreator;
import static com.thinkhr.external.api.repositories.QueryBuilder.DELETE_COMPANY_QUERY;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildCompanyInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildLocationInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.companyDefaultColumnsValuesForNewRecord;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
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

    public void saveCompanyRecord(List<String> companyColumns, List<Object> companyColumnsValues, List<String> locationColumns,
            List<Object> locationColumnValues) {

        String insertClientSql = buildCompanyInsertQuery(companyColumns);

        String insertLocationSql = buildLocationInsertQuery(locationColumns);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        companyColumnsValues.addAll(companyDefaultColumnsValuesForNewRecord());
        jdbcTemplate.update(buildPreparedStatementCreator(insertClientSql.toString(), companyColumnsValues), keyHolder);

        int clientId = keyHolder.getKey().intValue();

        try {
            locationColumnValues.add(String.valueOf(clientId));
            jdbcTemplate.update(buildPreparedStatementCreator(insertLocationSql.toString(), locationColumnValues));
        } catch (Exception ex) {
            //rollback client table  insert if location table insert fails
            jdbcTemplate.update(DELETE_COMPANY_QUERY, clientId);
            throw ex;
        }
    }

    /**
     * TODO: Add implementation
     * 
     * @param userColumnsToInsert
     * @param userColumnValues
     */
    public void saveUserRecord(List<String> userColumnsToInsert,
            List<Object> userColumnValues) {
        // TODO Auto-generated method stub
        
    }

}
