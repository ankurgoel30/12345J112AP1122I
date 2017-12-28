package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCompanyColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLocationColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.testQueryForCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.testQueryForLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
public class QueryBuilderTest {

    /**
     * Test to verify QueryBuilder.buildCompanyInsertQuery() when the test succeeds.
     * 
     */
    @Test
    public void testBuildCompanyInsertQuery() {
        String insertQuery = QueryBuilder.buildCompanyInsertQuery(getCompanyColumnList());
        List<String> allColumns = getCompanyColumnList();
        allColumns.addAll(QueryBuilder.companyRequiredFields);
        assertNotNull(insertQuery);
        assertEquals(16, allColumns.size());
        assertEquals(testQueryForCompany(), insertQuery);
        assertNotEquals(testQueryForLocation(), insertQuery); 
    }

    /**
     * Test to verify QueryBuilder.buildCompanyInsertQuery() when the expected and actual strings are not same.
     * 
     */
    @Test
    public void testBuildCompanyInsertQueryNotSame() {
        //Not expect result for empty list
        List<String> emptyList = new ArrayList<String>(); 
        assertNotEquals(testQueryForCompany(), QueryBuilder.buildCompanyInsertQuery(emptyList));
    }

    /**
     * Test to verify QueryBuilder.buildLocationInsertQuery() when the test succeeds.
     * 
     */
    @Test
    public void testBuildLocationInsertQuery() {
        String insertQuery = QueryBuilder.buildLocationInsertQuery(getLocationColumnList());
        List<String> allColumns = getLocationColumnList();
        allColumns.addAll(QueryBuilder.locationRequiredFields);
        assertNotNull(insertQuery);
        assertEquals(7, allColumns.size());
        assertNotEquals(testQueryForCompany(), insertQuery);
        assertEquals(testQueryForLocation(), insertQuery);
    }

    /**
     * Test to verify QueryBuilder.buildLocationInsertQuery() when the expected and actual strings are not same.
     * 
     */
    @Test
    public void testBuildLocationInsertQueryNotSame() {
        //Not expect result for empty list
        List<String> emptyList = new ArrayList<String>();
        assertNotEquals(testQueryForLocation(), QueryBuilder.buildLocationInsertQuery(emptyList));
    }

}
