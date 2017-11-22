package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.USER_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createContactIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUsers;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

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
	 * Test to verify Get users API (/v1/users) when no request parameters (default) are provided  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllUser() throws Exception {
		
		List<User> userList = createUsers();

		given(userController.getAllUser(null, 10, null, null, null)).willReturn(userList);
		
		mockMvc.perform(get(USER_API_BASE_PATH+"?limit=10")
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("limit", is("10")))
		.andExpect(jsonPath("sort", is("userName ASC")))
		.andExpect(jsonPath("offset", is("0")));
	}
	
	/**
	 * Test to verify Get All Users API (/v1/users) when no records are available
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
	 * Test to verify Get user by id API (/v1/users/{contactId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserById() throws Exception {
		User user = createUser(); 
		
		given(userController.getById(user.getContactId())).willReturn(user);

		mockMvc.perform(get(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("user.contactId", is(user.getContactId())))
		.andExpect(jsonPath("user.userName", is(user.getUserName())));
	}
	
	/**
	 * Test to verify Get user by id API (/v1/users/{contactId}). 
	 * API should return NOT_FOUND as response code
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserByIdNotExists() throws Exception {
		Integer contactId = new Integer(1);
		
		given(userController.getById(contactId)).willThrow(ApplicationException.
				createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "contactId=" + contactId));

		MvcResult result = mockMvc.perform(get(USER_API_BASE_PATH + contactId)
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andReturn();
		
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
		
		given(userController.addUser(user)).willReturn(responseEntity);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("user.userName", is(user.getUserName())));
	}

	/**
	 * Test to verify post user API (/v1/users) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddUserSearchHelpNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setSearchHelp(null);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("searchHelp")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getSearchHelp())));
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
	public void testAddUserBlockedAccountNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setBlockedAccount(null);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("blockedAccount")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getBlockedAccount())));
	}
	
	/**
	 * Test to verify post user API (/v1/users) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddUserMkDateNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setMkdate(null);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("mkdate")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getMkdate())));
	}
	
	/**
	 * Test to verify post user API (/v1/users) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddUserCodevalidNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setCodevalid(null);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("codevalid")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getCodevalid())));
	}
	
	/**
	 * Test to verify post user API (/v1/users) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddUserUpdatePasswordNullBadRequest() throws Exception {
		
		User user = createUser(); 
		
		user.setUpdatePassword(null);
		
		mockMvc.perform(post(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("updatePassword")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getUpdatePassword())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) without passing
	 * contactId to path parameter.
	 * 
	 * Expected - Should return 404 Not found response code
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserWithNoContactIdInPath() throws Exception {
		User user = createUser(); 
		
		ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);
		
		given(userController.updateUser(user.getContactId(), user)).willReturn(responseEntity);

		mockMvc.perform(put(USER_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isMethodNotAllowed());
	}


	/**
	 * Test to verify put user API (/v1/users/{contactId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUser() throws Exception {
		User user = createUser(); 
		
		ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);
		
		given(userController.updateUser(user.getContactId(), user)).willReturn(responseEntity);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("user.firstName", is(user.getFirstName())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserSearchHelpNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setSearchHelp(null);
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("searchHelp")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getSearchHelp())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserFirstNameNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setFirstName(null);
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
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
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserBlockedAccountNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setBlockedAccount(null);
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("blockedAccount")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getBlockedAccount())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserMkDateNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setMkdate(null);
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("mkdate")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getMkdate())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserCodevalidNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setCodevalid(null);;
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("codevalid")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getCodevalid())));
	}
	
	/**
	 * Test to verify put user API (/v1/users/{contactId}) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserUpdatePasswordNullBadRequest() throws Exception {
		User user = createUser(); 
		user.setUpdatePassword(null);
		
		mockMvc.perform(put(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(user)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("updatePassword")))
		.andExpect(jsonPath("errorDetails[0].object", is("user")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getUpdatePassword())));
	}

	
	/**
	 * Test to verify delete user API (/v1/users/{contactId}) . 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUser() throws Exception {
		
		User user = createUser(); 
		
		ResponseEntity<Integer> responseEntity = createContactIdResponseEntity(user.getContactId(), HttpStatus.NO_CONTENT);

		given(userController.deleteUser(user.getContactId())).willReturn(responseEntity);

		mockMvc.perform(delete(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(204));
	}
	
	/**
	 * Test to verify delete user API (/v1/users/{contactId}) for EntityNotFound
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUserForEntityNotFound() throws Exception {
		
		User user = createUser(); 
		
		given(userController.deleteUser(user.getContactId())).willThrow(ApplicationException.
				createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, String.valueOf(user.getContactId())));

		mockMvc.perform(delete(USER_API_BASE_PATH + user.getContactId())
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

}
