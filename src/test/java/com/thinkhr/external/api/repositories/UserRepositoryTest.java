package com.thinkhr.external.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUsers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.services.EntitySearchSpecification;

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
	
	private String defaultSortField = "+userName";
	
	private Integer savedUserId;  
	

	/**
	 * To test save method
	 */
	@Test
	public void testSave() {
		
		User user = createUser(null, "Jason", "Garner");
		
		user.setSearchHelp("dummy help");
		user.setUserName("jgarner");
		user.setBlockedAccount(1);
		user.setMkdate("dummyDate");
		user.setCodevalid("dummyCode");
		user.setUpdatePassword("updated"); 
		
		User userSaved = userRepository.save(user);
		
		savedUserId = user.getContactId();
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
		assertEquals(5, userList.size());
	}

	/**
	 * To test findOne method
	 */
	@Test
	public void testFindOne() {
		User user = createUser(null, "PEPCUS", "Services");
		user.setSearchHelp("dummy Help PEP");
		user.setUserName("pepcus");
		user.setBlockedAccount(1);
		user.setMkdate("dummyDate");
		user.setCodevalid("dummyCode");
		user.setUpdatePassword("updated");
		
		//SAVE a User
		User savedUser = userRepository.save(user);

		User findUser =  userRepository.findOne(savedUser.getContactId());
		assertNotNull(findUser);
		assertEquals("dummy Help PEP", findUser.getSearchHelp());
		assertEquals("PEPCUS", findUser.getFirstName());
		assertEquals("pepcus", findUser.getUserName());
	}
	
	/**
	 * To test delete method
	 */
	@Test
	public void testDelete() {
		User user = createUser(null, "PEPCUS", "Services");
		user.setSearchHelp("dummy help pepcus");
		user.setUserName("pepcus");
		user.setBlockedAccount(1);
		user.setMkdate("dummyDate");
		user.setCodevalid("dummyCode");
		user.setUpdatePassword("updated");
		
		//SAVE a User
		User savedUser = userRepository.save(user);

		//DELETING record here.
		userRepository.delete(savedUser);
		
		//FIND saved user with find and it should not  return
		User findUser =  userRepository.findOne(savedUser.getContactId());
		assertEquals(null, findUser);
	}
	
	/**
	 * Test to verify get all users when no parameters are provided 
	 * i.e., all parameters are default provided.  
	 * 
	 * @throws Exception
	 */
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

	/**
	 * Test to verify get all users when searchSpec is default and all other 
	 * parameters are provided (sort is ascending)  
	 * 
	 * @throws Exception
	 */
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
	
	/**
	 * Test to verify get all users searchSpec is provided and other parameters are default.  
	 * 
	 * @throws Exception
	 */
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
	
	/**
	 * Test to verify get all users when all parameters are provided 
	 * and sort is ascending   
	 * 
	 * @throws Exception
	 */
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
	
	/**
	 * Test to verify get all users when all parameters are provided
	 * and sort is descending.  
	 * 
	 * @throws Exception
	 */
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
	}

}
