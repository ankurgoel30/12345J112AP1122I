package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_USER_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FILE_EXTENSION_IMPORT;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.USER_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createBulkUserResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createBulkUsers;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createInputStreamResponseEntityForBulkUpload;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createMockMultipartFile;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.BulkJsonModel;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit class to test all the methods\APIs written for UserController
 * 
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserController userController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * Test to verify Get users API (/v1/users) when no request parameters
     * (default) are provided
     * 
     * @throws Exception
     */
    @Test
    public void testAllUser() throws Exception {

        List<User> userList = createUserList();

        given(userController.getAllUser(null, null, null, null, null)).willReturn(userList);

        mockMvc.perform(get(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    /**
     * Test to verify Get All Users API (/v1/users) when no records are
     * available
     * 
     * @throws Exception
     */
    @Test
    public void testAllUserWithEmptyResponse() throws Exception {

        List<User> userList = null;

        given(userController.getAllUser(null, null, null, null, null)).willReturn(userList);

        mockMvc.perform(get(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("message", IsNot.not("")));
    }

    /**
     * Test to verify Get user by id API (/v1/users/{userId}).
     * 
     * @throws Exception
     */
    @Test
    public void testGetUserById() throws Exception {
        User user = createUser();

        given(userController.getById(user.getUserId())).willReturn(user);

        mockMvc.perform(get(USER_API_BASE_PATH + user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("user.userId", is(user.getUserId())))
        .andExpect(jsonPath("user.userName", is(user.getUserName())));
    }

    /**
     * Test to verify Get user by id API (/v1/users/{userId}). API should
     * return NOT_FOUND as response code
     * 
     * @throws Exception
     */
    @Test
    public void testGetUserByIdNotExists() throws Exception {
        Integer userId = new Integer(1);

        given(userController.getById(userId)).willThrow(ApplicationException
                .createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId=" + userId));

        MvcResult result = mockMvc.perform(get(USER_API_BASE_PATH + userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        int status = result.getResponse().getStatus();
        assertEquals("Incorrest Response Status", HttpStatus.NOT_FOUND.value(), status);
    }

    /**
     * Test to verify post user API (/v1/users) with a valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUser() throws Exception {
        User user = createUser();

        ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.CREATED);

        given(userController.addUser(Mockito.any(User.class), Mockito.anyInt())).willReturn(responseEntity);

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
                .andExpect(status().isCreated())
        .andExpect(jsonPath("user.userId", is(user.getUserId())))
        .andExpect(jsonPath("user.firstName", is(user.getFirstName())))
        .andExpect(jsonPath("user.lastName", is(user.getLastName())))
        .andExpect(jsonPath("user.email", is(user.getEmail())))
        .andExpect(jsonPath("user.companyName", is(user.getCompanyName())))
        .andExpect(jsonPath("user.userName", is(user.getUserName())));
    }

    /**
     * Test to verify post user API (/v1/users) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUserLastNameNullBadRequest() throws Exception {
        User user = createUser();
        user.setLastName(null);

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("lastName")))
        .andExpect(jsonPath("errorDetails[0].object", is("user")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getLastName())));
    }

    /**
     * Test to verify post user API (/v1/users) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUserFirstNameNullBadRequest() throws Exception {
        User user = createUser();
        user.setFirstName(null);

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("firstName")))
        .andExpect(jsonPath("errorDetails[0].object", is("user")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getFirstName())));
    }

    /**
     * Test to verify post user API (/v1/users) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUserEmailNullBadRequest() throws Exception {
        User user = createUser();
        user.setEmail(null);

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("email")))
        .andExpect(jsonPath("errorDetails[0].object", is("user")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getEmail())));
    }

    /**
     * Test to verify post user API (/v1/users) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUserEmailInvalidBadRequest() throws Exception {
        User user = createUser();

        // setting not a well-formed email address 
        user.setEmail("ssolanki");

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("email")))
        .andExpect(jsonPath("errorDetails[0].object", is("user")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getEmail())));
    }

    /**
     * Test to verify post user API (/v1/users) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddUserCompanyNameNullBadRequest() throws Exception {

        User user = createUser();

        user.setCompanyName(null);

        mockMvc.perform(post(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companyName")))
        .andExpect(jsonPath("errorDetails[0].object", is("user")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getCompanyName())));
    }

    /**
     * Test to verify put user API (/v1/users/{userId}) without passing userId
     * to path parameter.
     * 
     * Expected - Should return 405 Method not allowed response code
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateUserWithNoUserIdInPath() throws Exception {
        User user = createUser();
        String userJson = ApiTestDataUtil.getJsonString(user);

        ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);

        given(userController.updateUser(user.getUserId(), userJson, 1)).willReturn(responseEntity);

        mockMvc.perform(put(USER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(user)))
        .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test to verify put user API (/v1/users/{userId}).
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateUser() throws Exception {
        User user = createUser();
        String userJson = ApiTestDataUtil.getJsonString(user);

        ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);

        given(userController.updateUser(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
                .willReturn(responseEntity);

        mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("user.userId", is(user.getUserId())))
        .andExpect(jsonPath("user.firstName", is(user.getFirstName())));
    }

    /**
     * Test to verify delete user API (/v1/users/{userId}) .
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteUser() throws Exception {

        User user = createUser();

        ResponseEntity<Integer> responseEntity = createUserIdResponseEntity(user.getUserId(), HttpStatus.ACCEPTED);

        given(userController.deleteUser(user.getUserId())).willReturn(responseEntity);

        mockMvc.perform(delete(USER_API_BASE_PATH + user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    /**
     * Test to verify delete user API (/v1/users/{userId}) for EntityNotFound
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteUserForEntityNotFound() throws Exception {

        User user = createUser();

        given(userController.deleteUser(user.getUserId())).willThrow(ApplicationException
                .createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, String.valueOf(user.getUserId())));

        mockMvc.perform(delete(USER_API_BASE_PATH + user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

    /**
     * Test to verify post (/v1/users/bulk) when it gives error message for
     * invalid file extension
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadFile_InvalidExtension() throws Exception {
        MockMultipartFile multipartFile = createMockMultipartFile();
        ApplicationException mockedExp = ApplicationException
                .createBulkImportError(APIErrorCodes.INVALID_FILE_EXTENTION,
                        "Test.abc", VALID_FILE_EXTENSION_IMPORT);

        given(userController.bulkUploadFile(any(), any())).willThrow(mockedExp);

        mockMvc.perform(
                fileUpload(USER_API_BASE_PATH + "bulk").file(multipartFile))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errorCode",
                        is(APIErrorCodes.INVALID_FILE_EXTENTION.getCode()
                                .toString())));
    }

    /**
     * Test to verify post (/v1/users/bulk) when it gives error message for No
     * records
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadFile_NoRecords() throws Exception {
        MockMultipartFile multipartFile = createMockMultipartFile();
        ApplicationException mockedExp = ApplicationException
                .createBulkImportError(
                        APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, "Test.csv");

        given(userController.bulkUploadFile(any(), any())).willThrow(mockedExp);

        mockMvc.perform(
                fileUpload(USER_API_BASE_PATH + "bulk").file(multipartFile))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errorCode",
                        is(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT.getCode()
                                .toString())));
    }

    /**
     * Test to verify post (/v1/users/bulk) when it gives error message for
     * Missing Required Headers
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadFile_MissingHeaders() throws Exception {
        MockMultipartFile multipartFile = createMockMultipartFile();

        String requiredHeaders = String.join(",",
                REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
        ApplicationException mockedExp = ApplicationException
                .createBulkImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS,
                        "Test.csv", "CLIENT_NAME,DISPLAY_NAME",
                        requiredHeaders);

        given(userController.bulkUploadFile(any(), any())).willThrow(mockedExp);

        mockMvc.perform(
                fileUpload(USER_API_BASE_PATH + "bulk").file(multipartFile))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errorCode",
                        is(APIErrorCodes.MISSING_REQUIRED_HEADERS.getCode()
                                .toString())));
    }

    /**
     * Test to verify post (/v1/users/bulk) when it gives error message for
     * Max records exceed
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadFile_MaxRecordExceed() throws Exception {
        MockMultipartFile multipartFile = createMockMultipartFile();

        ApplicationException mockedExp = ApplicationException
                .createBulkImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                        String.valueOf(MAX_RECORDS_COMPANY_CSV_IMPORT));

        given(userController.bulkUploadFile(any(), any())).willThrow(mockedExp);

        mockMvc.perform(
                fileUpload(USER_API_BASE_PATH + "bulk").file(multipartFile))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errorCode",
                        is(APIErrorCodes.MAX_RECORD_EXCEEDED.getCode()
                                .toString())));
    }

    /**
     * Test to verify post (/v1/users/bulk) when it executes successfully
     * and returns a response file with details of file import results
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadFile_Success() throws Exception {
        MockMultipartFile multipartFile = createMockMultipartFile();

        ResponseEntity<InputStreamResource> inputStreamResource = createInputStreamResponseEntityForBulkUpload();
        given(userController.bulkUploadFile(any(), any()))
                .willReturn(inputStreamResource);

        ResultActions resultActions = mockMvc
                .perform(fileUpload(USER_API_BASE_PATH + "bulk")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }
    
    /**
     * Test to verify post (/v1/users/bulk) when it executes successfully for JSON in request Body and returns 
     * a JSON response with details of JSON upload results
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadJson_Success() throws Exception {
       
        List<BulkJsonModel> users = createBulkUsers();
        
        ResponseEntity<List<BulkJsonModel>> responseEntity = createBulkUserResponseEntity(users, HttpStatus.CREATED);
 
        given(userController.bulkUploadJson(any(), any())).willReturn(responseEntity);
        
        mockMvc.perform(post(USER_API_BASE_PATH + "bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(users)))
        .andExpect(status().isCreated());
    }
    
    /**
     * Test to verify post (/v1/users/bulk) when it gives Required Field Missing Error for JSON with required field missing in request Body and returns 
     * a JSON response with details of JSON upload results
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadJson_MissingRequiredAttribute() throws Exception {
       
        List<BulkJsonModel> users = createBulkUsers();
        BulkJsonModel user = users.get(0);
        Map<String,Object> props = user.getProperties();
        props.remove("firstName");
        user.setProperties(props);
        users.set(0, user);
        
        String requiredHeaders = String.join(",", REQUIRED_HEADERS_USER_CSV_IMPORT);
        ApplicationException mockedExp = ApplicationException.createBulkImportError(APIErrorCodes.MISSING_REQUIRED_FIELDS, "USER",
                "firstName", requiredHeaders);
 
        given(userController.bulkUploadJson(any(), any())).willThrow(mockedExp);
        
        mockMvc.perform(post(USER_API_BASE_PATH + "bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(users)))
        .andExpect(status().isNotAcceptable()).andExpect(jsonPath("errorCode", is(APIErrorCodes.MISSING_REQUIRED_FIELDS.getCode().toString())));
    }
    
    /**
     * Test to verify post (/v1/users/bulk) when it gives Required Field Missing Error for JSON with required field wrong in request Body and returns 
     * a JSON response with details of JSON upload results
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadJson_WrongAttribute() throws Exception {
       
        List<BulkJsonModel> users = createBulkUsers();
        BulkJsonModel user = users.get(0);
        Map<String,Object> props = user.getProperties();
        props.remove("firstName");
        props.put("firstname", "Pepcus");
        user.setProperties(props);
        users.set(0, user);
        
        String requiredHeaders = String.join(",", REQUIRED_HEADERS_USER_CSV_IMPORT);
        ApplicationException mockedExp = ApplicationException.createBulkImportError(APIErrorCodes.MISSING_REQUIRED_FIELDS, "USER",
                "firstName", requiredHeaders);
 
        given(userController.bulkUploadJson(any(), any())).willThrow(mockedExp);
        
        mockMvc.perform(post(USER_API_BASE_PATH + "bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(users)))
        .andExpect(status().isNotAcceptable()).andExpect(jsonPath("errorCode", is(APIErrorCodes.MISSING_REQUIRED_FIELDS.getCode().toString())));
    }
    
    /**
     * Test to verify post (/v1/users/bulk) when it gives Required Field Missing Error for JSON with custom field wrong in request Body and returns 
     * a JSON response with details of JSON upload results
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testBulkUploadJson_MissingCustomAttribute() throws Exception {
       
        List<BulkJsonModel> users = createBulkUsers();
        BulkJsonModel user = users.remove(0);
        Map<String,Object> props = user.getProperties();
        props.remove("businessId");
        props.put("business_Ids", "4649973");
        user.setProperties(props);
        users.set(0, user);

        ApplicationException mockedExp = ApplicationException.createBulkImportError(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS,"BUSINESS_IDS");
 
        given(userController.bulkUploadJson(any(), any())).willThrow(mockedExp);
        
        mockMvc.perform(post(USER_API_BASE_PATH + "bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(users)))
        .andExpect(status().isNotAcceptable()).andExpect(jsonPath("errorCode", is(APIErrorCodes.UNMAPPED_CUSTOM_HEADERS.getCode().toString())));
    }

}
