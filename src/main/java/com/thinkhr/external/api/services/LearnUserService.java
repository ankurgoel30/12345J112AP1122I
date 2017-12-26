package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.BROKER_ROLE;
import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.ROLE_ID_FOR_INACTIVE;
import static com.thinkhr.external.api.ApplicationConstants.STUDENT_ROLE;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;
import static com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment.DEFAULT_COMPONENT;
import static com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment.DEFAULT_CONTEXT_ID;
import static com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment.DEFAULT_ITEM_ID;
import static com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment.DEFAULT_MODIFIER_ID;
import static com.thinkhr.external.api.db.learn.entities.LearnUserRoleAssignment.DEFAULT_SORT_ORDER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 
     * THR-3932
     * @param learnUser
     * @return
     */
    public LearnUser addLearnUser(LearnUser learnUser) {
        return learnUserRepository.save(learnUser);
    }

    /**
     * Create a learnUser from throneUser and add it to database
     * 
     * THR-3932
     * @param throneUser
     * @return
     */
    public LearnUser addLearnUser(User throneUser) {

        LearnUser learnUser = modelConvertor.convert(throneUser);

        learnUser.setUserName(getLearnUserNameByRoleId(throneUser));

        String roleName = getRoleName(throneUser);

        addUserRoleAssignment(learnUser, roleName);

        return this.addLearnUser(learnUser);
    }

    /**
     * To update learn user
     * 
     * @param learnUser
     * @return
     */
    public LearnUser updateLearnUser(LearnUser learnUser) {

        Long learnUserId = learnUser.getId();

        if (null == learnUserRepository.findOne(learnUserId)) {
            // TODO : Find what to  do ?
        }

        return learnUserRepository.save(learnUser);
    }

    /**
     * Update a learnUser from throneUser and add it to database
     * 
     * @param throneUser
     * @return
     */
    public LearnUser updateLearnUser(User throneUser) {

        LearnUser learnUser = learnUserRepository.findFirstByThrUserId(throneUser.getUserId());

        if (learnUser == null) {
            // TODO : what to do ?
        }

        learnUser.setUserName(getLearnUserNameByRoleId(throneUser));

        return this.updateLearnUser(learnUser);
    }

    /**
     * Return the role name based on the Company to which throneUser belongs
     * 
     * THR-3932
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
     * THR-3932
     * @param userName
     * @param companyId
     * @param brokerId
     * @return
     */
    public static String generateUserNameForInactive(String userName, Integer companyId, Integer brokerId) {
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
     * 
     * THR-3932
     * @param throneUser
     * @return
     */
    public Integer addLearnUserForBulk(User throneUser) {

        List<String> learnUserColumns = new ArrayList<String>(Arrays.asList(
                "thrcontactid", "username", "password",
                "firstname", "lastname", "email", "phone1", "companyid"));

        List<Object> learnUserColumnValues = new ArrayList<Object>(Arrays.asList(
                throneUser.getUserId(),
                getLearnUserNameByRoleId(throneUser),
                throneUser.getPassword(),
                throneUser.getFirstName(),
                throneUser.getLastName(),
                throneUser.getEmail(),
                throneUser.getPhone(),
                throneUser.getCompanyId()
        ));

        String roleName = getRoleName(throneUser);
        LearnRole learnRole = learnRoleRepository.findFirstByShortName(roleName);
        Integer roleId = null;
        if (learnRole != null) {
            roleId = learnRole.getId();
        }

        return learnFileDataRepository.saveLearnUserRecord(learnUserColumns, learnUserColumnValues, roleId);

    }

    /**
     * Add a role to learn user
     * 
     * THR-3932
     * @param learnUser
     * @return
     */
    public LearnUserRoleAssignment addUserRoleAssignment(LearnUser learnUser, String roleName) {
        if (learnUser == null) {
            return null;
        }

        LearnUserRoleAssignment userRoleAssignment = new LearnUserRoleAssignment();

        // Set default values 
        userRoleAssignment.setContextId(DEFAULT_CONTEXT_ID);
        userRoleAssignment.setModifierId(DEFAULT_MODIFIER_ID);
        userRoleAssignment.setSortOrder(DEFAULT_SORT_ORDER);
        userRoleAssignment.setItemId(DEFAULT_ITEM_ID);
        userRoleAssignment.setComponent(DEFAULT_COMPONENT);

        Date now = new Date();
        userRoleAssignment.setTimeModified(now.getTime());

        LearnRole learnRole = learnRoleRepository.findFirstByShortName(roleName);

        if (learnRole == null) {
            return null;
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

    /**
     * Returns true if LearnUser corresponding to throneUser is deactivated successfully
     * THR-3932
     * @param throneUser
     * @return
     */
    public boolean deactivateLearnUser(User throneUser) {
        if (throneUser == null) {
            return false;
        }

        Integer thrUserId = throneUser.getUserId();

        LearnUser learnUser = learnUserRepository.findFirstByThrUserId(thrUserId);
        if (learnUser == null) {
            // Skip this as no learnUser exists corresponding to throneUser
            return true;
        }

        String inactiveUserName = generateUserNameForInactive(throneUser.getUserName(), throneUser.getCompanyId(),
                throneUser.getBrokerId());

        learnUser.setUserName(inactiveUserName);

        learnUserRepository.save(learnUser);

        return true;
    }

    /**
     * Returns true if learnUser corresponding to throneUser is activated successfully
     * THR-3932
     * @param throneUser
     * @return
     */
    public boolean activateLearnUser(User throneUser) {
        if (throneUser == null) {
            return false;
        }

        Integer thrUserId = throneUser.getUserId();
        LearnUser learnUser = learnUserRepository.findFirstByThrUserId(thrUserId);
        if (learnUser == null) {
            // Skip this as no learnUser exists corresponding to throneUser
            return true;
        }

        learnUser.setUserName(throneUser.getUserName());
        learnUserRepository.save(learnUser);
        return true;
    }

    /**
     * Deactivate all LearnUsers corresponding to given throneCompany
     * THR-3932
     * @param throneCompany
     */
    @Transactional
    public void deactivateAllLearnUsers(Company throneCompany) {
        if (throneCompany == null) {
            return;
        }

        Integer companyId = throneCompany.getCompanyId();
        List<User> throneUsers = userRepository.findByCompanyId(companyId);

        if (throneUsers == null) {
            return;
        }

        throneUsers.stream().forEach(user -> deactivateLearnUser(user));
    }

    /**
     * Activate all LearnUsers corresponding to given throneCompany
     * THR-3932
     * @param throneCompany
     */
    @Transactional
    public void activateAllLearnUsers(Company throneCompany) {
        if (throneCompany == null) {
            return;
        }

        Integer companyId = throneCompany.getCompanyId();
        List<User> throneUsers = userRepository.findByCompanyId(companyId);

        if (throneUsers == null) {
            return;
        }

        throneUsers.stream().forEach(user -> activateLearnUser(user));
    }

    /**
     * 
     * @param throneUser
     * @return
     */
    public static String getLearnUserNameByRoleId(User throneUser) {
        String learnUserName = null;
        if (throneUser.getRoleId() != null && throneUser.getRoleId() != ROLE_ID_FOR_INACTIVE) {
            learnUserName = throneUser.getUserName(); // Active user 
        } else {
            learnUserName = generateUserNameForInactive(throneUser.getUserName(), throneUser.getCompanyId(),
                    throneUser.getBrokerId()); // Inactive User
        }
        return learnUserName;
    }
}


