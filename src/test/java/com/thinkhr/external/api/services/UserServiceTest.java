package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.repositories.UserRepository;

/**
 * Junit to test all the methods of UserService.
 * 
 * 
 */
@RunWith(SpringRunner.class)
public class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
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
	@Test
	public void testGetUserNotExists() {
		Integer userId = 1;
		when(userRepository.findOne(userId)).thenReturn(null);
		User result = userService.getUser(userId);
		assertNull("userId " + userId + " does not exist", result);
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

}
