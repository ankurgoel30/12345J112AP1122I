package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.sql.DataTruncation;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.db.entities.StandardFields;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnRole;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.learn.repositories.LearnRoleRepository;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
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

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private LearnRoleRepository roleRepository;

    @Mock
    private CustomFieldsRepository customFieldsRepository;

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

    @Mock
    private LearnUserService learnUserService;

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
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009687639");

        when(roleRepository.findOne(user.getRoleId())).thenReturn(new LearnRole());
        when(userRepository.save(user)).thenReturn(user);
        when(learnUserService.addLearnUser(user)).thenReturn(learnUser);

        User result = userService.addUser(user,1);
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
    public void testUpdateUser() throws Exception {

        User user = createUser();
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(1L, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009687639");

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findOne(user.getUserId())).thenReturn(user);

        when(learnUserService.updateLearnUser(user)).thenReturn(learnUser);

        // Updating first name 
        user.setFirstName("Pepcus - Updated");

        String userJson = ApiTestDataUtil.getJsonString(user);
        User updatedUser = null;
        try {
            updatedUser = userService.updateUser(user.getUserId(), userJson,1);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", updatedUser.getFirstName());
    }

    /**
     * To verify updateUser method when userRepository doesn't find a match for given userId.
     * @throws Exception 
     * 
     */

    @Test
    public void testUpdateUserForEntityNotFound() throws Exception {
        Integer userId = 1;
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");
        when(userRepository.findOne(userId)).thenReturn(null);
        String userJson = ApiTestDataUtil.getJsonString(user);
        try {
            userService.updateUser(user.getUserId(), userJson,1);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }

    /**
     * To verify updateUser method throws exception when trying to update 
     * a NotNull field with null value
     * 
     */

    @Test
    public void testUpdateUser_UpdateNotNullFieldWithNull() throws Exception {

        User user = createUser();

        when(userRepository.findOne(user.getUserId())).thenReturn(user);

        // Updating notNull field firstName with null
        String userJson = "{\"firstName\": null}";

        User updatedUser = null;
        try {
            updatedUser = userService.updateUser(user.getUserId(), userJson, 1);
            fail("Expecting Exception");
        } catch (Exception ex) {
            assertTrue(ex instanceof ConstraintViolationException);
            ConstraintViolationException cv = (ConstraintViolationException) ex;
            assertEquals(1, cv.getConstraintViolations().size());
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
        String userName = null;
        boolean isDuplicate = userService.checkDuplicate(userName);
        
        assertFalse(isDuplicate);
    }
    
    /**
     * Test to verify if duplicate user name found in DB.
     * 
     */
    @Test
    public void testCheckDuplicateForDuplicateUserName() {
        String userName = "ajain";
        User user = createUser(1, "Ajay", "Jain", "ajay.jain@pepcus.com", "ajain", "ThinkHR");
        
        when(userRepository.findByUserName(userName)).thenReturn(user);
        
        boolean isDuplicate = userService.checkDuplicate(userName);
        assertTrue(isDuplicate);
    }
    
    /**
     * Test to verify if user name not found in DB.
     * 
     */
    @Test
    public void testCheckDuplicateForNoDuplicateUserName() {
        String userName = "ajain";
        
        when(userRepository.findByUserName(userName)).thenReturn(null); 
        
        boolean isDuplicate = userService.checkDuplicate(userName);
        
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

        Mockito.doReturn(1).when(fileDataRepository).saveUserRecord(
                Mockito.anyListOf(String.class),
                Mockito.anyListOf(Object.class));

        when(userRepository.findOne(Matchers.anyInt())).thenReturn(new User());

        //Mock call  to add LearUser from throneUser
        when(learnUserService.addLearnUserForBulk(Matchers.any())).thenReturn(null);

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
        String resource = ApiTestDataUtil.RESOURCE_USER;
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
        Company broker = ApiTestDataUtil.createCompany();
        FileImportResult fileImportResult = new FileImportResult();
        String resource = ApiTestDataUtil.RESOURCE_USER;
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class, "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

        } catch (Exception e) {
            fail("Exception not expected");
        }

        List<CustomFields> customFields = ApiTestDataUtil
                .createCustomFieldsForUser();

        when(customFieldsRepository.findByCompanyIdAndCustomFieldType(companyId,
                resource)).thenReturn(customFields);

        List<StandardFields> standardFields = ApiTestDataUtil
                .createStandardFieldsForUser();

        when(standardFieldsRepository.findByType(ApplicationConstants.CONTACT))
                .thenReturn(standardFields);

        try {
            mockStatic(APIMessageUtil.class);

            PowerMockito.doReturn("MISSING_REQUIRED_FIELD").when(
                    APIMessageUtil.class, "getMessageFromResourceBundle",
                    resourceHandler, APIErrorCodes.MISSING_REQUIRED_FIELD,
                    "MISSING_FIELD");

        } catch (Exception e) {
            fail("Exception not expected");
        }

        fileImportResult = userService.processRecords(records, broker,
                resource);

        assertEquals(1, fileImportResult.getNumFailedRecords());
    }

    /**
     * Test to verify if existing email in csv file is in-valid
     * 
     */
    @Test
    public void testProcessRecordsForInValidEmail() {
        List<String> records = ApiTestDataUtil
                .getCsvRecordsForUserForInvalidMail();
        Company broker = ApiTestDataUtil.createCompany();
        FileImportResult fileImportResult = new FileImportResult();
        String resource = ApiTestDataUtil.RESOURCE_USER;
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class,
                    "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

            Mockito.when(FileImportUtil.getValueFromRow(Matchers.anyString(),
                    Matchers.anyInt())).thenCallRealMethod();

        } catch (Exception e) {
            fail("Exception not expected");
        }

        List<CustomFields> customFields = ApiTestDataUtil
                .createCustomFieldsForUser();

        when(customFieldsRepository.findByCompanyIdAndCustomFieldType(companyId,
                resource)).thenReturn(customFields);

        List<StandardFields> standardFields = ApiTestDataUtil
                .createStandardFieldsForUser();

        when(standardFieldsRepository.findByType(ApplicationConstants.CONTACT))
                .thenReturn(standardFields);

        try {
            mockStatic(APIMessageUtil.class);

            PowerMockito.doReturn("INVALID_EMAIL").when(APIMessageUtil.class,
                    "getMessageFromResourceBundle", resourceHandler,
                    APIErrorCodes.INVALID_EMAIL, "INVALID_EMAIL");

        } catch (Exception e) {
            fail("Exception not expected");
        }

        fileImportResult = userService.processRecords(records, broker,
                resource);

        assertEquals(1, fileImportResult.getNumFailedRecords());

    }

    /**
     * Test to verify if company having clientName and brokerId in csv file does
     * not exist in DB.
     * 
     * 
     */
    @Test
    public void testProcessRecords_ClientNotExists() {
        List<String> records = ApiTestDataUtil.getCsvRecordsForUser();
        Company broker = ApiTestDataUtil.createCompany();
        FileImportResult fileImportResult = new FileImportResult();
        String resource = ApiTestDataUtil.RESOURCE_USER;
        int companyId = 12345;
        broker.setCompanyId(companyId);

        try {
            mockStatic(FileImportUtil.class);

            PowerMockito.doNothing().when(FileImportUtil.class,
                    "validateAndFilterCustomHeaders", Matchers.any(),
                    Matchers.any(), Matchers.any(), Matchers.any());

            Mockito.when(FileImportUtil.getValueFromRow(Matchers.anyString(),
                    Matchers.anyInt())).thenCallRealMethod();

        } catch (Exception e) {
            fail("Exception not expected");
        }

        List<CustomFields> customFields = ApiTestDataUtil
                .createCustomFieldsForUser();

        when(customFieldsRepository.findByCompanyIdAndCustomFieldType(companyId,
                resource)).thenReturn(customFields);

        List<StandardFields> standardFields = ApiTestDataUtil
                .createStandardFieldsForUser();

        when(standardFieldsRepository.findByType(ApplicationConstants.CONTACT))
                .thenReturn(standardFields);

        when(companyRepository.findFirstByCompanyNameAndBroker(
                Matchers.anyString(), Matchers.anyInt())).thenReturn(null);

        try {
            mockStatic(APIMessageUtil.class);

            PowerMockito.doReturn("INVALID_CLIENT_NAME").when(
                    APIMessageUtil.class,
                    "getMessageFromResourceBundle", resourceHandler,
                    APIErrorCodes.INVALID_CLIENT_NAME, "INVALID_CLIENT_NAME");

        } catch (Exception e) {
            fail("Exception not expected");
        }

        fileImportResult = userService.processRecords(records, broker,
                resource);

        assertEquals(1, fileImportResult.getNumFailedRecords());

    }

    /**
     * Test to verify if fileToImport is null.
     * 
     */
    @Test
    public void testBulkUpload_fileToImportNull() {
        MultipartFile fileToImport = null;
        Integer brokerId = 1;
        try {
            FileImportResult  result = userService.bulkUpload(fileToImport, brokerId);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.REQUIRED_PARAMETER, ae.getApiErrorCode()); 
        }
    }

    /**
     * Test to verify if validateAndGetBroker method throws exception.
     * 
     */
    @Test
    public void testBulkUpload_validateAndGetBrokerFailed() {
        MultipartFile fileToImport = null;
        try {
            fileToImport = ApiTestDataUtil.createMockMultipartFile();
        } catch (IOException e) {
            fail("Exception not expected");
        }
        Integer brokerId = 1;

        when(companyRepository.findOne(brokerId)).thenReturn(null);

        try {
            FileImportResult result = userService.bulkUpload(fileToImport,
                    brokerId);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.INVALID_BROKER_ID,
                    ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify if validateAndGetFileContent method throws exception.
     * 
     */
    @Test
    public void testBulkUpload_validateAndGetFileContentFailed() {
        MultipartFile fileToImport = null;
        try {
            fileToImport = ApiTestDataUtil.createMockMultipartFile_EmptyFile();
        } catch (IOException e) {
            fail("Exception not expected");
        }
        Integer brokerId = 1;
        Company company = ApiTestDataUtil.createCompany();

        when(companyRepository.findOne(brokerId)).thenReturn(company);

        try {
            FileImportResult result = userService.bulkUpload(fileToImport,
                    brokerId);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT,
                    ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify generateUserName if userName is not blank and not duplicate
     * 
     */
    @Test
    public void testGenerateUserName_UsernameNotBlankNotDuplicate() {
        String userName = "AjayJain1";
        String email = "ajay.jain@pepcus.com";
        String firstName = "Ajay";
        String lastName = "Jain";

        String expectedUserName = "AjayJain1";

        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(false).when(userServiceSpy).checkDuplicate(userName);
        String generatedUserName = userServiceSpy.generateUserName(userName, email, firstName, lastName);

        assertEquals(expectedUserName, generatedUserName);
    }

    /**
     * Test to verify generateUserName if userName is blank/null and email not duplicate
     * 
     */
    @Test
    public void testGenerateUserName_BlankUsername_EmailNotDuplicate() {
        String userName = null;
        String email = "ajay.jain@pepcus.com";
        String firstName = "Ajay";
        String lastName = "Jain";

        String expectedUserName = "ajay.jain@pepcus.com";

        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(false).when(userServiceSpy).checkDuplicate(email);
        String generatedUserName = userServiceSpy.generateUserName(userName, email, firstName, lastName);

        assertEquals(expectedUserName, generatedUserName);
    }

    /**
     * Test to verify generateUserName if userName is duplicate
     * and name generated from firstName and lastName is not duplicate
     * 
     */
    @Test
    public void testGenerateUserName_DuplicateUsernameFound() {
        String userName = "AjayJain1";
        String email = "ajay.jain@pepcus.com";
        String firstName = "Ajay";
        String lastName = "Jain";

        String expectedUserName = "Ajay_Jain_1";

        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(true).when(userServiceSpy).checkDuplicate(userName);

        // Mock checkDuplicate so that name generated from firstName and lastName is not duplicate
        String nameFromFirstNameLastName = firstName + UNDERSCORE + lastName + UNDERSCORE + 1;
        Mockito.doReturn(false).when(userServiceSpy).checkDuplicate(nameFromFirstNameLastName);

        String generatedUserName = userServiceSpy.generateUserName(userName, email, firstName, lastName);

        assertEquals(expectedUserName, generatedUserName);
    }

    /**
     * Test to verify generateUserName if userName is duplicate
     * and name generated from firstName and lastName is duplicate
     * in first iteration and not in second iteration
     * 
     */
    @Test
    public void testGenerateUserName_DuplicateUsernameFound_AgainInFirstIteration() {
        String userName = "AjayJain1";
        String email = "ajay.jain@pepcus.com";
        String firstName = "Ajay";
        String lastName = "Jain";

        String expectedUserName = "Ajay_Jain_2";

        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(true).when(userServiceSpy).checkDuplicate(userName);

        // Mock checkDuplicate so that name generated from firstName and lastName is duplicate in first iteration
        String nameFromFirstNameLastName = firstName + UNDERSCORE + lastName + UNDERSCORE + 1;
        Mockito.doReturn(true).when(userServiceSpy).checkDuplicate(nameFromFirstNameLastName);

        // Mock checkDuplicate so that name generated from firstName and lastName is not duplicate in second iteration
        String nameFromFirstNameLastNameAgain = firstName + UNDERSCORE + lastName + UNDERSCORE + 2;
        Mockito.doReturn(false).when(userServiceSpy).checkDuplicate(nameFromFirstNameLastNameAgain);

        String generatedUserName = userServiceSpy.generateUserName(userName, email, firstName, lastName);

        assertEquals(expectedUserName, generatedUserName);
    }

    /**
     * Test to verify generateUserName if userName is duplicate
     * and name firstName and lastName is null/blank
     * 
     */
    @Test
    public void testGenerateUserName_DuplicateUsernameFound_FirstNameLastNameNull() {
        String userName = "AjayJain1";
        String email = "ajay.jain@pepcus.com";
        String firstName = null;
        String lastName = null;

        String expectedUserName = "null_null_1";

        UserService userServiceSpy = Mockito.spy(new UserService());
        Mockito.doReturn(true).when(userServiceSpy).checkDuplicate(userName);

        // Mock checkDuplicate so that name generated from firstName and lastName is not duplicate
        String nameFromFirstNameLastName = firstName + UNDERSCORE + lastName + UNDERSCORE + 1;
        Mockito.doReturn(false).when(userServiceSpy).checkDuplicate(nameFromFirstNameLastName);

        String generatedUserName = userServiceSpy.generateUserName(userName, email, firstName, lastName);

        assertEquals(expectedUserName, generatedUserName);
    }


    /**
     * Test validateRoleIdFromDB method.
     */
    @Test
    public void testValidateRoleIdFromDB() {
        Integer roleId = 1;
        LearnRole role = ApiTestDataUtil.createLearnRole(1, "Agent");

        when(roleRepository.findOne(roleId)).thenReturn(role);

        boolean isValid = userService.validateRoleIdFromDB(roleId);

        assertTrue(isValid);
    }

}
