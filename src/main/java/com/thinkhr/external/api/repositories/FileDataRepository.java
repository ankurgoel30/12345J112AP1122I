package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.repositories.PrepareStatementBuilder.buildPreparedStatement;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildCompanyInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildLocationInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildDeleteQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
        
        String insertClientSql = buildCompanyInsertQuery(companyColumns);

        String insertLocationSql = buildLocationInsertQuery(
                locationColumns);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(buildPreparedStatement(insertClientSql.toString(), companyColumnsValues), keyHolder);

        int clientId = keyHolder.getKey().intValue();

        try {
            jdbcTemplate.update(buildPreparedStatement(insertLocationSql.toString(), locationColumnValues));
        } catch (Exception ex) {
            //rollback client table  insert if location table insert fails
            jdbcTemplate.update(buildDeleteQuery(), clientId);
            throw ex;
        }
    }

}
