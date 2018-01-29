package com.thinkhr.external.api.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

/**
*
* Provides a collection of all services related with Configuration
* database object

* @author Surabhi Bhawsar
* @Since 2017-11-04
*
* 
*/
@Service
public class ConfigurationService extends CommonService {
    
    private Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    /**
     * Get an alternate configuration
     * 
     * @param configurationId
     * @return
     */
    public Configuration getConfiguraiton(Integer configurationId) {
        Configuration configuration = configurationRepository.findOne(configurationId);
        
        if (null == configuration) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND,
                    "configuration", "configurationId="+ configurationId);
        }
        return configuration;
    }

    /**
     * Delete an alternate configuration
     * 
     * @param configurationId
     */
    public int deleteConfiguration(Integer configurationId) {
        Configuration configuration = configurationRepository.findOne(configurationId);
        
        if (null == configuration) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "configuration", "configurationId="+configurationId);
        }
        
        if (configuration.getMasterConfiguration() != null && configuration.getMasterConfiguration() == 1) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.MASTER_CONFIGURATION_NOT_CHANGEABLE, 
                    String.valueOf(configurationId));
        }
        configurationRepository.delete(configuration);
        
        return configurationId;
    }

    /**
     * Update an alternate configuration
     * 
     * @param configurationId
     * @param configurationJson
     * @param brokerId
     * @return
     */
    @Transactional
    public Configuration updateConfiguration(Integer configurationId, String configurationJson) 
            throws ApplicationException, JsonProcessingException, IOException {
        Configuration configurationInDb = configurationRepository.findOne(configurationId);
        if (null == configurationInDb) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "configuration", "configurationId="+configurationId);
        }
        
        if (configurationInDb.getMasterConfiguration() != null && configurationInDb.getMasterConfiguration() == 1) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.MASTER_CONFIGURATION_NOT_CHANGEABLE, 
                    String.valueOf(configurationId));
        }
        return updateConfiguration(configurationJson, configurationInDb);
    }

    /**
     * 
     * @param configurationJson
     * @param configurationId
     * @return
     */
    @Transactional
    public Configuration updateConfiguration(String configurationJson, Configuration configurationInDb) 
            throws IOException {
        Configuration updatedConfiguration = update(configurationJson, configurationInDb);

        validateObject(updatedConfiguration);
        Configuration updatedConfig = configurationRepository.save(updatedConfiguration);

        // This is required otherwise values for updatable=false fields is not synced with 
        // database when these fields are passed in payload .
        entityManager.flush();
        entityManager.refresh(updatedConfig);

        return updatedConfig;
    }

    /**
     * Add an alternate configuration
     * 
     * @param configuration
     * @return
     */
    public Configuration addConfiguration(Configuration configuration) {
        Company broker = validateBrokerId(configuration.getCompanyId());
        
        Integer configurationId = broker.getConfigurationId();
        
        if (configurationId == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS); 
        }
        
        if (!isValidMasterConfigurationId(configurationId)){
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_MASTER_CONFIGURATION_ID, 
                    String.valueOf(configurationId));
        }
        
        configuration.setMasterConfiguration(0); // For alternate configuration
        return configurationRepository.save(configuration);
    }

    /**
     * To check whether configuration is master or not.
     * 
     * @param configurationId
     * @return
     */
    protected boolean isValidMasterConfigurationId(Integer configurationId) {
        return configurationRepository.findMasterConfiguration(configurationId) == null ? false : true;
    }

}