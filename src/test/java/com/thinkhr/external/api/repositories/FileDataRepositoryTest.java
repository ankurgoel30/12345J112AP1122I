package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCompanyColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getCompanyColumnValuesList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLocationColumnList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLocationsColumnValuesList;

import java.util.ArrayList;
import java.util.List;

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
    
    @Test
    public void testSaveCompanyRecord() {
        List<String> columnList = getCompanyColumnList();
        List<Object> columnValuesList = getCompanyColumnValuesList();
        List<String> locationList = getLocationColumnList();
        List<Object> locationValuesList = getLocationsColumnValuesList();
        
        fileRepository.saveCompanyRecord(columnList, columnValuesList, locationList, locationValuesList);
        
        /*List<Company> companyList = fileRepository.findAll();
        // TODO
        assertEquals(1, companyList.size());*/
        
    }
    
    @Test( expected = DataAccessException.class)
    public void testSaveCompanyRecordForFailure() {
        List<String> columnList = getCompanyColumnList();

        // @throws DataAccessException
        List<Object> columnValuesList = new ArrayList<Object>();
        List<String> locationList = getLocationColumnList();
        List<Object> locationValuesList = getLocationsColumnValuesList();
        
        fileRepository.saveCompanyRecord(columnList, columnValuesList, locationList, locationValuesList); 
    }
    
}
