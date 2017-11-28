package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.services.CompanyService;
import com.thinkhr.external.api.services.utils.FileImportUtil;


/**
 * Company Controller for performing operations
 * related with Company object.
 * 
 * @author Ajay Jain
 * @since 2017-11-05
 * 
 * 
 */
@RestController
@Validated
@RequestMapping(path="/v1/companies")
public class CompanyController {
	
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
    @Autowired
    CompanyService companyService;
    
    @Autowired
    MessageResourceHandler resourceHandler;

    /**
     * List all companies from repository
     * 
     * @return List<Company>
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET)
    public List<Company> getAllCompany(@Range(min=0l, message="Please select positive integer value for 'offset'") 
    		@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
    		@Range(min=1l, message="Please select positive integer and should be greater than 0 for 'limit'") 
    		@RequestParam(value = "limit", required = false, defaultValue= "50" ) Integer limit, 
    		@RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_BY_COMPANY_NAME) String sort,
    		@RequestParam(value = "searchSpec", required = false) String searchSpec, 
    		@RequestParam Map<String, String> allRequestParams) 
    				throws ApplicationException {
    	
    		return companyService.getAllCompany(offset, limit, sort, searchSpec, allRequestParams); 
    }
    
    /**
     * Get company for a given id from database
     * 
     * @param id clientId
     * @return Company object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET, value="/{companyId}")
    public Company getById(@PathVariable(name="companyId", value = "companyId") Integer companyId) 
    		throws ApplicationException {
        Company company = companyService.getCompany(companyId);
        if (null == company) {
        	throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+ companyId);
        }
        return company;
    }
    
    
    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{companyId}")
    public ResponseEntity<Integer> deleteCompany(@PathVariable(name="companyId", value = "companyId") Integer companyId) 
    		throws ApplicationException {
    	companyService.deleteCompany(companyId);
    	return new ResponseEntity <Integer>(companyId, HttpStatus.ACCEPTED);
    }
    
    
    /**
     * Update a company in database
     * 
     * @param Company object
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{companyId}")
	public ResponseEntity <Company> updateCompany(@PathVariable(name="companyId", value = "companyId") Integer companyId, 
			@Valid @RequestBody Company company) throws ApplicationException {
    	company.setCompanyId(companyId);
    	companyService.updateCompany(company);
        return new ResponseEntity<Company> (company, HttpStatus.OK);

	}
    
    
    /**
     * Add a company in database
     * 
     * @param Company object
     */
    @RequestMapping(method=RequestMethod.POST)
   	public ResponseEntity<Company> addCompany(@Valid @RequestBody Company company) throws ApplicationException {
    	companyService.addCompany(company);
        return new ResponseEntity<Company>(company, HttpStatus.CREATED);
   	}
    
    /**
     * Bulk import company records from a given CSV file.
     * 
     * @param Multipart file CSV files with records
     * @param brokerId - brokerId from request. Originally retrieved as part of JWT token
     * 
     */
    @RequestMapping(method=RequestMethod.POST,  value="/bulk")
    public ResponseEntity <InputStreamResource> bulkUploadFile(@RequestParam(value="file", required=false) MultipartFile file, 
    		@RequestParam(value = "brokerId", required = false, 
            			  defaultValue = ApplicationConstants.DEFAULT_BROKERID_FOR_FILE_IMPORT) Integer brokerId )
            throws ApplicationException, IOException {
     
    	logger.info("##### ######### COMPANY IMPORT BEGINS ######### #####");
        FileImportResult fileImportResult = companyService.bulkUpload(file, brokerId);
        logger.debug("************** COMPANY IMPORT ENDS *****************");
        
        // Set the attachment header & set up response to return a CSV file with result and erroneous records
        // This response CSV file can be used by users to resubmit records after fixing them.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-disposition", "attachment;filename=companiesImportResult.csv");

        File responseFile = FileImportUtil.createReponseFile(fileImportResult, resourceHandler);

        return ResponseEntity.ok().headers(headers).contentLength(responseFile.length()).contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new FileInputStream(responseFile)));
    }
   
}