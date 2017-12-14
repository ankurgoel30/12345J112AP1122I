package com.thinkhr.external.api.services;

import org.springframework.stereotype.Service;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.AppAuthData;

/**
 * To handle services specific to Authorization of application and user's data
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */
@Service
public class AuthorizationManager extends CommonService {

    /**
     * Purpose of this method to cover all the required authorization related stuff. 
     * 
     * @return
     */
    public boolean checkAuthorization(AppAuthData authData) {
        Integer brokerId = authData.getBrokerId();

        //Validate brokerId
        if (null == brokerId || null == companyRepository.findOne(brokerId)) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, "brokerId = "+ String.valueOf(brokerId));
        }
        //Validate user
        if (null == authData.getUser() || null == userRepository.findByUserName(authData.getUser())){
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, "user = "+ String.valueOf(authData.getUser()));
        }
        //Validate roles
        
        //TODO: In future we have additional/revised requirement we need to made change at this layer only.
        
        return true;
    }
}
