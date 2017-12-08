package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.ApplicationConstants.CONTACT;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_CLIENT_ID;
import static com.thinkhr.external.api.ApplicationConstants.USER_COLUMN_PASSWORD;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_PASSWORD;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateAndGetFileContent;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateEmail;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateRequired;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getRequiredHeaders;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getValueFromRow;
import static com.thinkhr.external.api.services.utils.FileImportUtil.populateColumnValues;
import static com.thinkhr.external.api.services.utils.FileImportUtil.validateAndFilterCustomHeaders;

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
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.services.crypto.AppEncryptorDecryptor;
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
    private UserRepository userRepository;
    
    @Autowired
    private AppEncryptorDecryptor encDecyptor;
    
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
     * @param User object
     */
    public User addUser(User user)  {
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

        if (null == userRepository.findOne(userId)) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "user", "userId="+userId);
        }
        //If not passed in model, then object will become in-active.
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

        Company broker = validateAndGetBroker(brokerId);

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
    private FileImportResult processRecords (List<String> records, 
            Company broker, String resource) throws ApplicationException {

        FileImportResult fileImportResult = new FileImportResult();

        String headerLine = records.get(0);
        records.remove(0);

        fileImportResult.setTotalRecords(records.size());
        fileImportResult.setHeaderLine(headerLine);

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
            
            String userName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.USER_USER_NAME.getHeader()));
           
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
           
            //Check to validate duplicate record
            if (checkDuplicate(record, userName, fileImportResult)) {
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

            //Finally save companies one by one
            List<String> userColumnsToInsert = new ArrayList<String>(userHeaderColumnMap.keySet());
            userColumnValues.add(companyId);
            userColumnValues.add(encDecyptor.encrypt(DEFAULT_PASSWORD));
            
            userColumnsToInsert.add(USER_COLUMN_CLIENT_ID);
            userColumnsToInsert.add(USER_COLUMN_PASSWORD);

            fileDataRepository.saveUserRecord(userColumnsToInsert, userColumnValues);

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
     * Check validate username is duplicate or not
     *  
     * @param record
     * @param username
     * @param fileImportResult
     * @return
     */
    public boolean checkDuplicate(String record, String username, 
            FileImportResult fileImportResult) {

        if (username == null) {
            return false;
        }
        if (userRepository.findByUserName(username) != null) {
            String causeDuplicateName = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DUPLICATE_USER_RECORD, username);
            fileImportResult.addFailedRecord(record, causeDuplicateName,
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return true;
        }

        return false;
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