package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.LIMIT;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.OFFSET;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.SEARCH_SPEC;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.USER_SORT_BY;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUsers;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.User;
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
	
	private String defaultSortField = "+userName";
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * To verify getAllUsers method when no specific (Default) method arguments are provided 
	 * 
	 */
	@Test
	public void testGetAllUsers(){
		List<User> userList = createUsers();
		
		Pageable pageable = getPageable(null, null, null, defaultSortField);
		
		when(userRepository.findAll(null, pageable)).thenReturn(new PageImpl<User>(userList, pageable, userList.size()));

		try {
			List<User> result =  userService.getAllUser(null, null, null, null, null);
			assertEquals(5, result.size());
		} catch (ApplicationException ex) {
			fail("Not expected exception");
		}
		
		//TODO: ADD MORE test cases to verify limit, offset, sort and other search parameters.
	}
	
	/**
	 * To verify getAllUser method when specific method arguments are provided
	 * 
	 */
	@Test
	public void testGetAllUserForParams(){
		List<User> userList = createUsers();
		
		Pageable pageable = getPageable(OFFSET, LIMIT, USER_SORT_BY, defaultSortField);
		Specification<User> spec = null;
    	if(SEARCH_SPEC != null && SEARCH_SPEC.trim() != "") {
    		spec = new EntitySearchSpecification<User>(SEARCH_SPEC, new User());
    	}
		when(userRepository.findAll(spec, pageable)).thenReturn(new PageImpl<User>(userList, pageable, userList.size()));

		List<User> result;
		try {
			result = userService.getAllUser(OFFSET, LIMIT, USER_SORT_BY, SEARCH_SPEC, null);
			assertEquals(5, result.size());
		} catch (ApplicationException e) {
			fail("Not expecting application exception for a valid test case");
		}

	}
	
	/**
	 * To verify getUser method when user exists.
	 * 
	 */
	@Test
	public void testGetUser() {
		User user = createUser();
		
		when(userRepository.findOne(user.getContactId())).thenReturn(user);
		User result = userService.getUser(user.getContactId());
		assertEquals(user.getContactId(), result.getContactId());
		assertEquals("Pepcus", result.getFirstName());
		assertEquals("Software", result.getLastName());
		assertEquals("dummy help", result.getSearchHelp());
		assertEquals("pepcus", result.getUserName());
	}
	
	/**
	 * To verify getUser method when user does not exist.
	 * 
	 */
	@Test
	public void testGetUserNotExists() {
		Integer contactIdId = 16;
		when(userRepository.findOne(contactIdId)).thenReturn(null);
		User result = userService.getUser(contactIdId);
		assertNull("contactId " + contactIdId + " does not exist", result);
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
		assertEquals(user.getContactId(), result.getContactId());
		assertEquals("Pepcus", result.getFirstName());
		assertEquals("Software", result.getLastName());
		assertEquals("dummy help", result.getSearchHelp());
		assertEquals("pepcus", result.getUserName());
	}

	/**
	 * To verify updateUser method
	 * 
	 */
	
	@Test
	public void testUpdateUser(){

		User user = createUser();

		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findOne(user.getContactId())).thenReturn(user);
		User result = null;
		try {
			result = userService.updateUser(user);
		} catch (ApplicationException e) {
			fail("Not expecting application exception for a valid test case");
		}
		assertEquals(user.getContactId(), result.getContactId());
		assertEquals("Pepcus", result.getFirstName());
		assertEquals("Software", result.getLastName());
		assertEquals("dummy help", result.getSearchHelp());
		assertEquals("pepcus", result.getUserName());
	}
	
	/**
	 * To verify deleteUser method
	 * 
	 */
	@Test
	public void testDeleteUser() {
		Integer contactId = 1;
		try {
			userService.deleteUser(contactId);
		} catch (ApplicationException e) {
		}
        verify(userRepository, times(1)).delete(contactId);
	}

}
