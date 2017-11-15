package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
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
    public static final String DEFAULT_SORT_BY = "userName";

    /**
     * Fetch all users from database based on offset, limit and sortField and search criteria
     * 
     * @param Integer offset First record index from database after sorting. Default value is 0
     * @param Integer limit Number of records to be fetched. Default value is 50
     * @param String sortField Field on which records needs to be sorted
     * @param String searchSpec Search string for filtering results
     * @return List<User> object 
     */
    public List<User> getAllUser(Integer offset,Integer limit ,String sortField,String searchSpec,Map<String, String> requestParameters) throws ApplicationException  {
    	List<User> users = new ArrayList<User>();

    	Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
    	Specification<User> spec = getEntitySearchSpecification(searchSpec, requestParameters, User.class, new User());
    	Page<User> userList  = (Page<User>) userRepository.findAll(spec,pageable);

    	userList.getContent().forEach(c -> users.add(c));
    	
    	return users;
    }

    /**
     * Fetch specific user from system
     * @param userId
     * @return User object 
     */
    public User getUser(Integer userId) {
    	User user = null;
    	user = userRepository.findOne(userId);
    	return user;
    }
    
    /**
     * Add a user in system
     * @param User object
     */
    public void addUser(User user)  {
    	userRepository.save(user);
    }
    
    /**
     * Update a user in database
     * 
     * @param User object
     * @throws ApplicationException 
     */
    public User updateUser(User user) throws ApplicationException  {
    	Integer userId = user.getContactId();
    	
		if (null == userRepository.findOne(userId)) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "contactId="+userId);
    	}
		
    	return userRepository.save(user);
    }
    
    /**
     * Delete specific user from database
     * 
     * @param userId
     * @throws ApplicationException 
     */
    public int deleteUser(int userId) throws ApplicationException {
    	try {
    		userRepository.delete(userId);
    	} catch (EmptyResultDataAccessException ex ) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "contactId="+userId);
    	}
    	return userId;
    }    
    
    
    /**
     * Return default sort field for user service
     * 
     * @return String 
     */
    @Override
    public String getDefaultSortField()  {
    	return DEFAULT_SORT_BY;
    }

}