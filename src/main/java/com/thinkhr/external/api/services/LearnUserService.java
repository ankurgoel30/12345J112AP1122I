package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.BROKER_ROLE;
import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.STUDENT_ROLE;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnRole;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment;

/**
 * Provides a collection of all services related with LearnCompany
 * database object
 * 
 * @since 2017-12-19
 *
 */
@Service
public class LearnUserService extends CommonService {

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

        String roleName = getRoleName(throneUser);

        addUserRoleAssignment(learnUser, roleName);

        learnUser.setUserName(inactiveUserName);

        return this.addLearnUser(learnUser);
    }

    /**
     * Return the role name based on the Company to which throneUser belongs
     * @param throneUser
     * @return
     */
    public String getRoleName(User throneUser) {
        Integer companyId = throneUser.getCompanyId();
        Company throneCompany = companyRepository.findOne(companyId);
        if (throneCompany == null) {
            return null;
        }

        if (throneCompany.isBroker()) {
            return BROKER_ROLE;
        } else {
            return STUDENT_ROLE;
        }
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
     * Saves learnUser for bulk operation
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

    /**
     * Add a role to learn user
     * @param learnUser
     * @return
     */
    public LearnUserRoleAssignment addUserRoleAssignment(LearnUser learnUser, String roleName) {
        LearnUserRoleAssignment userRoleAssignment = new LearnUserRoleAssignment();

        // Set default values // TODO: remove hardcoding from here 
        userRoleAssignment.setContextId(1);
        userRoleAssignment.setModifierId(0);
        userRoleAssignment.setSortOrder(0);
        userRoleAssignment.setItemId(0);
        userRoleAssignment.setComponent("");

        Date now = new Date();
        userRoleAssignment.setTimeModified(now.getTime());

        LearnRole learnRole = learnRoleRepository.findFirstByShortName(roleName);

        if (learnRole == null) {
            //TODO : What to do ?
        }

        userRoleAssignment.setLearnRole(learnRole);

        userRoleAssignment.setLearnUser(learnUser);

        List<LearnUserRoleAssignment> roleAssignments = learnUser.getRoleAssignments();
        if (roleAssignments == null) {
            roleAssignments = new ArrayList<LearnUserRoleAssignment>();
            learnUser.setRoleAssignments(roleAssignments);
        }

        roleAssignments.add(userRoleAssignment);

        return userRoleAssignment;
    }
}


