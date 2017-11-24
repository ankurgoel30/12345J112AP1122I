package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.USER_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
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

		given(userController.addUser(user)).willReturn(responseEntity);

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
	public void testAddUserUserNameNullBadRequest() throws Exception {
		User user = createUser();
		user.setUserName(null);

		mockMvc.perform(post(USER_API_BASE_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getJsonString(user)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
				.andExpect(jsonPath("errorDetails[0].field", is("userName")))
				.andExpect(jsonPath("errorDetails[0].object", is("user")))
				.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getUserName())));
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
	 * Test to verify put user API (/v1/users/{userId}) without passing
	 * userId to path parameter.
	 * 
	 * Expected - Should return 404 Not found response code
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserWithNoUserIdInPath() throws Exception {
		User user = createUser();

		ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);

		given(userController.updateUser(user.getUserId(), user)).willReturn(responseEntity);

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

		ResponseEntity<User> responseEntity = createUserResponseEntity(user, HttpStatus.OK);

		given(userController.updateUser(user.getUserId(), user)).willReturn(responseEntity);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getJsonString(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("user.userId", is(user.getUserId())))
				.andExpect(jsonPath("user.firstName", is(user.getFirstName())));
	}

	/**
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserLastNameNullBadRequest() throws Exception {
		User user = createUser();
		user.setLastName(null);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
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
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserFirstNameNullBadRequest() throws Exception {
		User user = createUser();
		user.setFirstName(null);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(getJsonString(user)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
				.andExpect(jsonPath("errorDetails[0].field", is("firstName")))
				.andExpect(jsonPath("errorDetails[0].object", is("user")))
				.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getFirstName())));
	}

	/**
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserCompanyNameNullBadRequest() throws Exception {
		User user = createUser();
		user.setCompanyName(null);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
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
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserEmailNullBadRequest() throws Exception {
		User user = createUser();
		user.setEmail(null);

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
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
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserEmailInvalidBadRequest() throws Exception {
		User user = createUser();
		
		// setting not a well-formed email address 
		user.setEmail("ssolanki");

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
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
	 * Test to verify put user API (/v1/users/{userId}) with a In-valid
	 * request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserUserNameNullBadRequest() throws Exception {
		User user = createUser();
		user.setUserName(null);
		;

		mockMvc.perform(put(USER_API_BASE_PATH + user.getUserId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getJsonString(user)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
				.andExpect(jsonPath("errorDetails[0].field", is("userName")))
				.andExpect(jsonPath("errorDetails[0].object", is("user")))
				.andExpect(jsonPath("errorDetails[0].rejectedValue", is(user.getUserName())));
	}

	/**
	 * Test to verify delete user API (/v1/users/{userId}) .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUser() throws Exception {

		User user = createUser();

		ResponseEntity<Integer> responseEntity = createUserIdResponseEntity(user.getUserId(), HttpStatus.NO_CONTENT);

		given(userController.deleteUser(user.getUserId())).willReturn(responseEntity);

		mockMvc.perform(delete(USER_API_BASE_PATH + user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
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

}
