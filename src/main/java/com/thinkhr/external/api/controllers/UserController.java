package com.thinkhr.external.api.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    List<User> getAllUser(@RequestParam(value = "offset", required = false) Integer offset,
    		@RequestParam(value = "limit", required = false) Integer limit,@RequestParam(value = "sort" , required = false) String sort,
    		@RequestParam(value = "searchSpec" , required = false) String searchSpec,
    		@RequestParam Map<String, String> allRequestParams) throws ApplicationException {
        return userService.getAllUser(offset,limit,sort,searchSpec,allRequestParams);
    }
    
    /**
     * Get user with given id from repository
     * @param id user id
     * @return User object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET,value="/{contactId}")
    public User getById(@PathVariable(name="contactId",value = "contactId") Integer contactId) throws ApplicationException { 
        User user = userService.getUser(contactId);
        if (user == null) {
        	throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "contactId="+ contactId);
        }
        return user;
    }
    
    
    /**
     * Delete specific user from database
     * 
     * @param userId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{contactId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable(name="contactId",value = "contactId") Integer contactId) throws ApplicationException{
    	userService.deleteUser(contactId);
    	return new ResponseEntity <Integer>(contactId, HttpStatus.NO_CONTENT);
    }
    
    /**
     * Update a user in database
     * 
     * @param User object
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{contactId}")
	public ResponseEntity <User> updateUser(@PathVariable(name="contactId",value = "contactId") Integer contactId, @RequestBody User user) throws ApplicationException {
    	user.setContactId(contactId);
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

