package com.thinkhr.external.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUsers;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.EntitySearchSpecification;
import com.thinkhr.external.api.services.utils.EntitySearchUtil;

/**
 * Junit to verify methods of UserRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * To test userRepository.save method when adding user.
	 */
	@Test
	public void testSaveForAdd() {
		
		User user = createUser(null, "Jason", "Garner", "dummy help", "jgarner", 1, "dummyDate", "dummyCode", "updated");
		
		User userSaved = userRepository.save(user);
		
		assertNotNull(userSaved);
		assertNotNull(userSaved.getContactId());// As user is saved successfully.
		assertEquals(user.getSearchHelp(), userSaved.getSearchHelp());
		assertEquals(user.getFirstName(), userSaved.getFirstName());
		assertEquals(user.getLastName(), userSaved.getLastName());
		assertEquals(user.getUserName(), userSaved.getUserName());
	}
	
	/**
	 * To test findAll method
	 */
	@Test
	public void testFindAll() {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		List<User> userList =  (List<User>) userRepository.findAll();
		  
		assertNotNull(userList);
		assertEquals(10, userList.size());
	}

	/**
	 * To test findOne method
	 */
	@Test
	public void testFindOne() {
		User user = createUser(null, "PEPCUS", "Services", "dummy Help PEP", "pepcus", 1, "dummyDate", "dummyCode", "updated");
		
		//SAVE a User
		User savedUser = userRepository.save(user);

		User findUser =  userRepository.findOne(savedUser.getContactId());
		assertNotNull(findUser);
		assertEquals(user.getSearchHelp(), findUser.getSearchHelp());
		assertEquals(user.getFirstName(), findUser.getFirstName());
		assertEquals(user.getLastName(), findUser.getLastName());
	}
	
	/**
	 * To test delete method
	 */
	@Test
	public void testDeleteForSuccess() {
		User user = createUser(null, "PEPCUS", "Services", "dummy Help PEP", "pepcus", 1, "dummyDate", "dummyCode", "updated");
		
		//SAVE a User
		User savedUser = userRepository.save(user);

		//DELETING record here.
		userRepository.delete(savedUser);
		
		//FIND saved user with find and it should not  return
		User findUser =  userRepository.findOne(savedUser.getContactId());
		assertEquals(null, findUser);
	}
	
	/**
	 * To test delete method when exception is thrown
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	public void testDeleteForFailure() {
		Integer contactId = 1;	// No record is available in H2 DB
		// DELETING record here. 
		userRepository.delete(contactId);
	}
	
	/**
	 * To verify userRepository.save method when updating user.
	 * 
	 */
	
	@Test
	public void testSaveForUpdate(){

		User user = userRepository.save(createUser(null, "PEPCUS", "Services", "dummy Help PEP", "pepcus", 1, "dummyDate", "dummyCode", "updated"));
		
		// Updating company name
		user.setFirstName("Pepcus - Updated");
		
		User updatedUser = null;
		try {
			updatedUser = userRepository.save(user);
		} catch (ApplicationException e) {
			fail("Not expecting application exception for a valid test case");
		}
		assertEquals(user.getContactId(), updatedUser.getContactId());
		assertEquals("Pepcus - Updated", updatedUser.getFirstName());
	}
	
	/**
	 * Test to verify get all users when no parameters are provided 
	 * i.e., all parameters are default provided.  
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testAllUsersWithDefault() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		String searchSpec = null;
		Pageable pageable = getPageable(null, null, null, defaultSortField);
    	Specification<User> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<User>(searchSpec, new User());
    	}
    	Page<User> users  = (Page<User>) userRepository.findAll(spec, pageable);
    	
    	assertNotNull(users.getContent());
    	assertEquals(5, users.getContent().size());
	}

	*//**
	 * Test to verify get all users when searchSpec is default and all other 
	 * parameters are provided (sort is ascending)  
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testAllUsersWithParamsAndSearchSpecNull() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		String searchSpec = null;
		Pageable pageable = getPageable(3, 3, "+firstName", defaultSortField);
    	Specification<User> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<User>(searchSpec, new User());
    	}
    	Page<User> users  = (Page<User>) userRepository.findAll(spec, pageable);
    	
    	assertNotNull(users.getContent());
    	assertEquals(2, users.getContent().size());
	}
	
	*//**
	 * Test to verify get all users searchSpec is provided and other parameters are default.  
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testAllUsersWithParamsAndPageableNull() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		String searchSpec = "help";
		Pageable pageable = getPageable(null, null, null, defaultSortField);
    	Specification<User> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<User>(searchSpec, new User());
    	}
    	Page<User> users  = (Page<User>) userRepository.findAll(spec, pageable);
    	
    	assertNotNull(users.getContent());
    	assertEquals(3, users.getContent().size());
	}
	
	*//**
	 * Test to verify get all users when all parameters are provided 
	 * and sort is ascending   
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testAllUsersWithParamsAndAscSort() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		String searchSpec = "icici";
		Pageable pageable = getPageable(0, null, "+firstName", defaultSortField);
    	Specification<User> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<User>(searchSpec, new User());
    	}
    	Page<User> users  = (Page<User>) userRepository.findAll(spec, pageable);
    	
    	assertNotNull(users.getContent());
    	assertEquals(1, users.getContent().size());
	}
	
	*//**
	 * Test to verify get all users when all parameters are provided
	 * and sort is descending.  
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testAllUsersWithParamsAndDescSort() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		String searchSpec = "thr";
		Pageable pageable = getPageable(null, null, "-firstName", defaultSortField);
    	Specification<User> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<User>(searchSpec, new User());
    	}
    	Page<User> users  = (Page<User>) userRepository.findAll(spec, pageable);
    	
    	
    	assertNotNull(users.getContent());
    	assertEquals(1, users.getContent().size());
	}*/
	
	/**
	 * Test userRepository.pageable with limit = 5 
	 * @throws Exception
	 */
	@Test
	public void testFindAllWithPageableWithLimit() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		Pageable pageable = getPageable(0, 5, null, DEFAULT_SORT_BY_USER_NAME);

		Page<User> users  = (Page<User>) userRepository.findAll(null, pageable);
		
		assertNotNull(users.getContent());
		assertEquals(users.getContent().size(), 5);
	}
	
	/**
	 * Test userRepository.pageable with offset = 5 
	 * @throws Exception
	 */
	@Test
	public void testFindAllWithPageableWithOffset() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		Pageable pageable = getPageable(5, null, null, DEFAULT_SORT_BY_USER_NAME);

		Page<User> users  = (Page<User>) userRepository.findAll(null, pageable);
		
		assertNotNull(users.getContent());
		assertEquals(5, users.getContent().size()); //As offset = 5, so it will pick records by 5th 
	}

	/**
	 * Junit to verify search specification
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindAllWithSpecification() throws Exception {
		
		for (User user : createUsers()) {
			userRepository.save(user);
		}
		
		Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_USER_NAME);

		EntitySearchSpecification<User> specification = (EntitySearchSpecification<User>) EntitySearchUtil.
				getEntitySearchSpecification("ICICI", null, User.class, new User());
		
		Page<User> users  = (Page<User>) userRepository.findAll(specification, pageable);
		
		assertNotNull(users.getContent());
		assertEquals(1, users.getContent().size()); //As we have only one record have searchKey = "icici"
	}

}
