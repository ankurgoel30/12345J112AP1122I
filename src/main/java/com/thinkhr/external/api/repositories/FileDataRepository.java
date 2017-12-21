package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.repositories.PrepareStatementBuilder.buildPreparedStatementCreator;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildCompanyInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildLocationInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildUserInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.defaultCompReqFieldValues;
import static com.thinkhr.external.api.repositories.QueryBuilder.defaultUserReqFieldValues;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;

@Repository
@Data
public class FileDataRepository {

    @Autowired
    @Qualifier("portalJdbcTemplate")
    JdbcTemplate jdbcTemplate;
    
    /**
     * Saves company & location records in database
     * 
     * @param companyColumns
     * @param companyColumnsValues
     * @param locationColumns
     * @param locationColumnValues
     */

    @Transactional
    public Integer saveCompanyRecord(List<String> companyColumns, List<Object> companyColumnsValues, List<String> locationColumns,
            List<Object> locationColumnValues) {

        String insertClientSql = buildCompanyInsertQuery(companyColumns);

        String insertLocationSql = buildLocationInsertQuery(locationColumns);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        companyColumnsValues.addAll(defaultCompReqFieldValues);

        jdbcTemplate.update(buildPreparedStatementCreator(insertClientSql, companyColumnsValues), keyHolder);

        int clientId = keyHolder.getKey().intValue();
        
        locationColumnValues.add(String.valueOf(clientId));
        jdbcTemplate.update(buildPreparedStatementCreator(insertLocationSql, locationColumnValues));
        return clientId;
    }

    /**
     * Save user's record
     *  
     * @param userColumnsToInsert
     * @param userColumnValues
     */
    public Integer saveUserRecord(List<String> userColumns,
            List<Object> userColumnValues) {

        String insertUserSql = buildUserInsertQuery(userColumns);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        userColumnValues.addAll(defaultUserReqFieldValues);
        jdbcTemplate.update(buildPreparedStatementCreator(insertUserSql, userColumnValues), keyHolder);
        
        return keyHolder.getKey().intValue();
    }

}
