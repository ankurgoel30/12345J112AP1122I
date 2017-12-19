package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnFileDataRepository;
import com.thinkhr.external.api.learn.repositories.LearnUserRepository;

/**
 * Provides a collection of all services related with LearnCompany
 * database object
 * 
 * @since 2017-12-19
 *
 */
@Service
public class LearnUserService {
    @Autowired
    LearnUserRepository learnUserRepository;

    @Autowired
    LearnFileDataRepository learnFileDataRepository;


    @Autowired
    protected ModelConvertor modelConvertor;

    /**
     * Save learnUser to database
     * @param learnUser
     * @return
     */
    public LearnUser addLearnUser(LearnUser learnUser) {
        return learnUserRepository.save(learnUser);
    }

    /**
     * Create a learnUser from throneUser and add it to database
     * @param throneUser
     * @return
     */
    public LearnUser addLearnUser(User throneUser) {

        LearnUser learnUser = modelConvertor.convert(throneUser);

        String inactiveUserName = generateUserNameForInactive(throneUser.getUserName(), throneUser.getCompanyId(),
                throneUser.getBrokerId());

        learnUser.setUserName(inactiveUserName);

        return this.addLearnUser(learnUser);
    }

    /**
     * Generate user name to make user inactive
     * @param userName
     * @param companyId
     * @param brokerId
     * @return
     */
    private String generateUserNameForInactive(String userName, Integer companyId, Integer brokerId) {
        return new StringBuffer(userName)
                .append(INACT)
                .append(UNDERSCORE)
                .append(companyId)
                .append(UNDERSCORE)
                .append(brokerId)
                .toString();
    }

    /**
     * Saves learnuser for bulk operation
     * @param throneUser
     * @return
     */
    public Integer addLearnUserForBulk(User throneUser) {

        List<String> learnUserColumns = new ArrayList<String>(Arrays.asList(
                "thrcontactid", "username", "password",
                "firstname", "lastname", "email", "phone1", "companyid"));

        String inactiveUserName = generateUserNameForInactive(throneUser.getUserName(), throneUser.getCompanyId(),
                throneUser.getBrokerId());

        List<Object> learnUserColumnValues = new ArrayList<Object>(Arrays.asList(
                throneUser.getUserId(),
                inactiveUserName,
                throneUser.getPassword(),
                throneUser.getFirstName(),
                throneUser.getLastName(),
                throneUser.getEmail(),
                throneUser.getPhone(),
                throneUser.getCompanyId()

        ));

        return learnFileDataRepository.saveLearnUserRecord(learnUserColumns, learnUserColumnValues);

    }
}


