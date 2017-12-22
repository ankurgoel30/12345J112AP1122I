package com.thinkhr.external.api.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnRole;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnFileDataRepository;
import com.thinkhr.external.api.learn.repositories.LearnRoleRepository;
import com.thinkhr.external.api.learn.repositories.LearnUserRepository;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class LearnUserServiceTest {

    @Mock
    private LearnUserRepository learnUserRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    protected LearnRoleRepository learnRoleRepository;

    @Mock
    private ModelConvertor modelConvertor;

    @Mock
    private LearnFileDataRepository learnFileDataRepository;

    @InjectMocks
    private LearnUserService learnService;

    /**
     * Test to verify addLearnUser method when learnUser is given.
     */
    @Test
    public void testAddLearnUser_ForLearnUser() {
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876479");

        when(learnUserRepository.save(learnUser)).thenReturn(learnUser);

        LearnUser actual = learnService.addLearnUser(learnUser);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(learnUser.getCompanyId(), actual.getCompanyId());
        assertEquals(learnUser.getUserName(), actual.getUserName());
        assertEquals(learnUser.getFirstName(), actual.getFirstName());
        assertEquals(learnUser.getLastName(), actual.getLastName());
        assertEquals(learnUser.getPassword(), actual.getPassword());
        assertEquals(learnUser.getEmail(), actual.getEmail());
        assertEquals(learnUser.getPhone1(), actual.getPhone1());
    }

    /**
     * Test to verify addLearnUser method when user is given.
     */
    @Test
    public void testAddLearnUser_ForUser() {
        Company company = ApiTestDataUtil.createCompany();
        User user = ApiTestDataUtil.createUser(1, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "Pepcus");
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876479");
        String roleName = "Test Agaent";

        when(modelConvertor.convert(user)).thenReturn(learnUser);
        when(learnUserRepository.save(learnUser)).thenReturn(learnUser);

        when(companyRepository.findOne(user.getCompanyId())).thenReturn(company);
        when(learnRoleRepository.findFirstByShortName(roleName)).thenReturn(new LearnRole());

        LearnUser actual = learnService.addLearnUser(user);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(user.getFirstName(), actual.getFirstName());
        assertEquals(user.getLastName(), actual.getLastName());
        assertEquals(user.getEmail(), actual.getEmail());
        assertEquals("ajain_inact_null_null", actual.getUserName());
    }

    /**
     * Test to verify updateLearnUser method when learn user is given.
     * 
     */
    @Test
    public void testUpdateLearnUser_ForLearnUser() {
        Long userId = 1L;

        LearnUser user = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "", "ajay.jain@pepcus.com",
                "9009876567");

        when(learnUserRepository.findOne(userId)).thenReturn(user);
        when(learnUserRepository.save(user)).thenReturn(user);

        // Updating user name 
        user.setUserName("Pepcus - Updated");

        LearnUser userUpdated = null;
        try {
            userUpdated = learnService.updateLearnUser(user);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", userUpdated.getUserName());
    }

    /**
     * Test to verify updateLearnUser method when user is given.
     * 
     */
    @Test
    public void testUpdateLearnUser_ForUser() {
        Long userId = 1L;

        User user = ApiTestDataUtil.createUser(10, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "Pepcus");
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876545");

        when(learnUserRepository.findFirstByThrUserId(user.getUserId())).thenReturn(learnUser);

        when(learnUserRepository.findOne(userId)).thenReturn(learnUser);
        when(learnUserRepository.save(learnUser)).thenReturn(learnUser);

        LearnUser userUpdated = null;
        try {
            userUpdated = learnService.updateLearnUser(user);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("ajain_inact_null_null", userUpdated.getUserName());
    }

    /**
     * Test to verify addLearnUserForBulk method
     */
    @Test
    public void testAddLearnUserForBulk() {
        Integer roleId = 1;
        Company company = ApiTestDataUtil.createCompany();
        String roleName = "Test Agaent";
        User user = ApiTestDataUtil.createUser(1, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "Pepcus");
        List<String> learnUserColumnList = ApiTestDataUtil.getLearnUserColumnList();
        List<Object> learnUserColumnValuesList = ApiTestDataUtil.getLearnUserColumnValuesList();

        when(learnFileDataRepository.saveLearnUserRecord(learnUserColumnList, learnUserColumnValuesList, roleId))
                .thenReturn(1);
        when(companyRepository.findOne(user.getCompanyId())).thenReturn(company);
        when(learnRoleRepository.findFirstByShortName(roleName)).thenReturn(new LearnRole());

        Integer learnUserId = learnService.addLearnUserForBulk(user);

        assertNotNull(learnUserId);
    }

}
