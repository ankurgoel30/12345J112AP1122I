package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_USER_NAME;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateAndGetFileContent;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
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

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.UserRepository;
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
    
    private static final String resource = "user";
    
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

        Company broker = validateAndGetBroker(brokerId);

        List<String> fileContents = validateAndGetFileContent(fileToImport, resource);

        return processRecords (fileContents, broker, resource);

    }

    /**
     * Process imported file to save companies records in database
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
        Map<String, String> headerVsColumnMap = getCompanyColumnHeaderMap(broker.getCompanyId(), resource); 


        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        validateAndFilterCustomHeaders(headersInCSV, headerVsColumnMap.values(), resourceHandler);

        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }

        int recCount = 0;

        for (String record : records ) {
            
            if (StringUtils.containsOnly(record,  new char[]{',',' '})) {
                fileImportResult.increamentBlankRecords();
                continue; //skip any fully blank line 
            }
          
            //Check to validate duplicate record
            if (checkDuplicate(recCount, record, fileImportResult, broker.getCompanyId())) {
                continue;
            }

        }

        logger.debug("Total Number of Records: " + fileImportResult.getTotalRecords());
        logger.debug("Total Number of Successful Records: " + fileImportResult.getNumSuccessRecords());
        logger.debug("Total Number of Failure Records: " + fileImportResult.getNumFailedRecords());
        logger.debug("Total Number of Blank Records: " + fileImportResult.getNumBlankRecords());
        
        if (fileImportResult.getNumFailedRecords() > 0) {
            logger.debug("List of Failure Records");
            for (FileImportResult.FailedRecord failedRecord : fileImportResult.getFailedRecords()) {
                logger.debug(failedRecord.getRecord() + COMMA_SEPARATOR + failedRecord.getFailureCause());
            }
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
     * @param recCount
     */
    public void populateAndSaveToDB(String record, 
            Map<String, String> userHeaderColumnMap, 
            Map<String, Integer> headerIndexMap,
            FileImportResult fileImportResult, 
            int recCount) {

        List<Object> userColumnValues = null;

        try {
            // Populate companyColumnsValues from split record
            userColumnValues = populateColumnValues(record, 
                    userHeaderColumnMap,
                    headerIndexMap);

        } catch (ArrayIndexOutOfBoundsException ex) {
            fileImportResult.addFailedRecord(recCount++ , record, 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.MISSING_FIELDS), 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return;
        }

        try {

            //Finally save companies one by one
            List<String> userColumnsToInsert = new ArrayList<String>(userHeaderColumnMap.keySet());

            fileDataRepository.saveUserRecord(userColumnsToInsert, userColumnValues);

            fileImportResult.increamentSuccessRecords();
        } catch (Exception ex) {
            String cause = ex.getCause() instanceof DataTruncation ? 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DATA_TRUNCTATION) :
                        ex.getMessage();
                    fileImportResult.addFailedRecord(recCount++ , record, cause,
                            getMessageFromResourceBundle(resourceHandler, APIErrorCodes.RECORD_NOT_ADDED));
        }

    }
    /**
     * TODO: Logic to decide record is duplicate
     * 
     * @param companyName
     * @param custom1Value
     * @param recCount
     * @param record
     * @param fileImportResult
     * @return
     */
    public boolean checkDuplicate(int recCount, String record,
            FileImportResult fileImportResult, Integer brokerId) {

        String[] rowColValues = record.split(COMMA_SEPARATOR);

        String companyName = rowColValues[0].trim(); //TODO Fix this hardcoding.

        String custom1Value = null; 

        if (rowColValues.length > 11) {
            custom1Value = rowColValues[11].trim();
        }

        boolean isDuplicate = false;

        boolean isSpecial = (brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER1) ||
                             brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER2)); 
        
        //find matching company by given company name and broker id
        Company companyFromDB = companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId);

        if (null != companyFromDB) { //A DB query is must here to check duplicates in data
            if (!isSpecial) {
                isDuplicate = true;
            }
            //handle special case of Paychex
          //find matching company by given company name, custom1 field and broker id
            if (isSpecial && companyRepository.findFirstByCompanyNameAndCustom1AndBroker(companyName, custom1Value, brokerId) != null) {  
                isDuplicate = true;
            }
            if (isDuplicate) {
                String causeDuplicateName = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DUPLICATE_RECORD);
                causeDuplicateName = (!isSpecial ? causeDuplicateName + " - " + companyName : 
                    causeDuplicateName + " - " + companyName + ", " + custom1Value);
                fileImportResult.addFailedRecord(recCount++ , record, causeDuplicateName,
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            } 
        }

        return isDuplicate;
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