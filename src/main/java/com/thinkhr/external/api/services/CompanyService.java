package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FILE_EXTENSION_IMPORT;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.io.IOException;
import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.BulkCompanyModel;
import com.thinkhr.external.api.model.BulkCompanyModel.CompanyJSONModel;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
import com.thinkhr.external.api.response.APIMessageUtil;
import com.thinkhr.external.api.services.utils.FileImportUtil;

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
	
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private FileDataRepository fileDataRepository;

    @Autowired
    MessageResourceHandler resourceHandler;

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
    public FileImportResult bulkUpload(MultipartFile fileToImport, 
    								   int brokerId, 
    								   BulkCompanyModel companyData) throws ApplicationException {
    	
		 StopWatch stopWatchImportData = new StopWatch();
		 stopWatchImportData.start();    	 
    	
		 Company broker = companyRepository.findOne(brokerId);
		 
		 // Process upload is submitted by a valid broker else throw an exception
		 if (null == broker) {
              throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_BROKER_ID, String.valueOf(brokerId));
		 }
        
    	List<String> fileContents = new ArrayList<String>();
    	String[] headers;
    	if (null!= fileToImport ) {  //if file upload, validate input file and read all content
    		validateAndReadFile(fileToImport, fileContents);
    	} else if (null!= companyData && !companyData.getCompanies().isEmpty()) { //if JSON upload, read and validate content
    		fileContents = (List<String>) companyData.getCompanies().stream().map(CompanyJSONModel::toCsvRow).collect(Collectors.toList());
    		String headerString = companyData.determineHeaders();
    		fileContents.add(0, headerString);
    		validateFileContent(fileContents);
    	} else {	// No data for import either by file or JSON
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT);
        }
        
		headers = fileContents.get(0).split(",");
		
	     //process file for import records 
        FileImportResult fileImportResult = new FileImportResult();
        
		saveByNativeQuery(headers, fileContents.subList(1, fileContents.size()), fileImportResult, broker);
        fileImportResult.setHeaderLine(fileContents.get(0));
        
        stopWatchImportData.stop();
        double totalTimeTakenForImport = stopWatchImportData.getTotalTimeSeconds();
        logger.debug("Time taken importing data :" + totalTimeTakenForImport+" seconds");
        return fileImportResult;
    }
    
	/**
     * This function validates fileToimport and populates fileContens
     * 
     * @param fileToImport
     * @param fileContents
     * @param headers
     * @throws ApplicationException
     * 
     */
    private void validateAndReadFile(MultipartFile fileToImport, List <String> fileContents) throws ApplicationException {
        String fileName = fileToImport.getOriginalFilename();

        // Validate if file has valid extension
        if (!FileImportUtil.hasValidExtension(fileName, VALID_FILE_EXTENSION_IMPORT)) {
            throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_FILE_EXTENTION, fileName, VALID_FILE_EXTENSION_IMPORT);
        }
        
        //validate if files has no records
        if (fileToImport.isEmpty()) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }
        
        // Read all records from file or simply use string data
        try {
        		fileContents.addAll(FileImportUtil.readFileContent(fileToImport)); 
        } catch (IOException ex) {
            throw ApplicationException.createFileImportError(APIErrorCodes.FILE_READ_ERROR, ex.getMessage());
        }
        validateFileContent(fileContents);
     }

    /**
     * Validates file content for missing header, number of records etc. 
     * 
     * @param fileContents
     */
	private void validateFileContent(List<String> fileContents) {
		// Validate for missing headers. File must container all expected columns, if not, return from here.
        String[] headers = fileContents.get(0).split(",");
        String[] missingHeadersIfAny = FileImportUtil.getMissingHeaders(headers, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
        if (missingHeadersIfAny.length != 0) {
            String requiredHeaders = String.join(",", REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
            String missingHeaders = String.join(",", missingHeadersIfAny);
            throw ApplicationException.createFileImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS, missingHeaders,
                    requiredHeaders);
        }

        // Validate number of records. We don't want to process huge number of records at real time
        int numOfRecords = fileContents.size() - 1; // as first line is for header
        if (numOfRecords == 0) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT);
        }
        if (numOfRecords > MAX_RECORDS_COMPANY_CSV_IMPORT) {
            throw ApplicationException.createFileImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                    String.valueOf(MAX_RECORDS_COMPANY_CSV_IMPORT));
        }
	}

    /**
     * Process imported file to save companies records in database 
     * 
     * @param headersInCSV
     * @param records
     * @param fileImportResult
     * @param brokerId
     * @throws ApplicationException
     */
    private void saveByNativeQuery(String[] headersInCSV, List<String> records, FileImportResult fileImportResult, Company broker)
            throws ApplicationException {
        fileImportResult.setTotalRecords(records.size());
        
        //DO not assume that CSV file shall contains fixed column position. Let's read and map then with database column
        Map<String, String> columnToHeaderCompanyMap = getCompanyColumnsHeaderMap(broker.getCompanyId()); 
        Map<String, String> columnToHeaderLocationMap= getColumnsToHeaderMapForLocationRecord();
        
        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        FileImportUtil.checkCustomHeaders(headersInCSV, columnToHeaderCompanyMap.values(), resourceHandler);
        
        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }
        
        //final company columns after merging custom fields too. Also final location columns too.
        String[] companyColumnsToInsert = columnToHeaderCompanyMap.keySet().toArray(new String[columnToHeaderCompanyMap.size()]);
        String[] locationColumnsToInsert = columnToHeaderLocationMap.keySet().toArray(new String[columnToHeaderLocationMap.size()]);
        
        //To keep track of duplicate records in imported file. A duplicate record = duplicate client_name
        
        //For special case of Paychex, get broker info
        boolean isSpecial = StringUtils.equalsIgnoreCase(broker.getCompanyName(), ApplicationConstants.SPECIAL_CASE_FOR_DUPLICATE);
        
        for (int recIdx = 0; recIdx < records.size(); recIdx++) {
            String record = records.get(recIdx).trim();
            String[] values = record.split(",");
            Object[] companyColumnsValues = new Object[companyColumnsToInsert.length];
            Object[] locationColumnsValues = new Object[locationColumnsToInsert.length];

            try {
                // Populate companyColumnsValues from split record
                FileImportUtil.populateColumnValues(companyColumnsValues, companyColumnsToInsert, columnToHeaderCompanyMap, values,
                        headerIndexMap);

                // Populate locationColumnsValues from split record
                FileImportUtil.populateColumnValues(locationColumnsValues, locationColumnsToInsert, columnToHeaderLocationMap, values,
                        headerIndexMap);
            } catch (ArrayIndexOutOfBoundsException ex) {
                fileImportResult.increamentFailedRecords();
                String causeMissingFields = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "MISSING_FIELDS");
                String infoSkipped = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "SKIPPED");
                fileImportResult.addFailedRecord(recIdx + 1, record, causeMissingFields, infoSkipped);
                continue;
            } catch (Exception ex) {
                throw ApplicationException.createFileImportError(APIErrorCodes.FILE_READ_ERROR, ex.toString());
            }

            try {
            	boolean isDuplicate=false;
                String companyName = values[0].trim(); //TODO Fix this hardcoding.
                String custom1Value = values[11].trim();
                             
                if (null!=companyRepository.findFirstByCompanyName(companyName)) { //A DB query is must here to check duplicates in data
                	if (!isSpecial) isDuplicate = true;
                	//handle special case of Paychex
                    if (isSpecial && companyRepository.findFirstByCompanyNameAndCustom1(companyName, values[11]) != null) {  
                    	isDuplicate = true;
                    }
                    if(isDuplicate) {
                    	fileImportResult.increamentFailedRecords();
                    	String causeDuplicateName = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "DUPLICATE_NAME");
                    	causeDuplicateName = (!isSpecial ? causeDuplicateName + " - " + companyName : causeDuplicateName + " - " + companyName +", " +custom1Value);
                    	String infoSkipped = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "SKIPPED");
                    	fileImportResult.addFailedRecord(recIdx + 1, record, causeDuplicateName, infoSkipped);
                    	continue; //skip this record considering duplicate
                    }
                }
                //Finally save companies one by one
                fileDataRepository.saveCompanyRecord(companyColumnsToInsert, companyColumnsValues, locationColumnsToInsert,
                        locationColumnsValues);
                fileImportResult.increamentSuccessRecords();
            } catch (Exception ex) {
                fileImportResult.increamentFailedRecords();
                Throwable th = ex.getCause();
                String cause = null;
                String info = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "RECORD_NOT_ADDED");
                if (th instanceof DataTruncation) {
                    DataTruncation dte = (DataTruncation) th;
                    cause = APIMessageUtil.getMessageFromResourceBundle(resourceHandler, "DATA_TRUNCTATION");
                } else {
                    cause = ex.getMessage();
                }
                fileImportResult.addFailedRecord(recIdx + 1, record, cause, info);
            }
        }
        logger.debug("Total Number of Records: " + fileImportResult.getTotalRecords());
        logger.debug("Total Number of Successful Records: " + fileImportResult.getNumSuccessRecords());
        logger.debug("Total Number of Failure Records: " + fileImportResult.getNumFailedRecords());
        if (fileImportResult.getNumFailedRecords() > 0) {
            logger.debug("List of Failure Records");
            for (FileImportResult.FailedRecord failedRecord : fileImportResult.getFailedRecords()) {
                logger.debug(failedRecord.getRecord() + "," + failedRecord.getFailureCause());
            }
        }
    }

    /**
     * This function returns a map of custom fields to customFieldDisplayLabel(Header in CSV)
     * map by looking up into app_throne_custom_fields table
     * 
     * @return Map<String,String> 
     */
    private Map<String, String> getCustomFieldsMap(int id) {
        Map<String, String> customFieldsMap = fileDataRepository.getCustomFields(id);
        Map<String, String> customFieldsToHeaderMap = new LinkedHashMap<String, String>();

        for (String customFieldDisplayLabel : customFieldsMap.keySet()) {
            String customFieldName = "custom" + customFieldsMap.get(customFieldDisplayLabel);
            customFieldsToHeaderMap.put(customFieldName, customFieldDisplayLabel);
        }
        return customFieldsToHeaderMap;
    }
    
    /**
     * Get a map of Company columns
     * 
     * @param customColumnsLookUpId
     * @return
     */
    private Map<String, String> getCompanyColumnsHeaderMap(int customColumnsLookUpId) {
        
    	Map<String, String> columnToHeaderCompanyMap = FileImportUtil.getColumnsToHeaderMapForCompanyRecord();
        Map<String, String> customColumnToHeaderMap = getCustomFieldsMap(customColumnsLookUpId);//customColumnsLookUpId - gets custom fields from database

        //Merge customColumnToHeaderMap to columnToHeaderCompanyMap
        for (String column : customColumnToHeaderMap.keySet()) {
            columnToHeaderCompanyMap.put(column, customColumnToHeaderMap.get(column));
        }
        return columnToHeaderCompanyMap;
    }
    
    /**
     * This method returns a map for columns in locations table to headers in csv.
     * Key in map is the name of column in db for locations table.
     * Value in map is name of corresponding column in CSV file.
     */
    public static Map<String, String> getColumnsToHeaderMapForLocationRecord() {
        Map<String, String> columnsToHeaderMap = new LinkedHashMap<String, String>();

        //address
        columnsToHeaderMap.put("address", "ADDRESS");
        //address2
        columnsToHeaderMap.put("address2", "ADDRESS2");
        //city
        columnsToHeaderMap.put("city", "CITY");
        //state
        columnsToHeaderMap.put("state", "STATE");
        //zip
        columnsToHeaderMap.put("zip", "ZIP");

        return columnsToHeaderMap;
    }
    
    
   	/**
	 * 
	 * SOME UTILITY METHODS
	 * 
	 */
	
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