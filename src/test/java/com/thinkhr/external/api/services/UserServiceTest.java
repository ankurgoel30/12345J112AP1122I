package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getFileRecordForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.sql.DataTruncation;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
import com.thinkhr.external.api.repositories.StandardFieldsRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.response.APIMessageUtil;
import com.thinkhr.external.api.services.crypto.AppEncryptorDecryptor;
import com.thinkhr.external.api.services.upload.FileImportValidator;
import com.thinkhr.external.api.services.utils.FileImportUtil;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to test all the methods of UserService.
 * 
 * 
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { FileImportUtil.class, FileImportValidator.class, APIMessageUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.crypto.*" })
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private MessageResourceHandler resourceHandler2;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private StandardFieldsRepository standardFieldsRepository;

    @Mock
    private AppEncryptorDecryptor encDecyptor;

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private MessageResourceHandler resourceHandler;

    @InjectMocks
    private UserService userService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * To verify getAllUsers method. 
     * 
     */
    @Test
    public void testGetAllUsers(){
        List<User> userList = createUserList();

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_USER_NAME);

        when(userRepository.findAll(null, pageable)).thenReturn(new PageImpl<User>(userList, pageable, userList.size()));

        try {
            List<User> result =  userService.getAllUser(null, null, null, null, null);
            assertEquals(10, result.size());
        } catch (ApplicationException ex) {
            fail("Not expected exception");
        }

    }

    /**
     * To verify getAllCompany method specifically for pageable.
     * 
     */
    @Test
    public void testGetAllToVerifyPageable(){

        userService.getAllUser(null, null, null, null, null);

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_USER_NAME);

        //Verifying that internally pageable arguments is passed to userRepository's findAll method
        verify(userRepository, times(1)).findAll(null, pageable);
    }

    /**
     * To verify getUser method when user exists.
     * 
     */
    @Test
    public void testGetUser() {
        User user = createUser();

        when(userRepository.findOne(user.getUserId())).thenReturn(user);
        User result = userService.getUser(user.getUserId());
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getCompanyName(), result.getCompanyName());
    }

    /**
     * To verify getUser method when user does not exist.
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testGetUserNotExists() {
        Integer userId = 1;
        when(userRepository.findOne(userId)).thenReturn(null);
        User result = userService.getUser(userId);
    }

    /**
     * To verify addUser method
     * 
     */
    @Test
    public void testAddUser(){
        User user = createUser();
        when(userRepository.save(user)).thenReturn(user);
        User result = userService.addUser(user);
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getCompanyName(), result.getCompanyName());
    }

    /**
     * To verify updateUser method
     * 
     */

    @Test
    public void testUpdateUser(){

        User user = createUser();

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findOne(user.getUserId())).thenReturn(user);
        // Updating first name 
        user.setFirstName("Pepcus - Updated");
        User updatedUser = null;
        try {
            updatedUser = userService.updateUser(user);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", updatedUser.getFirstName());
    }

    /**
     * To verify updateUser method when userRepository doesn't find a match for given userId.
     * 
     */

    @Test
    public void testUpdateUserForEntityNotFound(){
        Integer userId = 1;
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");
        when(userRepository.findOne(userId)).thenReturn(null);
        try {
            userService.updateUser(user);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }

    /**
     * To verify deleteUser method
     * 
     */
    @Test
    public void testDeleteUser() {
        Integer userId = 1;
        when(userRepository.findOne(userId)).thenReturn(createUser());
        try {
            userService.deleteUser(userId);
        } catch (ApplicationException e) {
            fail("Should be executed properly without any error");
        }
        //Verifying that internally userRepository's delete method executed
        verify(userRepository, times(1)).softDelete(userId);
    }

    /**
     * To verify deleteUser method throws ApplicationException when internally userRepository.delete method throws exception.
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testDeleteUserForEntityNotFound() {
        int userId = 1 ;
        when(userRepository.findOne(userId)).thenReturn(null);
        userService.deleteUser(userId);
    }
    
    /**
     * Test to verify if user name is null.
     * 
     */
    @Test
    public void testCheckDuplicateForUserNameNull() {
        String record = getFileRecordForUser();
        FileImportResult fileImportResult = new FileImportResult();
        String userName = null;
        
        boolean isDuplicate = userService.checkDuplicate(record, userName, fileImportResult);
        
        assertFalse(isDuplicate);
    }
    
    /**
     * Test to verify if duplicate user name found in DB.
     * 
     */
    @Test
    public void testCheckDuplicateForDuplicateUserName() {
        String record = getFileRecordForUser();
        FileImportResult fileImportResult = new FileImportResult();
        String userName = "ajain";
        User user = createUser(1, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "ThinkHR");
        
        when(userRepository.findByUserName(userName)).thenReturn(user);
        
        try {
            mockStatic(APIMessageUtil.class);

            PowerMockito.doReturn("DUPLICATE_USER_RECORD").when(APIMessageUtil.class,
                    "getMessageFromResourceBundle", resourceHandler, APIErrorCodes.DUPLICATE_USER_RECORD, userName);

        } catch (Exception e) {
            fail("Exception not expected");
        }
        
        boolean isDuplicate = userService.checkDuplicate(record, userName, fileImportResult);
        
        assertEquals(1, fileImportResult.getNumFailedRecords());
        assertTrue(isDuplicate);
    }
    
    /**
     * Test to verify if user name not found in DB.
     * 
     */
    @Test
    public void testCheckDuplicateForNoDuplicateUserName() {
        String record = getFileRecordForUser();
        FileImportResult fileImportResult = new FileImportResult();
        String userName = "ajain";
        User user = createUser(1, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "ThinkHR");
        
        when(userRepository.findByUserName(userName)).thenReturn(null); 
        
        boolean isDuplicate = userService.checkDuplicate(record, userName, fileImportResult);
        
        assertFalse(isDuplicate);
    }

    /**
     * Test to verify if record is saved successfully.
     * 
     */
    @Test
    public void testPopulateAndSaveToDBIfRecordSaved() {
        Integer companyId = 1;
        FileImportResult fileImportResult = new FileImportResult();
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        List<Object> userColumnValues = ApiTestDataUtil
                .getUserColumnValuesList();
        Map<String, String> userColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForUser();

        try {
            PowerMockito.doReturn(userColumnValues).when(FileImportUtil.class,
                    "populateColumnValues", record, userColumnsToHeaderMap,
                    headerIndexMap);

        } catch (Exception e) {
            fail("Exception not expected");
        }

        int expectedSuccessCount = fileImportResult.getNumSuccessRecords() + 1;

        Mockito.doNothing().when(fileDataRepository).saveUserRecord(
                Mockito.anyListOf(String.class),
                Mockito.anyListOf(Object.class));

        userService.populateAndSaveToDB(record, userColumnsToHeaderMap,
                headerIndexMap, fileImportResult, companyId);

        assertEquals(expectedSuccessCount, fileImportResult.getNumSuccessRecords());
    }
    
    /**
     * Test to verify if record is not saved successfully.
     * 
     */
    @Test
    public void testPopulateAndSaveToDBIfRecordNotSaved() {
        Integer companyId = 1;
        FileImportResult fileImportResult = new FileImportResult();
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        List<Object> userColumnValues = ApiTestDataUtil
                .getUserColumnValuesList();
        Map<String, String> userColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForUser();

        try {
            PowerMockito.doReturn(userColumnValues).when(FileImportUtil.class,
                    "populateColumnValues", record, userColumnsToHeaderMap,
                    headerIndexMap);

        } catch (Exception e) {
            fail("Exception not expected");
        }

        RuntimeException ex = new RuntimeException();
        ex.initCause(new DataTruncation(0, true, true, 12, 13));
        Mockito.doThrow(ex).when(fileDataRepository).saveUserRecord(Mockito.any(), Mockito.any());

        userService.populateAndSaveToDB(record, userColumnsToHeaderMap,
                headerIndexMap, fileImportResult, companyId);

        assertEquals(0, fileImportResult.getNumSuccessRecords());
        assertEquals(1, fileImportResult.getNumFailedRecords());
        assertEquals(1, fileImportResult.getFailedRecords().size());
    }
    
    /**
     * Test to verify if method throws ArrayIndexOutOfBoundsException
     * 
     */
    @Test
    public void testPopulateAndSaveToDBIfFieldsMissing() {
        Integer companyId = 1;
        FileImportResult fileImportResult = new FileImportResult();
        String record = "Test Record";

        mockStatic(FileImportUtil.class);

        List<Object> userColumnValues = ApiTestDataUtil
                .getUserColumnValuesList();
        Map<String, String> userColumnsToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForUser();

        ArrayIndexOutOfBoundsException ex = new ArrayIndexOutOfBoundsException("One or more fields Missing");
        try {
            PowerMockito.doThrow(ex).when(FileImportUtil.class, "populateColumnValues", record, userColumnsToHeaderMap, headerIndexMap);
        } catch (Exception e) {
            fail("Exeption not expected");
        }

        userService.populateAndSaveToDB(record, userColumnsToHeaderMap,
                headerIndexMap, fileImportResult, companyId);

        assertEquals(0, fileImportResult.getNumSuccessRecords());
        assertEquals(1, fileImportResult.getNumFailedRecords());
        assertEquals(1, fileImportResult.getFailedRecords().size());
    }
    
    /**
     * Test to verify if no records exist in csv file. 
     * 
     */
    @Test
    public void testProcessRecordsForBlankRecords() {
        List<String> records = ApiTestDataUtil.getBlankCsvRecordsForUser();
        Company broker = ApiTestDataUtil.createCompany();
        String resource = "USER";
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class, "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        Map<String, String> columnToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(columnToHeaderMap).when(userServiceSpy)
                .appendRequiredAndCustomHeaderMap(companyId, resource);

        FileImportResult fileImportResult = userServiceSpy.processRecords(records, broker, resource);

        assertEquals(3, fileImportResult.getNumBlankRecords());
    }
    
    /**
     * Test to verify if some required fields are missing in csv file.
     * 
     */
    @Test
    public void testProcessRecordsForValidateRequiredFalse() {
        List<String> records = ApiTestDataUtil.getCsvRecordsForUserForMissingFields();
        String record = "Ajay,Jain";
        Company broker = ApiTestDataUtil.createCompany();
        FileImportResult fileImportResult = new FileImportResult();
        Map<String, Integer> headerIndexMap = ApiTestDataUtil
                .getHeaderIndexMapForUser();
        String resource = "USER";
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class, "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        Map<String, String> columnToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(columnToHeaderMap).when(userServiceSpy)
                .appendRequiredAndCustomHeaderMap(companyId, resource);

        List<String> list = ApiTestDataUtil.getStdFieldsForUser();

        when(userService.getRequiredHeadersFromStdFields("CONTACT"))
                .thenReturn(list);

        boolean isValidate = FileImportValidator.validateRequired(record, list,
                headerIndexMap,
                fileImportResult, resourceHandler2);

        assertEquals(1, fileImportResult.getNumFailedRecords());
        assertFalse(isValidate);
    }

    /**
     * Test to verify if existing email in csv file is in-valid
     * 
     */
    @Test
    public void testProcessRecordsForInValidEmail() {
        String record = "Ajay,Jain,ThinkHR,ajay.jain,ajain,82374893423,20";
        String email = "ajay.jain";
        Company broker = ApiTestDataUtil.createCompany();
        FileImportResult fileImportResult = new FileImportResult();
        String resource = "USER";
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class,
                    "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        Map<String, String> columnToHeaderMap = ApiTestDataUtil
                .getColumnsToHeadersMapForUser();
        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(columnToHeaderMap).when(userServiceSpy)
                .appendRequiredAndCustomHeaderMap(companyId, resource);

        List<String> list = ApiTestDataUtil.getStdFieldsForUser();

        when(userService.getRequiredHeadersFromStdFields("CONTACT"))
                .thenReturn(list);

        try {
            mockStatic(FileImportValidator.class);

            PowerMockito.doReturn(true).when(FileImportValidator.class,
                    "validateRequired", Mockito.any(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        boolean isValidate = FileImportValidator.validateEmail(record, email,
                fileImportResult, resourceHandler2);

        assertFalse(isValidate);
    }

}
