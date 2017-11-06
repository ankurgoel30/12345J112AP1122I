package com.thinkhr.external.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.UserModel;
import com.thinkhr.external.api.repositories.UserRepository;

/**
* The UserService class provides a collection of all
* services related with users
*
* @author  Surabhi Bhawsar
* @since   2017-11-01 
*/

@Service
public class UserService extends CommonService {
	
    @Autowired	
    private UserRepository userRepository;

    /**
     * To fetch list of all users
     * @return
     */
    public List<UserModel> getAllUser() {
		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(user -> users.add(user));
		return users.stream().map(user -> { return (UserModel) convert(user, UserModel.class); } ).collect(Collectors.toList());
	}

    //TODO: Add implementation for other user services

}