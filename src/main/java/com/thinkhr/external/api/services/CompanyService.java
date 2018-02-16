package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.CLIENT;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY_COLUMN_ADDEDBY;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY_COLUMN_BROKER;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY_CUSTOM_HEADER1;
import static com.thinkhr.external.api.ApplicationConstants.CONFIGURATION_ID_FOR_INACTIVE;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.ApplicationConstants.HASH_KEY;
import static com.thinkhr.external.api.ApplicationConstants.LOCATION;
import static com.thinkhr.external.api.ApplicationConstants.SUCCESS_DELETED;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.exception.APIExceptionHandler.extractMessageFromException;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateAndGetFileContent;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validatePhone;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateRequired;
import static com.thinkhr.external.api.services.utils.CommonUtil.getTempId;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getValueFromRow;
import static com.thinkhr.external.api.services.utils.FileImportUtil.populateColumnValues;
import static com.thinkhr.external.api.services.utils.FileImportUtil.setRequestParamsForBulkJsonResponse;
import static com.thinkhr.external.api.services.utils.FileImportUtil.validateAndGetContentFromModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hashids.Hashids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.BulkJsonModel;
import com.thinkhr.external.api.model.CsvModel;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.response.APIResponse;
import com.thinkhr.external.api.services.upload.FileUploadEnum;
import com.thinkhr.external.api.services.utils.CommonUtil;

/**
 *
 * Provides a collection of all services related with Company
 * database object

 * @author Surabhi Bhawsar
 * @Since 2017-11-04
 *
 * 
 */

@Service
public class CompanyService  extends ImportService {

    @Autowired
    protected LearnCompanyService learnCompanyService;
    
    @Autowired
    protected UserService userService;

    private Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private static final String resource = COMPANY;

    /**
     *
     * To fetch companies records. Based on given parameters companies records will be filtered out.
     * 
     * @param Integer offset First record index from database after sorting. Default value is 0
     * @param Integer limit Number of records to be fetched. Default value is 50
     * @param String sortField Field on which records needs to be sorted
     * @param String searchSpec Search string for filtering results
     * @param Map<String, String>
     * @return List<Company> object 
     * @throws ApplicationException 
     * 
     */
    public List<Company> getAllCompany(Integer offset, 
            Integer limit,
            String sortField, 
            String searchSpec, 
            Map<String, String> requestParameters) throws ApplicationException {

        List<Company> companies = new ArrayList<Company>();

        Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());

        if(logger.isDebugEnabled()) {
            logger.debug("Request parameters to filter, size and paginate records ");
            if (requestParameters != null) {
                requestParameters.entrySet().stream().forEach(entry -> { logger.debug(entry.getKey() + ":: " + entry.getValue()); });
            }
        }
        
        if (requestParameters == null) {
            requestParameters = new HashMap<String, String>();
        }
        
        if (requestParameters.get("companyStatus") == null) {
            requestParameters.put("companyStatus", "1");
        }

        Specification<Company> spec = getEntitySearchSpecification(searchSpec, requestParameters, Company.class, new Company());

        Page<Company> companyList  = companyRepository.findAll(spec, pageable);

        if (companyList != null) {
            companyList.getContent().forEach(c -> companies.add(c));
        }

        //Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, companyRepository.count(spec));

        return companies;
    }


    /**
     * Fetch specific company from database
     * 
     * @param companyId
     * @return Company object 
     */
    public Company getCompany(Integer companyId) {
        Company company =  companyRepository.findOne(companyId);

        if (null == company) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND,
                    "company", "companyId="+ companyId);
        }

        return company;
    }

    /**
     * Add a company in database
     * 
     * @param company object
     */
    @Transactional
    public Company addCompany(Company company, Integer brokerId) {
        company.setTempID(getTempId());

        Company throneCompany = saveCompany(company, brokerId, true);
        if (brokerId == null) { //i.e. 
            throneCompany.setBroker(throneCompany.getCompanyId());
        }
        learnCompanyService.addLearnCompany(throneCompany);// THR-3929 

        return throneCompany;
    }

    /**
     * Validates configurationId from the Database.
     * 
     * @param configurationId
     * @return
     */
    public boolean validateConfigurationIdFromDB(Integer configurationId, Integer brokerId) {
        return configurationRepository.findFirstByConfigurationIdAndCompanyId(configurationId, brokerId) == null ? 
                 false : true;
    }

     /**
     * Make a link in child entity with parent entity
     * 
     * @param company
     */
    private void associateChildEntities(Company company) {
        List<Location> locations = company.getLocation();
        locations.stream().forEach(loc -> {
            if (loc != null && loc.getCompany() == null) {
                loc.setCompany(company);

                // setting tempID for location
                loc.setTempID(CommonUtil.getTempId());
            }
        });
    }

    /**
     * Update a company in database
     * 
     * @param companyId
     * @param companyJson
     * @return
     * @throws ApplicationException
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    @Transactional
    public Company updateCompany(Integer companyId, String companyJson, Integer brokerId) 
            throws ApplicationException, JsonProcessingException, IOException {

        Company companyInDb = companyRepository.findOne(companyId);
        if (null == companyInDb) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
        }
        
        return updateCompany(companyJson, brokerId, companyInDb);
    }


    /**
     * @param companyJson
     * @param brokerId
     * @param companyInDb
     * @return
     * @throws IOException
     */
    @Transactional
    protected Company updateCompany(String companyJson, Integer brokerId,
            Company companyInDb) throws IOException {
        Company updatedCompany = update(companyJson, companyInDb);

        if (updatedCompany.getLocation() != null) {
            List<Location> locations = updatedCompany.getLocation();
            locations.stream().forEach(loc -> {
                loc.setCompany(null);// This is required before doing validation otherwise validation is cascaded recursively and gives stackoverflow error
            });
            //updatedCompany.getLocation().setCompany(null); // This is required before doing validation otherwise validation is cascaded recursively and gives stackoverflow error
        }
        validateObject(updatedCompany);

        Company throneCompany = saveCompany(updatedCompany, brokerId, false);
        learnCompanyService.updateLearnCompany(throneCompany);

        // This is required otherwise values for updatable=false fields is not synced with 
        // database when these fields are passed in payload .
        entityManager.flush();
        entityManager.refresh(throneCompany);
        return throneCompany;
    }

    /**
     * To save company object
     * 
     * @param company
     * @param brokerId
     * @param isNew
     * @return
     */
    private Company saveCompany(Company company, Integer brokerId, boolean isNew) {
        validateBrokerId(brokerId);

        // setting valid brokerId for company. 
        company.setBroker(brokerId);

        associateChildEntities(company);

        // Checking Duplicate company name
        if (isNew) {
            if (isDuplicateCompany(company)) {
                throw ApplicationException.createBadRequest(APIErrorCodes.DUPLICATE_COMPANY_RECORD,
                        company.getCompanyName());
            }

            company.setAddedBy(getAddedBy(brokerId));
        }

        Integer configurationId = company.getConfigurationId();

        if (configurationId != null && configurationId != CONFIGURATION_ID_FOR_INACTIVE
                && !validateConfigurationIdFromDB(configurationId, brokerId)) {

            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_CONFIGURATION_ID,
                    String.valueOf(configurationId), String.valueOf(brokerId));
        }

        if (configurationId == CONFIGURATION_ID_FOR_INACTIVE) {
            company.setConfigurationId(null);
        }

        Company throneCompany = companyRepository.save(company);
        
        return throneCompany;
    }

    /**
     * To check whether company name already exists in DB.
     * 
     * @param companyName
     * @param brokerId
     * @param custom1
     * @return
     */
    public boolean isDuplicateCompany(Company company) {

        boolean isDuplicate = false;

        boolean isSpecial = (company.getBroker().equals(ApplicationConstants.SPECIAL_CASE_BROKER1) ||
                company.getBroker().equals(ApplicationConstants.SPECIAL_CASE_BROKER2)); 
        
        //find matching company by given company name and broker id
        Company companyFromDB = companyRepository.findFirstByCompanyNameAndBroker(company.getCompanyName(),
                company.getBroker());

        if (null != companyFromDB) { //A DB query is must here to check duplicates in data
            if (!isSpecial) {
                isDuplicate = true;
            }
            // handle special case of Paychex
            // find matching company by given company name, custom1 field and broker id
            if (isSpecial && companyRepository.findFirstByCompanyNameAndCustom1AndBroker(company.getCompanyName(),
                    company.getCustom1(), company.getBroker()) != null) {
                isDuplicate = true;
            }
        }
        return isDuplicate;
    }

    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    public int deleteCompany(int companyId) throws ApplicationException {
        Company company = companyRepository.findOne(companyId);

        if (null == company) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
        }

        return deleteCompany(companyId, company);
    }


    /**
     * Delete company
     * @param companyId
     * @param company
     * @return
     */
    protected int deleteCompany(int companyId, Company company) {
        companyRepository.softDelete(companyId);
        
        // Deleting users associated with companyId
        List<Integer> throneUsers = userRepository.findAllUsersByCompanyId(companyId);
        throneUsers.stream().forEach(userId -> userService.deleteUser(userId));

        learnCompanyService.deactivateLearnCompany(company);

        return companyId;
    }     

    /**
     * Imports a CSV file with companies record or a BulkJsonModel objects
     * 
     * @param fileToImport
     * @param companies 
     * @param brokerId
     * @throws ApplicationException
     */
    public FileImportResult bulkUpload(MultipartFile fileToImport, List<BulkJsonModel> companies, int brokerId) throws ApplicationException {
        
        if (fileToImport == null && CollectionUtils.isEmpty(companies)) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.REQUIRED_PARAMETER, "file Or CompanyJsonBody");
        }

        Company broker = validateBrokerId(brokerId);
        
        List<String> fileContents = null;

        if (null != fileToImport) {
        	fileContents = validateAndGetFileContent(fileToImport, resource, maxRecordsCompanyImport);
        } else {
        	fileContents = validateAndGetContentFromModel(companies, resource, maxRecordsCompanyImport);
        }
        
        if (fileContents == null) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, null);
        }

        if (broker == null || broker.getCompanyId() == null) {
            throw ApplicationException.createBulkImportError(APIErrorCodes.INVALID_BROKER_ID, "null");
        }
        
        CsvModel csvModel = new CsvModel();
        csvModel.initialize(fileContents, broker.getCompanyId());
        
        Map<String, Map<String, String>> combinedHashMap = new HashMap<String, Map<String,String>>();
        
        //DO not assume that CSV file contains fixed column position. Let's read and map then with database column
        Map<String, String> companyFileHeaderMap = appendRequiredAndCustomHeaderMap(broker.getCompanyId(), resource);
        Map<String, String> locationFileHeaderColumnMap = FileUploadEnum.prepareColumnHeaderMap(LOCATION);
        combinedHashMap.put(COMPANY, companyFileHeaderMap);
        combinedHashMap.put(LOCATION, locationFileHeaderColumnMap);
        
        csvModel.setHeaderVsColumnMap(combinedHashMap);
        
        FileImportResult fileImportResult = processCsvModel(resource, csvModel);
        
        if(!CollectionUtils.isEmpty(companies)){
            setRequestParamsForBulkJsonResponse(fileImportResult);
        }
        
        return fileImportResult;

    }


    /**
     * Populate values to columns and insert record into DB
     * 
     * @param record
     * @param companyFileHeaderColumnMap
     * @param locationFileHeaderColumnMap
     * @param headerIndexMap
     * @param fileImportResult
     * @param recCount
     */
    @Transactional
    public void populateAndSaveToDB(String record, 
            Map<String, String> companyFileHeaderColumnMap, 
            Map<String, String> locationFileHeaderColumnMap, 
            Map<String, Integer> headerIndexMap,
            FileImportResult fileImportResult) {

        List<Object> companyColumnsValues = null;
        List<Object> locationColumnsValues = null;

        try {
            // Populate companyColumnsValues from split record
            companyColumnsValues = populateColumnValues(record, 
                    companyFileHeaderColumnMap,
                    headerIndexMap);

            // Populate locationColumnsValues from split record
            locationColumnsValues = populateColumnValues(record, 
                    locationFileHeaderColumnMap,
                    headerIndexMap);
            

        } catch (ArrayIndexOutOfBoundsException ex) {
            fileImportResult.addFailedRecord(record, 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.MISSING_FIELDS), 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return;
        }

        try {
            //Finally save companies one by one
            List<String> companyColumnsToInsert = new ArrayList<String>(companyFileHeaderColumnMap.keySet());
            List<String> locationColumnsToInsert = new ArrayList<String>(locationFileHeaderColumnMap.keySet());
            companyColumnsToInsert.add(COMPANY_COLUMN_BROKER);
            companyColumnsToInsert.add(COMPANY_COLUMN_ADDEDBY);

            companyColumnsValues.add(fileImportResult.getBrokerId());
            companyColumnsValues.add(fileImportResult.getJobId());

            saveCompanyRecord(companyColumnsValues, locationColumnsValues,
                    companyColumnsToInsert, locationColumnsToInsert);

            fileImportResult.increamentSuccessRecords();
        } catch (Exception ex) {
            String cause = extractMessageFromException(ex, resourceHandler);

            fileImportResult.addFailedRecord(record, cause,
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.RECORD_NOT_ADDED));
        }

    }

    /**
     * Get authorizationKey from companyId for CompanyProduct entity on the
     * basis of Hashids.
     * 
     * @param companyId
     * @return
     */
    public static String getAuthorizationKeyFromCompanyId(Integer companyId) {
        Hashids hashids = new Hashids(HASH_KEY);
        String authorizationKey = hashids.encode(companyId);
        return authorizationKey;
    }

    /**
     * To persist throne and learn record to-gether.
     * 
     * @param companyColumnsValues
     * @param locationColumnsValues
     * @param companyColumnsToInsert
     * @param locationColumnsToInsert
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveCompanyRecord(List<Object> companyColumnsValues,
            List<Object> locationColumnsValues,
            List<String> companyColumnsToInsert,
            List<String> locationColumnsToInsert) {
        
        Integer companyId = fileDataRepository.saveCompanyRecord(companyColumnsToInsert, companyColumnsValues,
                locationColumnsToInsert,
                locationColumnsValues);

        Company throneCompany = this.getCompany(companyId);

        try {
            learnCompanyService.addLearnCompanyForBulk(throneCompany);
        } catch (Exception ex) {
            // TODO: FIXME - Ideally this should handled by transaction roll-back; some-reason transaction is not working with combination of jdbcTemplate and spring
            // data. Need some research on it. To manage records properly, explicitly roll-back record. 
            companyRepository.delete(companyId);
            throw ex;
        }

    }
    
    /**
     * TODO: Logic to decide record is duplicate
     * 
     * @param record
     * @param fileImportResult
     * @param brokerId
     * @param headerIndexMap
     * @return
     */
    public boolean checkDuplicate(String record,
            FileImportResult fileImportResult, Integer brokerId, Map<String, Integer> headerIndexMap) {

        String companyName = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.COMPANY_NAME.getHeader()));

        String custom1Value = getValueFromRow(record, headerIndexMap.get(COMPANY_CUSTOM_HEADER1));
        
        Company company = new Company();
        company.setCompanyName(companyName);
        company.setBroker(brokerId);
        company.setCustom1(custom1Value); 

        boolean isDuplicate = isDuplicateCompany(company);

        boolean isSpecial = (brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER1) ||
                brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER2)); 
        if (isDuplicate) {
            String causeDuplicateName = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DUPLICATE_RECORD);
            causeDuplicateName = (!isSpecial ? causeDuplicateName + " - " + companyName : 
                causeDuplicateName + " - " + companyName + ", " + custom1Value);
            fileImportResult.addFailedRecord(record, causeDuplicateName,
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
        } 

        return isDuplicate;
    }

    /**
     * Enable specific company in database
     * //TODO: Understand  how this function will be used and what will be the inputs
     * 
     * @param companyId
     */
    public int activateCompany(int companyId) throws ApplicationException {
        Company company = companyRepository.findOne(companyId);

        if (null == company) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company",
                    "companyId=" + companyId);
        }

        companyRepository.activateCompany(companyId);

        learnCompanyService.activateLearnCompany(company, null);

        return companyId;
    }

    /**
     * Return default sort field for company service
     * 
     * @return String 
     */
    @Override
    public String getDefaultSortField()  {
        return DEFAULT_SORT_BY_COMPANY_NAME;
    }

    /**
     * Delete all companies by jobId
     * 
     * @param jobId
     * @return
     */
    @Transactional
    public APIResponse deleteCompanies(String jobId) {
        List<Integer> companies = companyRepository.findAllCompaniesByJobId(jobId);
        
        if (CollectionUtils.isEmpty(companies)) { 
            throw ApplicationException.createBadRequest(APIErrorCodes.NO_RECORDS_FOUND);
        }
        
        // Deleting companies records from throne DB
        companyRepository.deleteByAddedBy(jobId);
        
        // Deleting companies records from learn DB
        learnCompanyRepository.deleteByCompanyIdIn(companies);
        
        // Deleting users associated with these companies
        deleteAssociatedUsers(companies);

        APIResponse apiResponse = new APIResponse();
        apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, SUCCESS_DELETED, "jobId", jobId));
        return apiResponse;
    }

    /**
     * Delete users associated with companies
     * 
     * @param companyIdList
     */
    @Transactional
    public void deleteAssociatedUsers(List<Integer> companyIdList) {
        List<User> users = userRepository.findByCompanyIdIn(companyIdList);
        
        List<Integer> userIdList = new ArrayList<Integer>();
        users.stream().forEach(user -> userIdList.add(user.getUserId()));
        
        // Deleting users records from throne DB
        userRepository.deleteByCompanyIdIn(companyIdList);
        
        // Deleting users records from learn DB
        learnUserRepository.deleteByThrUserIdIn(userIdList); 
    }

    /**
     * 
     * @param csvModel
     * @param recordIndex
     * @param brokerId
     */
    @Override
    public void addRecordForBulk(CsvModel csvModel, Integer recordIndex, Integer brokerId) {
        
        FileImportResult fileImportResult = csvModel.getImportResult();
        String record = csvModel.getRecords().get(recordIndex);
        Map<String, Integer> headerIndexMap = csvModel.getHeaderIndexMap();
        Map<String, Map<String, String>> headerVsColumnMap = csvModel.getHeaderVsColumnMap();

        Map<String, String> companyHeaderVsColumnMap = headerVsColumnMap.get(COMPANY);
        Map<String, String> locationHeaderVsColumnMap = headerVsColumnMap.get(LOCATION);

        if (StringUtils.containsOnly(record, new char[] { ',', ' ' })) {
            fileImportResult.increamentBlankRecords();
            return; //skip any fully blank line 
        }
        
        List<String> requiredFields = getRequiredHeadersFromStdFields(CLIENT);

        if (!validateRequired(record, requiredFields, headerIndexMap, fileImportResult, resourceHandler)) {
            return;
        }
      
        //Check to validate duplicate record
        if (checkDuplicate(record, fileImportResult, brokerId, headerIndexMap)) {
            return;
        }

        String phone = getValueFromRow(record, headerIndexMap.get(FileUploadEnum.COMPANY_PHONE.getHeader()));
        if (!validatePhone(COMPANY, record, phone, fileImportResult, resourceHandler)) {
            return;
        }

        populateAndSaveToDB(record, companyHeaderVsColumnMap,
                locationHeaderVsColumnMap,
                headerIndexMap,
                fileImportResult);
    }

}