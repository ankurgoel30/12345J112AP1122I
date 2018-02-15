package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.services.BrokerService;

@RestController
@Validated
@RequestMapping(path="/v1/brokers")
public class BrokerController {

    private Logger logger = LoggerFactory.getLogger(BrokerController.class);

    @Autowired
    BrokerService brokerService;

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
    public List<Company> getAllBrokers(@Range(min=0l, message="Please select positive integer value for 'offset'") 
    @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
    @Range(min=1l, message="Please select positive integer and should be greater than 0 for 'limit'") 
    @RequestParam(value = "limit", required = false, defaultValue= "50" ) Integer limit, 
    @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_BY_COMPANY_NAME) String sort,
    @RequestParam(value = "searchSpec", required = false) String searchSpec, 
    @RequestParam Map<String, String> allRequestParams) 
            throws ApplicationException {

        return brokerService.getAllBroker(offset, limit, sort, searchSpec, allRequestParams); 
    }

    /**
     * Get company for a given id from database
     * 
     * @param id clientId
     * @return Company object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET, value="/{brokerId}")
    public Company getById(@PathVariable(name="brokerId", value = "brokerId") Integer brokerId) 
            throws ApplicationException {
        return brokerService.getBroker(brokerId);
    }


    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{brokerId}")
    public ResponseEntity<Integer> deleteBroker(@PathVariable(name = "brokerId", value = "brokerId") Integer brokerId)
            throws ApplicationException {
        brokerService.deleteBroker(brokerId);
        return new ResponseEntity <Integer>(brokerId, HttpStatus.ACCEPTED);
    }


    /**
     * Update a company in database
     * 
     * @param Company object
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{brokerId}")
    public ResponseEntity<Company> updateBroker(@PathVariable(name = "brokerId", value = "brokerId") Integer brokerId,
            @RequestBody String companyJson) 
            throws ApplicationException, JsonProcessingException, IOException {

        Company updatedCompany = brokerService.updateBroker(brokerId, companyJson);
        return new ResponseEntity<Company>(updatedCompany, HttpStatus.OK);

    }

    /**
     * Add a company in database
     * 
     * @param Company object
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Company> addBroker(@Valid @RequestBody Company company) throws ApplicationException {
        brokerService.addBroker(company);
        return new ResponseEntity<Company>(company, HttpStatus.CREATED);
    }

}