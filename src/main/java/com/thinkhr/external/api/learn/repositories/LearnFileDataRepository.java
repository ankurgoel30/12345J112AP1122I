package com.thinkhr.external.api.learn.repositories;

import static com.thinkhr.external.api.repositories.PrepareStatementBuilder.buildPreparedStatementCreator;
import static com.thinkhr.external.api.repositories.QueryBuilder.INSERT_LEARN_COMPANY;
import static com.thinkhr.external.api.repositories.QueryBuilder.INSERT_LEARN_PKG_COMPANY;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildLearnUserInsertQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.buildQuery;
import static com.thinkhr.external.api.repositories.QueryBuilder.defaultUserReqFieldValues;
import static com.thinkhr.external.api.repositories.QueryBuilder.learnCompanyFields;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;

@Repository
@Data
public class LearnFileDataRepository {

    @Autowired
    @Qualifier("learnJdbcTemplate")
    JdbcTemplate jdbcTemplate;
    
    @Value("${com.thinkhr.external.api.learn.default.package}")
    protected String defaultCompanyPackage;

    
    /**
     * To create an instance of learn company
     * 
     * @param companyColumnsValues
     * @param pkgId
     * @return
     */
    @Transactional
    public Integer saveLearnCompanyRecord(List<Object> companyColumnsValues, Integer pkgId) {

        String insertCompanySql = buildQuery(INSERT_LEARN_COMPANY, learnCompanyFields);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(buildPreparedStatementCreator(insertCompanySql, companyColumnsValues), keyHolder);

        Integer learnCompanyId =  keyHolder.getKey().intValue();
        
        jdbcTemplate.update(buildPreparedStatementCreator(INSERT_LEARN_PKG_COMPANY, Arrays.asList(pkgId, learnCompanyId)));

        return learnCompanyId;

    }
    
    /**
     * Save learnuser's record 
     *  
     * @param userColumnsToInsert
     * @param userColumnValues
     */
    @Transactional
    public Integer saveLearnUserRecord(List<String> userColumns, List<Object> userColumnValues) {

        String insertLearnUserSql = buildLearnUserInsertQuery(userColumns);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(buildPreparedStatementCreator(insertLearnUserSql, userColumnValues), keyHolder);
        
        return keyHolder.getKey().intValue();
    }
}
