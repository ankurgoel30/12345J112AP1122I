package com.thinkhr.external.api.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;

/**
 * Junits to test methods of UserController
 * @author Surabhi Bhawsar
 * @since 2011-11-06
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
	 * Test to verify Get companies API (/v1/users)  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAllUser() throws Exception {
		//TODO: Add implementation here
	}
	
	/**
	 * Test to verify Get user by id API (/v1/users/{userId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserById() throws Exception {
		//TODO: Add implementation here
	}

	/**
	 * Test to verify post user API (/v1/users). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddUser() throws Exception {
		//TODO: Add implementation here
	}

	/**
	 * Test to verify put user API (/v1/users). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateUser() throws Exception {
		//TODO: Add implementation here
	}

	/**
	 * Test to verify delete user API (/v1/users/{userId}) . 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUser() throws Exception {
		
		//TODO: Add implementation here
	}

}
