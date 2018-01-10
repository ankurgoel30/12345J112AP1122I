package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.CLIENT;
import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY;
import static com.thinkhr.external.api.ApplicationConstants.COMPANY_CUSTOM_HEADER1;
import static com.thinkhr.external.api.ApplicationConstants.CONFIGURATION_ID_FOR_INACTIVE;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.ApplicationConstants.HASH_KEY;
import static com.thinkhr.external.api.ApplicationConstants.LOCATION;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateAndGetFileContent;
import static com.thinkhr.external.api.services.upload.FileImportValidator.validateRequired;
import static com.thinkhr.external.api.services.utils.CommonUtil.getTempId;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
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
public class CompanyService  extends CommonService {

    @Autowired
    protected LearnCompanyService learnCompanyService;

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

        Specification<Company> spec = getEntitySearchSpecification(searchSpec, requestParameters, Company.class, new Company());

        Page<Company> companyList  = companyRepository.findAll(spec, pageable);

        if (companyList != null) {
            companyList.getContent().forEach(c -> companies.add(c));
        }

        //Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, companyRepository.count());

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

        if (company.getCustomField5() == null) {
            company.setCustomField5(company.getDisplayName());
        }

        Company throneCompany = saveCompany(company, brokerId, true);
        
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
        Location location = company.getLocation();
        if (location != null && location.getCompany() == null) {
            location.setCompany(company);

            // setting tempID for location
            location.setTempID(CommonUtil.getTempId());
        }
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
        
        Company updatedCompany = update(companyJson, companyInDb);

        if (updatedCompany.getLocation() != null) {
            updatedCompany.getLocation().setCompany(null); // This is required before doing validation otherwise validation is cascaded recursively and gives stackoverflow error
        }
        validateObject(updatedCompany);

        Company throneCompany = saveCompany(updatedCompany, brokerId, false);
        learnCompanyService.updateLearnCompany(throneCompany);
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
        if (isNew && isDuplicateCompany(company.getCompanyName(), company.getBroker(), company.getCustom1())) {
            throw ApplicationException.createBadRequest(APIErrorCodes.DUPLICATE_COMPANY_RECORD,
                    company.getCompanyName());
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
    public boolean isDuplicateCompany(String companyName, Integer brokerId, String custom1) {

        boolean isDuplicate = false;

        boolean isSpecial = (brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER1) ||
                brokerId.equals(ApplicationConstants.SPECIAL_CASE_BROKER2)); 
        
        //find matching company by given company name and broker id
        Company companyFromDB = companyRepository.findFirstByCompanyNameAndBroker(companyName,
                brokerId);

        if (null != companyFromDB) { //A DB query is must here to check duplicates in data
            if (!isSpecial) {
                isDuplicate = true;
            }
            // handle special case of Paychex
            // find matching company by given company name, custom1 field and broker id
            if (isSpecial && companyRepository.findFirstByCompanyNameAndCustom1AndBroker(companyName,
                    custom1, brokerId) != null) {
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

        companyRepository.softDelete(companyId);

        learnCompanyService.deactivateLearnCompany(company);

        return companyId;
    }     

    /**
     * Imports a CSV file for companies record
     * 
     * @param fileToImport
     * @param brokerId
     * @throws ApplicationException
     */
    public FileImportResult bulkUpload(MultipartFile fileToImport, int brokerId) throws ApplicationException {

        Company broker = validateBrokerId(brokerId);

        List<String> fileContents = validateAndGetFileContent(fileToImport, COMPANY);

        return processRecords (fileContents, broker);

    }

    /**
     * Process imported file to save companies records in database
     *  
     * @param records
     * @param brokerId
     * @param resource
     * @throws ApplicationException
     */
    FileImportResult processRecords(List<String> records, 
            Company broker) throws ApplicationException {

        if (records == null) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, null);
        }

        if (broker == null || broker.getCompanyId() == null) {
            throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_BROKER_ID, "null");
        }

        FileImportResult fileImportResult = new FileImportResult();

        String headerLine = records.get(0);
        records.remove(0);

        fileImportResult.setTotalRecords(records.size());
        fileImportResult.setHeaderLine(headerLine);
        fileImportResult.setBrokerId(broker.getCompanyId());

        String[] headersInCSV = headerLine.split(COMMA_SEPARATOR);

        //DO not assume that CSV file shall contains fixed column position. Let's read and map then with database column
        Map<String, String> companyFileHeaderColumnMap = appendRequiredAndCustomHeaderMap(broker.getCompanyId(), resource); 

        Map<String, String> locationFileHeaderColumnMap = FileUploadEnum.prepareColumnHeaderMap(LOCATION);

        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        String[] requiredHeaders = getRequiredHeaders(resource);
        validateAndFilterCustomHeaders(headersInCSV, companyFileHeaderColumnMap.values(), requiredHeaders, resourceHandler);

        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }

        int recCount = 0;

        for (String record : records ) {
            
            if (StringUtils.isEmpty(StringUtils.deleteWhitespace(record).replaceAll(",", ""))) {
                fileImportResult.increamentBlankRecords();
                continue; //skip any fully blank line 
            }

            List<String> requiredFields = getRequiredHeadersFromStdFields(CLIENT);

            if (!validateRequired(record, requiredFields, headerIndexMap, fileImportResult, resourceHandler)) {
                continue;
            }
          
            //Check to validate duplicate record
            if (checkDuplicate(record, fileImportResult, broker.getCompanyId(), headerIndexMap)) {
                continue;
            }

            populateAndSaveToDB(record, companyFileHeaderColumnMap,
                    locationFileHeaderColumnMap,
                    headerIndexMap,
                    fileImportResult,
                    recCount);
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
            FileImportResult fileImportResult, 
            int recCount) {

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
            companyColumnsToInsert.add("broker");
            companyColumnsValues.add(fileImportResult.getBrokerId());

            //t1_display_name
            companyColumnsToInsert.add("t1_display_name");
            String displayName = getValueFromRow(record,
                    headerIndexMap.get(FileUploadEnum.COMPANY_DISPLAY_NAME.getHeader()));
            companyColumnsValues.add(displayName);

            saveCompanyRecord(companyColumnsValues, locationColumnsValues,
                    companyColumnsToInsert, locationColumnsToInsert);

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

        boolean isDuplicate = isDuplicateCompany(companyName, brokerId, custom1Value);

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

}