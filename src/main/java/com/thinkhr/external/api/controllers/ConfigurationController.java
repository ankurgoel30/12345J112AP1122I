package com.thinkhr.external.api.controllers;

import java.io.IOException;

import javax.validation.Valid;

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
     * Get configuration for a given id from database
     * 
     * @param id configurationId
     * @return Configuration object
     * @throws ApplicationException 
     * 
     */
    @RequestMapping(method=RequestMethod.GET, value="/{configurationId}")
    public Configuration getById(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId) 
            throws ApplicationException {
        return configurationService.getConfiguraiton(configurationId);
    }


    /**
     * Delete specific configuration from database
     * 
     * @param configurationId
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/{configurationId}")
    public ResponseEntity<Integer> deleteConfiguration(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId) 
            throws ApplicationException {
        configurationService.deleteConfiguration(configurationId);
        return new ResponseEntity <Integer>(configurationId, HttpStatus.ACCEPTED);
    }


    /**
     * Update a configuration in database
     * 
     * @param Configuration object
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    @RequestMapping(method=RequestMethod.PUT,value="/{configurationId}")
    public ResponseEntity <Configuration> updateConfiguration(@PathVariable(name="configurationId", value = "configurationId") Integer configurationId, 
            @RequestBody String configurationJson) 
            throws ApplicationException, JsonProcessingException, IOException {

        Configuration updatedConfiguration = configurationService.updateConfiguration(configurationId, configurationJson);
        return new ResponseEntity<Configuration>(updatedConfiguration, HttpStatus.OK);

    }


    /**
     * Add a configuration in database
     * 
     * @param Configuration object
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Configuration> addConfiguration(@Valid @RequestBody Configuration configuration)
            throws ApplicationException {
        configurationService.addConfiguration(configuration);
        return new ResponseEntity<Configuration>(configuration, HttpStatus.CREATED);
    }

}
