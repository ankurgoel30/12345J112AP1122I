package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.repositories.PrepareStatementBuilder.buildPreparedStatementCreator;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildCompanyInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildLocationInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.defaultCompReqFieldValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;
import com.thinkhr.external.api.model.UserCsvModel;
import com.thinkhr.external.api.services.utils.CommonUtil;

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

        // Saving Company Record
        jdbcTemplate.update(buildPreparedStatementCreator(insertClientSql, companyColumnsValues), keyHolder);

        int clientId = keyHolder.getKey().intValue();
        
        locationColumnValues.add(String.valueOf(clientId));
        locationColumnValues.add(CommonUtil.getTempId());
        
        // Saving Location Record
        jdbcTemplate.update(buildPreparedStatementCreator(insertLocationSql, locationColumnValues));
        
        return clientId;
    }
    
    /**
     * @param userColumns
     * @return
     * @throws SQLException
     */
    public PreparedStatement createdPreparedStatement(String query) throws SQLException {
        
       Connection con = jdbcTemplate.getDataSource().getConnection();

       return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }
    
    public DataSource getDataSource() throws SQLException {
        
        return jdbcTemplate.getDataSource();

     }

    /**
     * Save user's record
     *  
     * @param userColumnsToInsert
     * @param userColumnValues
     */
    public Integer saveUserRecord2(UserCsvModel userCsvModel) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(buildPreparedStatementCreator(userCsvModel.getPreparedStatement(), userCsvModel.getUserColumnValues()), keyHolder);
        
        return keyHolder.getKey().intValue();
    }
    
    /**
     * Save user's record
     *  
     * @param userColumnsToInsert
     * @param userColumnValues
     * @throws SQLException 
     */
    public Integer saveUserRecord(UserCsvModel userCsvModel) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update(buildPreparedStatementCreator(userCsvModel.getPreparedStatement(), userCsvModel.getUserColumnValues()), keyHolder);
        
        for (int i = 0; i < userCsvModel.getUserColumnValues().size(); i++) {
            Object value = userCsvModel.getUserColumnValues().get(i);
            if (value instanceof String) {
                userCsvModel.getPreparedStatement().setString(i + 1, (String) value);
            } else {
                userCsvModel.getPreparedStatement().setObject(i + 1, value);
            }
        }
        
        PreparedStatement ps = userCsvModel.getPreparedStatement();
        ps.executeUpdate();
        ps.getGeneratedKeys();
        
        List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();
        generatedKeys.clear();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys != null) {
            try {
                RowMapperResultSetExtractor<Map<String, Object>> rse =
                        new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1);
                generatedKeys.addAll(rse.extractData(keys));
            }
            finally {
                JdbcUtils.closeResultSet(keys);
            }
        }
        
        return keyHolder.getKey().intValue();
    }

}
