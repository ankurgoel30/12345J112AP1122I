package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.UserService;

/**
 * User Controller for performing operations
 * related with User object.
 * 
 */
@RestController
@Validated
@RequestMapping(path="/v1/users")
public class UserController {
	
    @Autowired
    UserService userService;

    /**
     * Get all users from repository
     * @return List<User>
     * 
     */
    @RequestMapping(method=RequestMethod.GET)
    List<User> getAllUser(@Range(min = 0l, message = "Please select positive integer value for 'offset'") 
    		@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
    		@Range(min = 1l, message = "Please select positive integer and should be greater than 0 for 'limit'")
    		@RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit, 
    		@RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_BY_USER_NAME) String sort,
    		@RequestParam(value = "searchSpec", required = false) String searchSpec,
    		@RequestParam Map<String, String> allRequestParams) throws ApplicationException {
    	
        return userService.getAllUser(offset, limit, sort, searchSpec, allRequestParams);
    }
    
    /**
     * Get user with given id from repository
     * @param id user id
     * @return User object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET, value="/{userId}")
    public User getById(@PathVariable(name="userId", value = "userId") Integer userId) throws ApplicationException { 
        return userService.getUser(userId);
    }
    
    
    /**
     * Delete specific user from database
     * 
     * @param userId
     */
    @RequestMapping(method=RequestMethod.DELETE, value="/{userId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable(name="userId", value = "userId") Integer userId) throws ApplicationException{
    	userService.deleteUser(userId);
    	return new ResponseEntity <Integer>(userId, HttpStatus.ACCEPTED);
    }
    
    /**
     * Update a user in database
     * 
     * @param User object
     */
    @RequestMapping(method=RequestMethod.PUT, value="/{userId}")
	public ResponseEntity <User> updateUser(@PathVariable(name="userId", value = "userId") Integer userId, 
			@Valid @RequestBody User user) throws ApplicationException {
    	user.setUserId(userId);
    	userService.updateUser(user);
        return new ResponseEntity<User> (user, HttpStatus.OK);
	}
    
    
    /**
     * Add a user in database
     * 
     * @param User object
     */
    @RequestMapping(method=RequestMethod.POST)
   	public ResponseEntity<User>  addUser(@Valid @RequestBody User user)  throws ApplicationException {
   		userService.addUser(user);
   		return new ResponseEntity<User>(user, HttpStatus.CREATED);
   	}
}

