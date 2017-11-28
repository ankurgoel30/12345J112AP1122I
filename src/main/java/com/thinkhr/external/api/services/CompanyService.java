package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.services.upload.FileUploadEnum;

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

    private Logger logger = LoggerFactory.getLogger(CompanyService.class);

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

        Page<Company> companyList  = (Page<Company>) companyRepository.findAll(spec, pageable);

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
        return companyRepository.findOne(companyId);
    }

    /**
     * Add a company in database
     * 
     * @param company object
     */
    public Company addCompany(Company company)  {
        return companyRepository.save(company);
    }

    /**
     * Update a company in database
     * 
     * @param company object
     * @throws ApplicationException 
     */
    public Company updateCompany(Company company) throws ApplicationException  {
        Integer companyId = company.getCompanyId();

        if (null == companyRepository.findOne(companyId)) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
        }

        return companyRepository.save(company);

    }

    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    public int deleteCompany(int companyId) throws ApplicationException {

        if (null == companyRepository.findOne(companyId)) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
        }

        companyRepository.softDelete(companyId);

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

        Company broker = validateAndGetBroker(brokerId);

        List<String> fileContents = validateAndGetFileContent(fileToImport);

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
    private FileImportResult processRecords (List<String> records, 
                                                 Company broker) throws ApplicationException {

        FileImportResult fileImportResult = new FileImportResult();

        String headerLine = records.get(0);
        records.remove(0);

        fileImportResult.setTotalRecords(records.size());
        fileImportResult.setHeaderLine(headerLine);

        //To keep track of duplicate records in imported file. A duplicate record = duplicate client_name
        Set<String> companyNames = new HashSet<String>();

        String[] headersInCSV = headerLine.split(COMMA_SEPARATOR);

        //DO not assume that CSV file shall contains fixed column position. Let's read and map then with database column
        Map<String, String> companyFileHeaderColumnMap = getCompanyColumnHeaderMap(broker.getCompanyId()); 

        Map<String, String> locationFileHeaderColumnMap = FileUploadEnum.LOCATION.prepareColumnHeaderMap();

        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        validateAndFilterCustomHeaders(headersInCSV, companyFileHeaderColumnMap.values(), resourceHandler);

        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }

        int recCount = 0;

        for (String record : records ) {

            //Check to validate empty record
            if (StringUtils.isBlank(record)) {
                fileImportResult.addFailedRecord(recCount++ , record, 
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.BLANK_RECORD),
                        getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));

                continue;
            }

            String[] rowColValues = record.split(COMMA_SEPARATOR);
            String companyName = rowColValues[0].trim(); //TODO Fix this hardcoding.
            companyNames.add(companyName);
            

            //Check to validate duplicate record
            if (checkDuplicate(recCount, record, fileImportResult)) {
                continue;
            }

            populateAndSaveToDB(record, companyFileHeaderColumnMap,
                                locationFileHeaderColumnMap,
                                headerIndexMap,
                                fileImportResult,
                                recCount);
        }

        logger.debug("Total Number of Records: " + fileImportResult.getTotalRecords());
        logger.debug("Total Number of Successful Records: " + fileImportResult.getNumSuccessRecords());
        logger.debug("Total Number of Failure Records: " + fileImportResult.getNumFailedRecords());

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
     * @param companyFileHeaderColumnMap
     * @param locationFileHeaderColumnMap
     * @param headerIndexMap
     * @param fileImportResult
     * @param recCount
     */
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
            fileImportResult.addFailedRecord(recCount++ , record, 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.MISSING_FIELDS), 
                    getMessageFromResourceBundle(resourceHandler, APIErrorCodes.SKIPPED_RECORD));
            return;
        } catch (Exception ex) {
            throw ApplicationException.createFileImportError(APIErrorCodes.FILE_READ_ERROR, ex.toString());
        }

        try {

            //Finally save companies one by one
            List<String> companyColumnsToInsert = new ArrayList<String>(companyFileHeaderColumnMap.keySet());
            List<String> locationColumnsToInsert = new ArrayList<String>(locationFileHeaderColumnMap.keySet());

            fileDataRepository.saveCompanyRecord(companyColumnsToInsert, companyColumnsValues, locationColumnsToInsert,
                    locationColumnsValues);

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
            FileImportResult fileImportResult) {

        String[] rowColValues = record.split(COMMA_SEPARATOR);
        
        String companyName = rowColValues[0].trim(); //TODO Fix this hardcoding.

        String custom1Value = null; 

        if (rowColValues.length > 11) {
           custom1Value = rowColValues[11].trim();
        }

        boolean isDuplicate = false;

        boolean isSpecial = StringUtils.equalsIgnoreCase(companyName, ApplicationConstants.SPECIAL_CASE_FOR_DUPLICATE);

        Company companyByFirstName = companyRepository.findFirstByCompanyName(companyName);

        if (null != companyByFirstName) { //A DB query is must here to check duplicates in data
            if (!isSpecial) {
                isDuplicate = true;
            }
            //handle special case of Paychex
            if (isSpecial && companyRepository.findFirstByCompanyNameAndCustom1(companyName, custom1Value) != null) {  
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
     * Get a map of Company columns
     * 
     * @param companyId
     * @return
     */
    public Map<String, String> getCompanyColumnHeaderMap(int companyId) {
        
        Map<String, String> companyColumnHeaderMap = FileUploadEnum.COMPANY.prepareColumnHeaderMap();
        
        Map<String, String> customColumnHeaderMap = getCustomFieldsMap(companyId);//customColumnsLookUpId - gets custom fields from database
        
        if (customColumnHeaderMap != null) {
            companyColumnHeaderMap.putAll(customColumnHeaderMap);
        }
        return companyColumnHeaderMap;
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