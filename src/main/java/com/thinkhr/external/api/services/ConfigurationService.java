package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
     * @param brokerId 
     * @return
     */
    public Configuration getConfiguration(Integer configurationId, Integer brokerId) {
        
        return checkConfigurationForBroker(configurationId, brokerId);
    }

    /**
     * Checks if the configuration belongs to the broker
     * 
     * @param configurationId
     * @param brokerId
     * @return
     */
    private Configuration checkConfigurationForBroker(Integer configurationId,
            Integer brokerId) {

        Configuration configuration = configurationRepository.findFirstByConfigurationIdAndCompanyId(configurationId, brokerId);
        
        if (null == configuration) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.INVALID_CONFIGURATION_ID,
                    String.valueOf(configurationId), String.valueOf(brokerId));
        }
        return configuration;
    }

    /**
     * Delete an alternate configuration
     * 
     * @param configurationId
     * @param brokerId 
     */
    public int deleteConfiguration(Integer configurationId, Integer brokerId) {
        Configuration configuration = checkConfigurationForBroker(configurationId, brokerId);
        
        if (configuration.getMasterConfiguration() != null && configuration.getMasterConfiguration() == 1) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.MASTER_CONFIGURATION_NOT_CHANGEABLE, 
                    String.valueOf(configurationId));
        }
        
        Company company = companyRepository.findFirstByConfigurationId(configurationId);
        
        if(company!= null){
            throw ApplicationException.createBadRequest(APIErrorCodes.UNREMOVABLE_LINKED_CONFIGURATION, 
                    String.valueOf(configurationId),String.valueOf(company.getCompanyId()));
        }
        
        configurationRepository.softDelete(configurationId);
        
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
    public Configuration updateConfiguration(Integer configurationId, String configurationJson, Integer brokerId) 
            throws ApplicationException, JsonProcessingException, IOException {
        
        Configuration configurationInDb = checkConfigurationForBroker(configurationId, brokerId);
        
        if (configurationInDb.getMasterConfiguration() != null && configurationInDb.getMasterConfiguration() == 1) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.MASTER_CONFIGURATION_NOT_CHANGEABLE, 
                    String.valueOf(configurationId));
        }
        return updateConfiguration(configurationJson, configurationInDb);
    }

    /**
     * Update Configuration
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
     * @param brokerId 
     * @return
     */
    public Configuration addConfiguration(Configuration configuration, Integer brokerId) {
        
        if(!configuration.getCompanyId().equals(brokerId)){
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_BROKER_ID_IN_BODY,String.valueOf(configuration.getCompanyId()));
        }
        
        Configuration masterConfiguration = configurationRepository.findFirstByCompanyIdAndMasterConfiguration(brokerId,1);
        
        if (masterConfiguration == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS); 
        }
        
        return configurationRepository.save(configuration);
    }

    
    /**
     * Get multiple Configurations
     * 
     * @param brokerId
     * @param searchSpec 
     * @param sort 
     * @param limit 
     * @param offset 
     * @return
     */
    public List<Configuration> getConfigurations(Integer brokerId, Integer offset, Integer limit, String sortField, String searchSpec) {
        
        List<Configuration> configurations = new ArrayList<Configuration>();
        
        Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
        Specification<Configuration> spec = getEntitySearchSpecification(searchSpec, null, Configuration.class, new Configuration());

        Page<Configuration> configurationList  = configurationRepository.findAll(spec, pageable);

        if (configurationList != null) {
            configurationList.getContent().forEach(c -> {
                if(c.getCompanyId().equals(brokerId)){
                    configurations.add(c);
                }
            });
        }

        //Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, configurations.size());
        
        return configurations;
    }
    

}