package com.thinkhr.external.api.learn.repositories;

import static com.thinkhr.external.api.repositories.QueryBuilder.SELECT_LEARN_COMPANY_QUERY;
import static com.thinkhr.external.api.repositories.QueryBuilder.SELECT_LEARN_USER_QUERY;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getLearnCompanyColumnValuesList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ApiApplication.class)
public class LearnFileDataRepositoryTest {

    @Autowired
    private LearnFileDataRepository fileRepository;

    @Before
    public void setup() {
        EmbeddedDatabase db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("createLearnTables.sql").build();
        fileRepository.getJdbcTemplate().setDataSource(db);
    }

    /**
     * Test to verify when LearnFileDataRepository.saveLearnCompanyRecord()
     * saves the company record successfully.
     */
    @Test
    public void testSaveLearnCompanyRecord() {
        Integer packageId = 2;
        List<Object> companyValuesList = getLearnCompanyColumnValuesList();

        Integer learnCompanyId = fileRepository.saveLearnCompanyRecord(companyValuesList, packageId);

        List<Map<String, Object>> rows = fileRepository.getJdbcTemplate().queryForList(SELECT_LEARN_COMPANY_QUERY);
        LearnCompany company = new LearnCompany();
        for (Map<String, Object> row : rows) {
            company.setCompanyName((String) row.get("company_name"));
            company.setCompanyId((Integer) row.get("thrclientid"));
            company.setCompanyKey((String) row.get("company_key"));
            company.setCompanyType((String) row.get("company_type"));
            company.setAddress((String) row.get("address"));
            company.setAddress2((String) row.get("address2"));
            company.setCity((String) row.get("city"));
            company.setState((String) row.get("state"));
            company.setZip((String) row.get("zip"));
            company.setBroker((String) row.get("partnerid"));
            company.setPhone((String) row.get("phone"));
            company.setCreatedBy((Long) row.get("createdby"));
        }

        assertNotNull(learnCompanyId);
        assertEquals("Pepcus Software Services", company.getCompanyName());
        assertEquals(234, company.getCompanyId().intValue());
        assertEquals("hkf", company.getCompanyKey());
        assertEquals("IT", company.getCompanyType());
        assertEquals("10 Monroe St.", company.getAddress());
        assertEquals("dummy_address", company.getAddress2());
        assertEquals("Washington", company.getCity());
        assertEquals("DC", company.getState());
        assertEquals("34544", company.getZip());
        assertEquals("111", company.getBroker());
        assertEquals("9009638270", company.getPhone());
        assertEquals(1, company.getCreatedBy().longValue());
    }

    /**
     * Test to verify when LearnFileDataRepository.saveLearnCompanyRecord()
     * throws some DB exception while saving record into DB.
     */
    @Test(expected = DataAccessException.class)
    public void testSaveCompanyRecordForFailure() {
        Integer pkgId = 2;

        // Company List is empty
        List<Object> companyValuesList = new ArrayList<Object>();
    
        fileRepository.saveLearnCompanyRecord(companyValuesList, pkgId);
    }

    /**
     * Test to verify when LearnFileDataRepository.saveLearnUserRecord() saves
     * the user record successfully.
     */
    @Test
    public void testSaveLearnUserRecord() {
        Integer roleId = 1;
        List<String> learnUserColumnList = ApiTestDataUtil.getLearnUserColumnList();
        List<Object> learnUserColumnValuesList = ApiTestDataUtil.getLearnUserColumnValuesList();

        Integer learnUserId = fileRepository.saveLearnUserRecord(learnUserColumnList, learnUserColumnValuesList,
                roleId);
        List<Map<String, Object>> rows = fileRepository.getJdbcTemplate().queryForList(SELECT_LEARN_USER_QUERY);
        LearnUser user = new LearnUser();
        for (Map<String, Object> row : rows) {
            user.setFirstName((String) row.get("firstname"));
            user.setLastName((String) row.get("lastname"));
            user.setUserName((String) row.get("username"));
            user.setPassword((String) row.get("password"));
            user.setEmail((String) row.get("email"));
            user.setPhone1((String) row.get("phone1"));
            user.setThrUserId((Integer) row.get("thrcontactid"));
        }

        assertNotNull(learnUserId);
        assertEquals("Ajay", user.getFirstName());
        assertEquals("Jain", user.getLastName());
        assertEquals("ajay.jain@pepcus.com", user.getEmail());
        assertEquals("ajain", user.getUserName());
        assertEquals("3457893455", user.getPhone1());
        assertEquals("", user.getPassword());
        assertEquals(20, user.getThrUserId().intValue());
    }

    /**
     * Test to verify when LearnFileDataRepository.saveLearnUserRecord() throws
     * some DB exception while saving record into DB.
     */
    @Test(expected = DataAccessException.class)
    public void testSaveLearnUserRecordForFailure() {
        Integer roleId = 1;
        List<String> userList = ApiTestDataUtil.getLearnUserColumnList();

        // User List is empty
        List<Object> userValuesList = new ArrayList<Object>();

        fileRepository.saveLearnUserRecord(userList, userValuesList, roleId);
    }
}
