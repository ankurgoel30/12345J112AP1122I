package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.repositories.QueryBuilder.SELECT_COMPANY_QUERY;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCompanyColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCompanyColumnValuesList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLocationColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLocationsColumnValuesList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ApiApplication.class)
public class FileDataRepositoryTest {
    
    @Autowired
    private FileDataRepository fileRepository;
    
    @Before
    public void setup() {
        EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("createTables.sql")
                .build();
        fileRepository.getJdbcTemplate().setDataSource(db);
    }
    
    /**
     * Test to verify when FileDataRepository.saveCompanyRecord() saves the
     * company record successfully.
     */
    @Test
    public void testSaveCompanyRecord() {
        List<String> companyList = getCompanyColumnList();
        List<Object> companyValuesList = getCompanyColumnValuesList();
        List<String> locationList = getLocationColumnList();
        List<Object> locationValuesList = getLocationsColumnValuesList();
        
        fileRepository.saveCompanyRecord(companyList, companyValuesList, locationList, locationValuesList);
        
        List<Map<String, Object>> rows = fileRepository.getJdbcTemplate().queryForList(SELECT_COMPANY_QUERY);
        Company company = new Company();
        for (Map<String, Object> row : rows) {
            company.setCompanyName((String) row.get("client_name"));
            company.setDisplayName((String) row.get("display_name"));
            company.setCompanyPhone((String) row.get("client_phone"));
            company.setIndustry((String) row.get("industry"));
            company.setCompanySize((String) row.get("companySize"));
            company.setProducer((String) row.get("producer"));
            company.setCustom1((String) row.get("custom1"));
            company.setCustom2((String) row.get("custom2"));
            company.setCustom3((String) row.get("custom3"));
            company.setCustom4((String) row.get("custom4"));
        }

        assertEquals("Pepcus Software Services", company.getCompanyName());
        assertEquals("Pepcus", company.getDisplayName());
        assertEquals("3457893455", company.getCompanyPhone());
        assertEquals("IT", company.getIndustry());
        assertEquals("20", company.getCompanySize());
        assertEquals("AJain", company.getProducer());
        assertEquals("dummy_business", company.getCustom1());
        assertEquals("dummy_branch", company.getCustom2());
        assertEquals("dummy_client", company.getCustom3());
        assertEquals("dummy_client_type", company.getCustom4());
    }
    
    /**
     * Test to verify when FileDataRepository.saveCompanyRecord() throws some DB
     * exception while saving record into DB.
     */
    @Test(expected = DataAccessException.class)
    public void testSaveCompanyRecordForFailure() {
        List<String> companyList = getCompanyColumnList();

        // Company List is empty
        List<Object> companyValuesList = new ArrayList<Object>();
        List<String> locationList = getLocationColumnList();
        List<Object> locationValuesList = getLocationsColumnValuesList();
        
        fileRepository.saveCompanyRecord(companyList, companyValuesList, locationList, locationValuesList);
    }
    
}
