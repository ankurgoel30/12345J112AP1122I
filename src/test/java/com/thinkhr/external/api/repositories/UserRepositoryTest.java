package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUser;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createUserList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.validation.ConstraintViolationException;

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

        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");

        User userSaved = userRepository.save(user);

        assertNotNull(userSaved);
        assertNotNull(userSaved.getUserId());// As user is saved successfully.
        assertEquals(user.getFirstName(), userSaved.getFirstName());
        assertEquals(user.getLastName(), userSaved.getLastName());
        assertEquals(user.getUserName(), userSaved.getUserName());
        assertEquals(user.getEmail(), userSaved.getEmail()); 
        assertEquals(user.getCompanyName(), userSaved.getCompanyName());
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddFirstNameNull() {

        // first name is null
        String firstName = null;
        User user = createUser(null, firstName, "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");

        userRepository.save(user);
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddLastNameNull() {

        // last name is null
        String lastName = null;
        User user = createUser(null, "Jason", lastName, "jgarner@gmail.com", "jgarner", "Pepcus");

        userRepository.save(user);
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddEmailNull() {

        // email is null
        String email  = null;
        User user = createUser(null, "Jason", "Garner", email, "jgarner", "Pepcus");

        userRepository.save(user);
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddUserNameNull() {

        // user name is null
        String userName = null;
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", userName, "Pepcus");

        userRepository.save(user);
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddCompanyNameNull() {

        // company name is null
        String companyName = null;
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", companyName);

        userRepository.save(user);
    }

    /**
     * To test userRepository.save method when adding user if exception is thrown.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testSaveForAddEmailInvalid() {

        // email is not in well-formed pattern. 
        String email = "jgarner";
        User user = createUser(null, "Jason", "Garner", email, "jgarner", "Pepcus");

        userRepository.save(user);
    }

    /**
     * To test findAll method
     */
    @Test
    public void testFindAll() {

        for (User user : createUserList()) {
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
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");

        //SAVE a User
        User savedUser = userRepository.save(user);

        User findUser =  userRepository.findOne(savedUser.getUserId());
        assertNotNull(findUser);
        assertEquals(user.getFirstName(), findUser.getFirstName());
        assertEquals(user.getLastName(), findUser.getLastName());
        assertEquals(user.getEmail(), findUser.getEmail());
        assertEquals(user.getUserName(), findUser.getUserName());
        assertEquals(user.getCompanyName(), findUser.getCompanyName());
    }

    /**
     * To test findOne method when userId is not found.
     */
    @Test
    public void testFindOneForFailure() {
        Integer userId = 1;
        User user =  userRepository.findOne(userId);
        assertEquals(null, user);
    }

    /**
     * To test delete method
     */
    @Test
    public void testDeleteForSuccess() {
        User user = createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus");

        //SAVE a User
        User savedUser = userRepository.save(user);

        //DELETING record here.
        userRepository.delete(savedUser);

        //FIND saved user with find and it should not  return
        User findUser =  userRepository.findOne(savedUser.getUserId());
        assertEquals(null, findUser);
    }

    /**
     * To test delete method when exception is thrown
     */
    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteForFailure() {
        Integer userId = 1;	// No record is available in H2 DB
        // DELETING record here. 
        userRepository.delete(userId);
    }

    /**
     * To verify userRepository.save method when updating user.
     * 
     */
    @Test
    public void testUpdate(){

        User user = userRepository.save(createUser(null, "Jason", "Garner", "jgarner@gmail.com", "jgarner", "Pepcus"));
        // Updating first name
        user.setFirstName("Pepcus - Updated");
        User updatedUser = userRepository.save(user);
        assertEquals(user.getUserId(), updatedUser.getUserId());
        assertEquals("Pepcus - Updated", updatedUser.getFirstName());
    }

    /**
     * Test userRepository.pageable with limit = 5 
     * @throws Exception
     */
    @Test
    public void testFindAllWithPageableWithLimit() throws Exception {

        for (User user : createUserList()) {
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

        for (User user : createUserList()) {
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

        for (User user : createUserList()) {
            userRepository.save(user);
        }

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_USER_NAME);

        EntitySearchSpecification<User> specification = (EntitySearchSpecification<User>) EntitySearchUtil.
                getEntitySearchSpecification("Sandeep", null, User.class, new User());

        Page<User> users  = (Page<User>) userRepository.findAll(specification, pageable);

        assertNotNull(users.getContent());
        assertEquals(1, users.getContent().size()); //As we have only one record have searchKey = "Sandeep"
    }

}
