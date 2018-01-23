package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.BROKER_ID_PARAM;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.FileImportResult;
import com.thinkhr.external.api.services.BrokerService;
import com.thinkhr.external.api.services.utils.FileImportUtil;

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

        return brokerService.getAllCompany(offset, limit, sort, searchSpec, allRequestParams); 
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
        return brokerService.getCompany(brokerId);
    }


    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{brokerId}")
    public ResponseEntity<Integer> deleteCompany(@PathVariable(name="brokerId", value = "brokerId") Integer brokerId) 
            throws ApplicationException {
        brokerService.deleteCompany(brokerId);
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
    public ResponseEntity <Company> updateCompany(@PathVariable(name="brokerId", value = "brokerId") Integer brokerId, 
            @RequestBody String companyJson) 
            throws ApplicationException, JsonProcessingException, IOException {

        Company updatedCompany = brokerService.updateCompany(brokerId, companyJson , brokerId);
        return new ResponseEntity<Company>(updatedCompany, HttpStatus.OK);

    }


    /**
     * Add a company in database
     * 
     * @param Company object
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Company> addCompany(@Valid @RequestBody Company company, 
            @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId)
            throws ApplicationException {
        brokerService.addCompany(company, brokerId);
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
            @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId)
                    throws ApplicationException, IOException {

        logger.info("##### ######### COMPANY IMPORT BEGINS ######### #####");
        FileImportResult fileImportResult = brokerService.bulkUpload(file, brokerId);
        logger.debug("************** COMPANY IMPORT ENDS *****************");

        // Set the attachment header & set up response to return a CSV file with result and erroneous records
        // This response CSV file can be used by users to resubmit records after fixing them.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-disposition", "attachment;filename=companiesImportResult.csv");

        File responseFile = FileImportUtil.createReponseFile(fileImportResult, resourceHandler);

        return ResponseEntity.status(fileImportResult.getHttpStatus()).headers(headers).contentLength(responseFile.length()).contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new FileInputStream(responseFile)));
    }
}