package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.ApplicationConstants.BROKER_ID_PARAM;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_CONFIGURATION_NAME;

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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.services.ConfigurationService;

/**
 * Configuration Controller for performing operations
 * related with Configuration object.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-23
 * 
 * 
 */
@RestController
@Validated
@RequestMapping(path = "/v1/configurations")
public class ConfigurationController {
    
    private Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    MessageResourceHandler resourceHandler;

    /**
     * Get an alternate configuration for a given id from database
     * 
     * @param id configurationId
     * @return Configuration object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET, value="/{configurationId}")
    public Configuration getById(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId,
            @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId) 
            throws ApplicationException {
        return configurationService.getConfiguration(configurationId,brokerId);
    }
    
    /**
     * Get all configurations for a given brokerid from JWT
     * 
     * @param id configurationId
     * @return Configuration object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET)
    public List<Configuration> getConfigurations(@Range(min=0l, message="Please select positive integer value for 'offset'") 
    @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
    @Range(min=1l, message="Please select positive integer and should be greater than 0 for 'limit'") 
    @RequestParam(value = "limit", required = false, defaultValue= "50" ) Integer limit, 
    @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_BY_CONFIGURATION_NAME) String sort,
    @RequestParam(value = "searchSpec", required = false) String searchSpec,
    @RequestParam Map<String, String> allRequestParams,
    @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId) 
            throws ApplicationException {
        return configurationService.getConfigurations(brokerId, offset, limit, sort, searchSpec, allRequestParams);
    }


    /**
     * Delete specific alternate configuration from database
     * 
     * @param configurationId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{configurationId}")
    public ResponseEntity<Integer> deleteConfiguration(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId,
            @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId) 
            throws ApplicationException {
        configurationService.deleteConfiguration(configurationId,brokerId);
        return new ResponseEntity <Integer>(configurationId, HttpStatus.ACCEPTED);
    }


    /**
     * Update an alternate configuration in database
     * 
     * @param Configuration object
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{configurationId}")
    public ResponseEntity <Configuration> updateConfiguration(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId, 
            @RequestBody String configurationJson, @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId) 
            throws ApplicationException, JsonProcessingException, IOException {

        Configuration updatedConfiguration = configurationService.updateConfiguration(configurationId, configurationJson, brokerId);
        return new ResponseEntity<Configuration>(updatedConfiguration, HttpStatus.OK);

    }


    /**
     * Add an alternate configuration in database
     * 
     * @param Configuration object
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Configuration> addConfiguration(@Valid @RequestBody Configuration configuration,
            @RequestAttribute(name = BROKER_ID_PARAM) Integer brokerId)
            throws ApplicationException {
        configurationService.addConfiguration(configuration,brokerId);
        return new ResponseEntity<Configuration>(configuration, HttpStatus.CREATED);
    }

}
