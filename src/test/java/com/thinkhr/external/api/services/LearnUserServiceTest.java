package com.thinkhr.external.api.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnRole;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;
import com.thinkhr.external.api.learn.repositories.LearnFileDataRepository;
import com.thinkhr.external.api.learn.repositories.LearnRoleRepository;
import com.thinkhr.external.api.learn.repositories.LearnUserRepository;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class LearnUserServiceTest {

    @Mock
    private LearnUserRepository learnUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    protected LearnRoleRepository learnRoleRepository;

    @Mock
    private ModelConvertor modelConvertor;

    @Mock
    private LearnFileDataRepository learnFileDataRepository;

    @Mock
    private LearnCompanyRepository learnCompanyRepository;

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

    /**
     * Test to verify getRoleName method.
     * 
     */
    @Test
    public void testGetRoleName_ForBroker() {
        User user = ApiTestDataUtil.createUser();
        Company company = ApiTestDataUtil.createCompany();
        company.setCompanyId(10);
        company.setBroker(10);

        when(companyRepository.findOne(user.getCompanyId())).thenReturn(company);

        String roleName = learnService.getRoleName(user);

        assertEquals(ApplicationConstants.BROKER_ROLE, roleName);
    }

    /**
     * Test to verify getRoleName method.
     * 
     */
    @Test
    public void testGetRoleName_ForStudent() {
        User user = ApiTestDataUtil.createUser();
        Company company = ApiTestDataUtil.createCompany();
        company.setCompanyId(1);
        company.setBroker(10);

        when(companyRepository.findOne(user.getCompanyId())).thenReturn(company);

        String roleName = learnService.getRoleName(user);

        assertEquals(ApplicationConstants.STUDENT_ROLE, roleName);
    }

    /**
     * Test to verify addUserRoleAssignment method.
     */
    @Test
    public void testAddUserRoleAssignment() {
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876543");
        String roleName = "Test Agent";
        LearnRole role = ApiTestDataUtil.createLearnRole(1, "Test Agent");

        when(learnRoleRepository.findFirstByShortName(roleName)).thenReturn(role);

        LearnUserRoleAssignment userRoleAssignment = learnService.addUserRoleAssignment(learnUser, roleName);

        assertEquals(roleName, userRoleAssignment.getLearnRole().getName());
        assertEquals(learnUser.getUserName(), userRoleAssignment.getLearnUser().getUserName());
    }

    /**
     * Test to verify addUserRoleAssignment method.
     */
    @Test
    public void testAddUserRoleAssignment_ForLearnUserNull() {
        LearnUser learnUser = null;
        String roleName = "Test Agent";
        LearnRole role = ApiTestDataUtil.createLearnRole(1, "Test Agent");

        when(learnRoleRepository.findFirstByShortName(roleName)).thenReturn(role);

        LearnUserRoleAssignment userRoleAssignment = learnService.addUserRoleAssignment(learnUser, roleName);

        assertNull(userRoleAssignment);
    }

    /**
     * Test to verify deactivateAllLearnUsers method.
     */
    @Test
    public void testDeactivateAllLearnUsers() {
        Company company = ApiTestDataUtil.createCompany();
        List<User> userList = ApiTestDataUtil.createUserList();
        LearnUser learnUser = new LearnUser();

        when(userRepository.findByCompanyId(company.getCompanyId())).thenReturn(userList);

        for (User user : userList) {
            when(learnUserRepository.findFirstByThrUserId(user.getUserId())).thenReturn(learnUser);
            when(learnUserRepository.save(learnUser)).thenReturn(learnUser);
        }

        learnService.deactivateAllLearnUsers(company);
    }

    /**
     * Test to verify activateAllLearnUsers method.
     */
    @Test
    public void testActivateAllLearnUsers() {
        Company company = ApiTestDataUtil.createCompany();
        List<User> userList = ApiTestDataUtil.createUserList();
        LearnUser learnUser = new LearnUser();

        when(userRepository.findByCompanyId(company.getCompanyId())).thenReturn(userList);

        for (User user : userList) {
            when(learnUserRepository.findFirstByThrUserId(user.getUserId())).thenReturn(learnUser);
            when(learnUserRepository.save(learnUser)).thenReturn(learnUser);
        }

        learnService.activateAllLearnUsers(company);
    }

    /**
     * Test to verify getLearnUserNameByRoleId method.
     * 
     */
    @Test
    public void testGetLearnUserNameByRoleId_ForInactive() {
        User user = ApiTestDataUtil.createUser();
        user.setRoleId(-1);

        String learnUserName = LearnUserService.getLearnUserNameByRoleId(user);

        assertEquals("sbhawsar_inact_null_null", learnUserName);
    }

    /**
     * Test to verify getLearnUserNameByRoleId method.
     * 
     */
    @Test
    public void testGetLearnUserNameByRoleId_ForActive() {
        User user = ApiTestDataUtil.createUser();
        user.setRoleId(1);

        String learnUserName = LearnUserService.getLearnUserNameByRoleId(user);

        assertEquals("sbhawsar", learnUserName);
    }

    /**
     * Test to verify generateUserNameForInactive method.
     */
    @Test
    public void testGenerateUserNameForInactive_ForBrokerIdNull() {
        String userName = "ajain";
        Integer companyId = 2;
        Integer brokerId = null;

        String inactiveName = LearnUserService.generateUserNameForInactive(userName, companyId, brokerId);

        assertEquals("ajain_inact_2_null", inactiveName);
    }

    /**
     * Test to verify generateUserNameForInactive method.
     */
    @Test
    public void testGenerateUserNameForInactive_ForCompanyIdNull() {
        String userName = "ajain";
        Integer companyId = null;
        Integer brokerId = 1;

        String inactiveName = LearnUserService.generateUserNameForInactive(userName, companyId, brokerId);

        assertEquals("ajain_inact_null_1", inactiveName);
    }

}
