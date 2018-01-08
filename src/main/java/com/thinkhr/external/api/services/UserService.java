package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.APP_AUTH_DATA;
import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.CONTACT;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_PASSWORD;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.ApplicationConstants.ROLE_ID_FOR_INACTIVE;
import static com.thinkhr.external.api.ApplicationConstants.SPACE;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;
import static com.thinkhr.external.api.ApplicationConstants.USER;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_ACTIVATION_DATE;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_ADDEDBY;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_BROKERID;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_CLIENT_ID;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_PASSWORD;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateAndGetFileContent;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateEmail;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateRequired;
import static com.thinkhr.external.api.services.utils.CommonUtil.getCurrentDateInUTC;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getRequiredHeaders;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getValueFromRow;
import static com.thinkhr.external.api.services.utils.FileImportUtil.populateColumnValues;
import static com.thinkhr.external.api.services.utils.FileImportUtil.validateAndFilterCustomHeaders;

import java.io.IOException;
import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.AppAuthData;
import com.thinkhr.external.api.model.EmailRequest;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.ThroneRoleRepository;
import com.thinkhr.external.api.request.APIRequestHelper;
import com.thinkhr.external.api.services.crypto.AppEncryptorDecryptor;
import com.thinkhr.external.api.services.email.EmailService;
import com.thinkhr.external.api.services.upload.FileUploadEnum;

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
    private EmailService emailService;

    @Autowired
    private AppEncryptorDecryptor encDecyptor;
    
    @Autowired
    private LearnUserService learnUserService;
    
    @Autowired
    protected ThroneRoleRepository throneRoleRepository;

    private static final String resource = USER;
    
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

        Page<User> userList  = userRepository.findAll(spec,pageable);

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
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId = "+ userId);
        }

        return user;
    }

    /**
     * Add a user in system
     * Also adds learnUser
     * 
     * @param User object
     */

    @Transactional
    public User addUser(User user, Integer brokerId) {

        //Saving default password
        user.setPasswordApps(encDecyptor.encrypt(DEFAULT_PASSWORD));

        User throneUser = saveUser(user, brokerId, true);

        learnUserService.addLearnUser(throneUser); //THR-3932

        try {
            // Sending welcome email to user 
            EmailRequest emailRequest = emailService.createEmailRequest(brokerId, throneUser.getUserName());
            emailService.sendEmail(emailRequest);

        } catch (Exception ex) {
            // TODO: Need to understand exact behavior
            logger.error("Error occured while sending email.", ex);
        }

        return throneUser;
    }

    /**
     * Update a user in database
     * 
     * @param User object
     * @throws ApplicationException
     * @throws IOException 
     */
    @Transactional
    public User updateUser(Integer userId, String userJson, Integer brokerId) throws ApplicationException , IOException  {

        User userInDb = userRepository.findOne(userId);
        if (null == userInDb) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId="+userId);
        }

        User updatedUser = update(userJson, userInDb);
        validateObject(updatedUser);

        User throneUser = saveUser(updatedUser, brokerId, false);

        learnUserService.updateLearnUser(throneUser);

        // This is required otherwise values for updatable=false fields is not synced with 
        // database when these fields are passed in payload .
        entityManager.flush();
        entityManager.refresh(throneUser);
        return throneUser;
    }

    /**
     * To save existing users
     * 
     * @param user
     * @param brokerId
     * @param isNew
     * @return
     */
    private User saveUser(User user, Integer brokerId, boolean isNew) {
        validateBrokerId(brokerId);

        Integer roleId = user.getRoleId();
        
        if (roleId != null && roleId != ROLE_ID_FOR_INACTIVE && !validateRoleIdFromDB(roleId)) {
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_ROLE_ID, String.valueOf(roleId));
        }

        if (roleId == ROLE_ID_FOR_INACTIVE) {
            user.setRoleId(null);
        }

        if (isNew) {
            validateCompanyName(user, brokerId);

            //Validate duplicate username and generate a new one
            String userName = generateUserName(user.getUserName(), user.getEmail(), user.getFirstName(),
                    user.getLastName());

            user.setUserName(userName);

            user.setActivationDate(getCurrentDateInUTC());

            user.setAddedBy(getAddedBy(brokerId));
        }
        // If not passed in model, then object will become in-active.
        User throneUser = userRepository.save(user);
        return throneUser;
    }

    /**
     * 
     * @param user
     * @param brokerId
     */
    private String getAddedBy(Integer brokerId) {
        String addedBy = null;
        AppAuthData authData = (AppAuthData) APIRequestHelper.getRequestAttribute(APP_AUTH_DATA);
        if (authData != null) {
            User authUser = userRepository.findByUserName(authData.getUser());
            addedBy = "" + authUser.getUserId();
        } else {
            addedBy = "" + brokerId;
        }

        return addedBy;
    }

    /**
     * Validate companyName
     * 
     * @param user
     * @param brokerId
     * @return
     */
    private Company validateCompanyName(User user, Integer brokerId) {
        Company company = companyRepository.findFirstByCompanyNameAndBroker(user.getCompanyName(),
                brokerId);

        if (company == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_CLIENT_NAME, user.getCompanyName(),
                    String.valueOf(brokerId));
        }
        user.setCompanyId(company.getCompanyId());
        user.setBrokerId(brokerId);

        return company;
    }

    /**
     * Delete specific user from database
     * 
     * @param userId
     */
    public int deleteUser(int userId) throws ApplicationException {
        User throneUser = userRepository.findOne(userId);

        if (null == throneUser) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId="+userId);
        }

        userRepository.softDelete(userId);

        learnUserService.deactivateLearnUser(throneUser);

        return userId;
    }    

    /**
     * Imports a CSV file for companies record
     * 
     * @param fileToImport
     * @param brokerId
     * @throws ApplicationException
     */
    public FileImportResult bulkUpload(MultipartFile fileToImport, int brokerId) throws ApplicationException {
        
        if (fileToImport == null) {
            throw ApplicationException.createFileImportError(APIErrorCodes.REQUIRED_PARAMETER, "file");
        }

        Company broker = validateBrokerId(brokerId);

        List<String> fileContents = validateAndGetFileContent(fileToImport, resource);

        return processRecords (fileContents, broker, resource);

    }

    /**
     * Process imported file to save users records in database
     *  
     * @param records
     * @param brokerId
     * @param resource
     * @throws ApplicationException
     */
     FileImportResult processRecords (List<String> records, 
            Company broker, String resource) throws ApplicationException {

        FileImportResult fileImportResult = new FileImportResult();

        String headerLine = records.get(0);
        records.remove(0);

        fileImportResult.setTotalRecords(records.size());
        fileImportResult.setHeaderLine(headerLine);
        fileImportResult.setBrokerId(broker.getCompanyId());

        String[] headersInCSV = headerLine.split(COMMA_SEPARATOR);

        //DO not assume that CSV file shall contains fixed column position. Let's read and map then with database column
        Map<String, String> headerVsColumnMap = appendRequiredAndCustomHeaderMap(broker.getCompanyId(), resource); 

        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        String[] requiredHeaders = getRequiredHeaders(resource);
        validateAndFilterCustomHeaders(headersInCSV, headerVsColumnMap.values(), requiredHeaders, resourceHandler);

        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }

        for (String record : records ) {
            
            if (StringUtils.containsOnly(record,  new char[]{',',' '})) {
                fileImportResult.increamentBlankRecords();
                continue; //skip any fully blank line 
            }
            
           
           List<String> requiredFields = getRequiredHeadersFromStdFields(CONTACT);
           
           if (!validateRequired(record, requiredFields, headerIndexMap, fileImportResult, resourceHandler)) {
               continue;
           }
           
            String email = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_EMAIL.getHeader()));
           
           if (!validateEmail(record, email, fileImportResult, resourceHandler)) {
               continue;
           }
           
            // Check if user is for valid company
            String clientName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_CLIENT_NAME.getHeader()));
           
           Company company = companyRepository.findFirstByCompanyNameAndBroker(clientName, broker.getCompanyId());
           
           if (company == null) {
               fileImportResult.addFailedRecord(record, 
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.INVALID_CLIENT_NAME, clientName,
                                String.valueOf(broker.getCompanyId())),
                       getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
                continue;
            }
            

            populateAndSaveToDB(record, headerVsColumnMap,
                    headerIndexMap,
                    fileImportResult,
                    company.getCompanyId());

        }

        if (logger.isDebugEnabled()) {
            logger.debug(fileImportResult.toString());
        }

        return fileImportResult;
    }

    /**
     * Populate values to columns and insert record into DB
     * 
     * @param record
     * @param userHeaderColumnMap
     * @param locationFileHeaderColumnMap
     * @param headerIndexMap
     * @param fileImportResult
     * @param companyId
     */
    public void populateAndSaveToDB(String record, 
            Map<String, String> userHeaderColumnMap, 
            Map<String, Integer> headerIndexMap,
            FileImportResult fileImportResult, 
            int companyId) {

        List<Object> userColumnValues = null;

        try {
            // Populate companyColumnsValues from split record
            userColumnValues = populateColumnValues(record, 
                    userHeaderColumnMap,
                    headerIndexMap);

        } catch (ArrayIndexOutOfBoundsException ex) {
            fileImportResult.addFailedRecord(record, 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.MISSING_FIELDS), 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return;
        }

        try {

            //Finally save users one by one
            List<String> userColumnsToInsert = new ArrayList<String>(userHeaderColumnMap.keySet());
            userColumnValues.add(companyId);
            userColumnValues.add(encDecyptor.encrypt(DEFAULT_PASSWORD));
            userColumnValues.add(getCurrentDateInUTC());
            userColumnValues.add(getAddedBy(companyId)); // TODO: Fix : Instead of companyId brokerId should go here
            userColumnValues.add(fileImportResult.getBrokerId());
            
            userColumnsToInsert.add(USER_COLUMN_CLIENT_ID);
            userColumnsToInsert.add(USER_COLUMN_PASSWORD);
            userColumnsToInsert.add(USER_COLUMN_ACTIVATION_DATE);
            userColumnsToInsert.add(USER_COLUMN_ADDEDBY);
            userColumnsToInsert.add(USER_COLUMN_BROKERID);

            // THR-3927 [Start]
            String userName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_USER_NAME.getHeader()));
            String lastName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_LAST_NAME.getHeader()));
            String firstName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_FIRST_NAME.getHeader()));
            String email = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_EMAIL.getHeader()));

            userName = generateUserName(userName, email, firstName, lastName);

            // Get Index of UserName field in userColumnsToInsert and replace the username  
            // value in userColumnValues with the generated user name
            int userNameColumnIndex = userColumnsToInsert.indexOf(FileUploadEnum.USER_USER_NAME.getColumn());
            if (userNameColumnIndex != -1) {
                userColumnValues.set(userNameColumnIndex, userName);
            }
            // THR-3927 [End]

            saveUserRecord(userColumnValues, userColumnsToInsert);

            fileImportResult.increamentSuccessRecords();
        } catch (Exception ex) {
            String cause = ex.getCause() instanceof DataTruncation ? 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DATA_TRUNCTATION) :
                        ex.getMessage();
                    fileImportResult.addFailedRecord(record, cause,
                            getMessageFromResourceBundle(resourceHandler, APIErrorCodes.RECORD_NOT_ADDED));
        }

    }

    /**
     * Saves User and LearnUser record
     * @param userColumnValues
     * @param userColumnsToInsert
     */
    @Transactional
    public void saveUserRecord(List<Object> userColumnValues, List<String> userColumnsToInsert) {
        Integer userId = fileDataRepository.saveUserRecord(userColumnsToInsert, userColumnValues);
        User throneUser = this.getUser(userId);

        try {
            learnUserService.addLearnUserForBulk(throneUser);
        } catch (Exception ex) {
            // TODO: FIXME - Ideally this should handled by transaction roll-back; some-reason transaction is not working 
            // Need some research on it. To manage records properly, explicitly roll-back record. 
            userRepository.delete(userId);
            throw ex;
        }

    }

    /**
     * Check for duplicate username in database
     * @param username
     * @return
     */
    public boolean checkDuplicate(String username) {
        if (username == null) {
            return false;
        }
        if (userRepository.findByUserName(username) != null) {
            return true;
        }
        return false;
    }

    /**
     * Logic to generate username from email, firstName and lastName if it is
     * blank or duplicate
     * 
     * JIRA = THR-3927
     * 
     * @param userName
     * @param email
     * @param firstName
     * @param lastName
     * @return
     */
    protected String generateUserName(String userName, String email, String firstName, String lastName) {
        if (StringUtils.isBlank(userName)) {
            userName = email;
        }

        int i = 1;
        while (true) {
            if (!StringUtils.isBlank(userName) && !checkDuplicate(userName)) { // Not blank and not duplicate
                break;
            }
            firstName = firstName != null ? firstName.replace(SPACE, UNDERSCORE) : null;
            lastName = lastName != null ? lastName.replace(SPACE, UNDERSCORE) : null;
            userName = firstName + UNDERSCORE + lastName + UNDERSCORE + i;
            i = i + 1;
        }

        return userName;
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

    /**
     * Validates roleId from the Database.
     * 
     * @param roleId
     * @return
     */
    public boolean validateRoleIdFromDB(Integer roleId) {
        return throneRoleRepository.findOne(roleId) == null ? false : true;
    }


}