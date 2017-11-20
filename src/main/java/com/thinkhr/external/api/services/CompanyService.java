package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_BROKER;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.ApplicationConstants.MAX_RECORDS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FILE_EXTENSION_IMPORT;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
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
	 JdbcTemplate jdbcTemplate;

    /**
     * To fetch companies records. Based on given parameters companies records will be filtered out.
     * 
     * @param Integer offset First record index from database after sorting. Default value is 0
     * @param Integer limit Number of records to be fetched. Default value is 50
     * @param String sortField Field on which records needs to be sorted
     * @param String searchSpec Search string for filtering results
     * @param Map<String, String>
     * @return List<Company> object 
     * @throws ApplicationException 
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

    	companyList.getContent().forEach(c -> companies.add(c));
    	
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
    	try {
    		companyRepository.delete(companyId);
    	} catch (EmptyResultDataAccessException ex ) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
    	}
    	return companyId;
    }    
    
   
    /**
     * Imports a CSV file for companies record
     * 
     * @param fileToImport
     * @throws ApplicationException
     */
    public FileImportResult bulkUpload(MultipartFile fileToImport) throws ApplicationException {
        StopWatch stopWatchFileRead = new StopWatch();
        stopWatchFileRead.start();

        List<String> fileContents = new ArrayList<String>();
        String[] headers = null;
        validateAndReadFile(fileToImport, fileContents, headers);
        stopWatchFileRead.stop();
        StopWatch stopWatchDBSave = new StopWatch();
        stopWatchDBSave.start();

        FileImportResult fileImportResult = new FileImportResult();
        saveByNativeQuery(headers, fileContents.subList(1, fileContents.size()), fileImportResult);
        stopWatchDBSave.stop();

        double totalFileReadTimeInSec = stopWatchFileRead.getTotalTimeSeconds();
        double totalDBSaveTimeInSec = stopWatchDBSave.getTotalTimeSeconds();
        System.out.println("File Read Time:" + totalFileReadTimeInSec);
        System.out.println("DB Save Time :" + totalDBSaveTimeInSec);
        System.out.println(fileImportResult.getNumSuccessRecords());
        System.out.println(fileImportResult.getNumFailedRecords());
        return fileImportResult;
    }
    
    /**
     * Validates fileToimport and populates fileContens and file headers
     * 
     * @param fileToImport
     * @param fileContents
     * @param headers
     * @throws ApplicationException
     */
    public void validateAndReadFile(MultipartFile fileToImport, 
    									   List<String> fileContents, 
    									   String[] headers) throws ApplicationException {
        
    	String fileName = fileToImport.getOriginalFilename();

        // Validate if file has valid extension or empty
        if (!FileImportUtil.hasValidExtension(fileName, VALID_FILE_EXTENSION_IMPORT)) {
            throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_FILE_EXTENTION, fileName, VALID_FILE_EXTENSION_IMPORT);
        }

        if (fileToImport.isEmpty()) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }

        // Read all lines from file
        try {
            fileContents = FileImportUtil.readFileContent(fileToImport);
        } catch (IOException ex) {
            throw ApplicationException.createFileImportError(APIErrorCodes.FILE_READ_ERROR, ex.getMessage());
        }

        // Validate for missing headers
        headers = fileContents.get(0).split(",");
        String[] missingHeadersIfAny = FileImportUtil.getMissingHeaders(headers, REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
        if (missingHeadersIfAny.length != 0) {
            String requiredHeaders = String.join(",", REQUIRED_HEADERS_COMPANY_CSV_IMPORT);
            String missingHeaders = String.join(",", missingHeadersIfAny);
            throw ApplicationException.createFileImportError(APIErrorCodes.MISSING_REQUIRED_HEADERS, fileName, missingHeaders,
                    requiredHeaders);
        }

        // If we are here then file is a valid csv file with all the required headers. 
        // Now  validate if it has records and number of records not exceed max allowed records
        int numOfRecords = fileContents.size() - 1; // as first line is for header
        if (numOfRecords == 0) {
            throw ApplicationException.createFileImportError(APIErrorCodes.NO_RECORDS_FOUND_FOR_IMPORT, fileName);
        }
        if (numOfRecords > MAX_RECORDS_COMPANY_CSV_IMPORT) {
            throw ApplicationException.createFileImportError(APIErrorCodes.MAX_RECORD_EXCEEDED,
                    String.valueOf(MAX_RECORDS_COMPANY_CSV_IMPORT));
        }
    }
    
    /**
     * Saves file data using native query. 
     * We don't want ORM mapping here as that will lead
     * additional mapping overhead and slow down operations. 
     * 
     *  Not able to use batch processing here due to requirement of
     *  dealing with failure records & adding data in more than one tables.
     * 
     *  
     * @param headers
     * @param records
     * @param fileImportResult
     */
    private void saveByNativeQuery(String[] headersInCSV, List<String> records, FileImportResult fileImportResult)
            throws ApplicationException {
        fileImportResult.setTotalRecords(records.size());

        Map<String, String> columnToHeaderCompanyMap = getCompanyColumnsHeaderMap(DEFAULT_BROKER);
        Map<String, String> columnToHeaderLocationMap = FileImportUtil.getColumnsToHeaderMapForLocationRecord();

        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }

        String[] companyColumnsToInsert = columnToHeaderCompanyMap.keySet().toArray(new String[columnToHeaderCompanyMap.size()]);
        String[] locationColumnsToInsert = columnToHeaderLocationMap.keySet().toArray(new String[columnToHeaderLocationMap.size()]);

        for (int recIdx = 0; recIdx < records.size(); recIdx++) {
            String record = records.get(recIdx).trim();
            if (StringUtils.isBlank(record)) {
                fileImportResult.increamentFailedRecords();
                fileImportResult.addFailedRecord(recIdx + 1, record, "Blank Record", "Skipped");
                continue;
            }

            String[] values = record.split(",");
            Object[] companyColumnsValues = new Object[companyColumnsToInsert.length];
            Object[] locationColumnsValues = new Object[locationColumnsToInsert.length];

            try {
                // Populate companyColumnsValues from split record
                FileImportUtil.populateColumnValues(companyColumnsValues, companyColumnsToInsert, columnToHeaderCompanyMap, values,
                        headerIndexMap);

                // Populate locationColumnsValues from split record
                FileImportUtil.populateColumnValues(locationColumnsValues, locationColumnsToInsert, columnToHeaderCompanyMap, values,
                        headerIndexMap);
            } catch (ArrayIndexOutOfBoundsException ex) {
                fileImportResult.increamentFailedRecords();
                fileImportResult.addFailedRecord(recIdx + 1, record, "Not All Fields available in record", "Skipped");
                continue;
            } catch (Exception ex) {
                throw ApplicationException.createFileImportError(APIErrorCodes.FILE_READ_ERROR, ex.getMessage());
            }

            try {
                fileDataRepository.saveCompanyRecord(companyColumnsToInsert, companyColumnsValues, locationColumnsToInsert,
                        locationColumnsValues);
                fileImportResult.increamentSuccessRecords();
            } catch (Exception ex) {
                fileImportResult.increamentFailedRecords();
                fileImportResult.addFailedRecord(recIdx + 1, record, ex.getMessage(), "Record could not be added");
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
     * @param customColumnsLookUpId
     * @return
     */
    private Map<String, String> getCompanyColumnsHeaderMap(int customColumnsLookUpId) {
        Map<String, String> columnToHeaderCompanyMap = FileImportUtil.getColumnsToHeaderMapForCompanyRecord();
        Map<String, String> customColumnToHeaderMap = getCustomFieldsMap(187624);//customColumnsLookUpId

        //Merge customColumnToHeaderMap to columnToHeaderCompanyMap
        for (String column : customColumnToHeaderMap.keySet()) {
            columnToHeaderCompanyMap.put(column, customColumnToHeaderMap.get(column));
        }
        return columnToHeaderCompanyMap;
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