package com.thinkhr.external.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.CompanyService;


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
@RequestMapping(path="/v1/companies")
public class CompanyController {
	
    @Autowired
    CompanyService companyService;

    /**
     * List all companies from repository
     * 
     * @return List<Company>
     * 
     */
    @RequestMapping(method=RequestMethod.GET)
    public List<Company> getAllCompany() {
        return companyService.getAllCompany();
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
    public Company getById(@PathVariable(name="companyId",value = "companyId") Integer companyId) throws ApplicationException {
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
    public ResponseEntity<Integer> deleteCompany(@PathVariable(name="companyId",value = "companyId") Integer companyId) throws ApplicationException{
    	companyService.deleteCompany(companyId);
    	return new ResponseEntity <Integer>(companyId, HttpStatus.NO_CONTENT);
    }
    
    
    /**
     * Update a company in database
     * 
     * @param CompanyModel object
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{companyId}")
	public ResponseEntity <Company> updateCompany(@PathVariable(name="companyId",value = "companyId") Integer companyId, 
			@RequestBody Company company) throws ApplicationException {
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
}

