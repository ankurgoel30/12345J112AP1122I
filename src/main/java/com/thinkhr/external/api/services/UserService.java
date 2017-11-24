package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
    @Autowired	
    private UserRepository userRepository;
    
    /**
     * Fetch all users from database based on offset, limit and sortField and search criteria
     * 
     * @param Integer offset First record index from database after sorting. Default value is 0
     * @param Integer limit Number of records to be fetched. Default value is 50
     * @param String sortField Field on which records needs to be sorted
     * @param String searchSpec Search string for filtering results
     * @return List<User> object 
     */
    public List<User> getAllUser(Integer offset, Integer limit, String sortField, 
    		String searchSpec, Map<String, String> requestParameters) throws ApplicationException  {
    
    	List<User> users = new ArrayList<User>();

    	Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
    	
    	if(logger.isDebugEnabled()) {
			logger.debug("Request parameters to filter, size and paginate records ");
			if (requestParameters != null) {
				requestParameters.entrySet().stream().forEach(entry -> { logger.debug(entry.getKey() + ":: " + entry.getValue()); });
			}
		}
    	
    	Specification<User> spec = getEntitySearchSpecification(searchSpec, requestParameters, User.class, new User());
    	
    	Page<User> userList  = (Page<User>) userRepository.findAll(spec,pageable);

    	if (userList != null) {
    		userList.getContent().forEach(c -> users.add(c));
    	}
    	
    	//Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, userRepository.count());
    	
    	return users;
    }

    /**
     * Fetch specific user from system
     * @param userId
     * @return User object 
     */
    public User getUser(Integer userId) {
    	return userRepository.findOne(userId);
    }
    
    /**
     * Add a user in system
     * @param User object
     */
    public User addUser(User user)  {
    	User userExistsByUserName = userRepository.findByUserName(user.getUserName());
    	if (userExistsByUserName != null) {
    		throw ApplicationException.createBadRequest(APIErrorCodes.ALREADY_EXISTS, "user", "userName="+user.getUserName());
    	}
    	User userExistsByEmail = userRepository.findByEmail(user.getEmail());
    	if (userExistsByEmail != null) {
    		throw ApplicationException.createBadRequest(APIErrorCodes.ALREADY_EXISTS, "user", "email="+user.getEmail());
    	}
    	return userRepository.save(user);
    }
    
    /**
     * Update a user in database
     * 
     * @param User object
     * @throws ApplicationException 
     */
    public User updateUser(User user) throws ApplicationException  {
    	Integer userId = user.getUserId();
    	
		User userInDB = userRepository.findOne(userId);
		if (null == userInDB) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId="+userId);
    	}
		//If not passed in model, then object will become in-active.
		user.setIsActive(userInDB.getIsActive());
    	return userRepository.save(user);
    }
    
    /**
     * Delete specific user from database
     * 
     * @param userId
     */
    public int deleteUser(int userId) throws ApplicationException {

    	if (null == userRepository.findOne(userId)) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId="+userId);
    	}

    	userRepository.softDelete(userId);

		return userId;
    }    
    
    
    /**
     * Return default sort field for user service
     * 
     * @return String 
     */
    @Override
    public String getDefaultSortField()  {
    	return DEFAULT_SORT_BY_USER_NAME;
    }

}