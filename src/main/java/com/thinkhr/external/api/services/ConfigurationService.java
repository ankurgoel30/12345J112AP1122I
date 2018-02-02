package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        Configuration configuration = configurationRepository.findOne(configurationId);
        
        if (null == configuration) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND,
                    "Configuration", "Configuration Id = " + String.valueOf(configurationId));
        }
        
        return checkConfigurationForBroker(configuration, brokerId);
    }

    /**
     * Checks if the configuration belongs to the broker
     * 
     * @param configuration
     * @param brokerId
     * @return
     */
    private Configuration checkConfigurationForBroker(Configuration configuration,
            Integer brokerId) {
        
        if(!configuration.getCompanyId().equals(brokerId)){
            throw ApplicationException.createAuthorizationError(APIErrorCodes.UNAUTHORIZED_CONFIGURATION_ACCESS,
                    String.valueOf(brokerId), String.valueOf(configuration.getConfigurationId()));
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
        
        Configuration configuration = configurationRepository.findOne(configurationId);
        
        if (null == configuration) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND,
                    "Configuration", "Configuration Id = " + String.valueOf(configurationId));
        }
        
        checkConfigurationForBroker(configuration, brokerId);
        
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
        
        Configuration configurationInDb = configurationRepository.findOne(configurationId);
        
        if (null == configurationInDb) {
            throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND,
                    "Configuration", "Configuration Id = " + String.valueOf(configurationId));
        }
        
        checkConfigurationForBroker(configurationInDb, brokerId);
        
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
        
        configuration.setCompanyId(brokerId);
        
        Configuration masterConfiguration = configurationRepository.findFirstByCompanyIdAndMasterConfiguration(brokerId,1);
        
        if (masterConfiguration == null) {
            throw ApplicationException.createBadRequest(APIErrorCodes.MASTER_CONFIGURATION_NOT_EXISTS); 
        }
        
        configuration.setMasterConfiguration(null);
        
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
     * @param allRequestParams 
     * @return
     */
    public List<Configuration> getConfigurations(Integer brokerId, Integer offset, Integer limit, String sortField, String searchSpec, Map<String, String> requestParams) {
        
        List<Configuration> configurations = new ArrayList<Configuration>();
        
        Map<String,String> brokerParameter = new HashMap<String,String>();
        brokerParameter.put("companyId", String.valueOf(brokerId));
        
        Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
        Specification<Configuration> spec = getEntitySearchSpecification(searchSpec, requestParams, Configuration.class, new Configuration());
        
        spec = addFilterInSearchSpecification(spec,brokerParameter);
        
        Page<Configuration> configurationList  = configurationRepository.findAll(spec, pageable);

        if (configurationList != null) {
            configurationList.getContent().forEach(c -> configurations.add(c));
        }

        //Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, configurationRepository.count(spec));
        
        return configurations;
    }

    
    /**
     * To add broker manually in search specification
     * 
     * @param spec
     * @param requestParameter
     * @return 
     */
    private Specification<Configuration> addFilterInSearchSpecification(
            Specification<Configuration> spec, Map<String, String> requestParameter) {

        if(spec!=null){
            EntitySearchSpecification<Configuration> entitySearchSpecification = (EntitySearchSpecification<Configuration>)spec;
            if(entitySearchSpecification.getSearchParameters() != null){
                entitySearchSpecification.getSearchParameters().putAll(requestParameter);
            }else if(entitySearchSpecification.getSearchSpec() != null){
                entitySearchSpecification.setSearchParameters(requestParameter);
            }
            
            return entitySearchSpecification;
        }
        else{
            return new EntitySearchSpecification(requestParameter, new Configuration());
        }
    }
    

}