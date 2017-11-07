package com.thinkhr.external.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.thinkhr.external.api.model.UserModel;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.services.UserService;

/**
 * User Controller for performing operations
 * related with User object.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-01
 * 
 */
@RestController
@RequestMapping(path="/v1/users")
public class UserController {
	
    @Autowired
    UserRepository userRepo;
    
    @Autowired
    UserService userService;
    
    /**
     * Get all users from repository
     * @return
     * 
     */

    @GetMapping
    public @ResponseBody
    List<UserModel> getAllUsers() {
        return userService.getAllUser();
    }
    
}

