package com.thinkhr.external.api.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     * 
     * @param configurationId
     * @return
     */
    public Configuration getConfiguraiton(Integer configurationId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * @param configurationId
     */
    public void deleteConfiguration(Integer configurationId) {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     * @param configurationId
     * @param configurationJson
     * @param brokerId
     * @return
     */
    public Configuration updateConfiguration(Integer configurationId, String configurationJson) 
            throws ApplicationException, JsonProcessingException, IOException {
        Configuration configurationInDb = configurationRepository.findOne(configurationId);
        if (null == configurationInDb) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "configuration", "configurationId="+configurationId);
        }
        
        return updateConfiguration(configurationJson, configurationInDb);
    }

    /**
     * 
     * @param configurationJson
     * @param configurationId
     * @return
     */
    public Configuration updateConfiguration(String configurationJson, Configuration configurationInDb) 
            throws IOException {
        Configuration updatedConfiguration = update(configurationJson, configurationInDb);

        validateObject(updatedConfiguration);
        return null;
    }

    /**
     * Add a alternate configuration
     * 
     * @param configuration
     * @return
     */
    public Configuration addConfiguration(Configuration configuration) {
        Company broker = validateBrokerId(configuration.getCompanyId());
        
        Integer configurationId = broker.getConfigurationId();
        Integer brokerId = broker.getCompanyId();
        
        if (configurationId == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS, 
                    String.valueOf(brokerId)); 
        }
        
        if (configurationId != null && !isValidMasterConfigurationId(configurationId)){
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_MASTER_CONFIGURATION_ID, 
                    String.valueOf(brokerId));
        }
        
        configuration.setIsMasterConfiguration(0); // For alternate configuration
        return configurationRepository.save(configuration);
    }

    /**
     * TO check whether configuration is master.
     * 
     * @param configurationId
     * @return
     */
    private boolean isValidMasterConfigurationId(Integer configurationId) {
        return configurationRepository.findMasterConfiguration(configurationId) == null ? false : true;
    }

}