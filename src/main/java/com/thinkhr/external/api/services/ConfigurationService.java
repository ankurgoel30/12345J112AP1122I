package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_CONFIGURATION_NAME;
import static com.thinkhr.external.api.ApplicationConstants.SKUS_FIELD;
import static com.thinkhr.external.api.ApplicationConstants.TOTAL_RECORDS;
import static com.thinkhr.external.api.request.APIRequestHelper.setRequestAttribute;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.applyAdditionalFilter;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    protected Configuration checkConfigurationForBroker(Configuration configuration,
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
        
        //To Set Configuration DB object's SKU list to null when all SKU have to be deleted otherwise it merges the same and does not delete
        if(containsField(configurationJson,SKUS_FIELD)){
            configurationInDb.setSkus(null);
        }
        
        Configuration configuration = update(configurationJson, configurationInDb);
        
        if(!CollectionUtils.isEmpty(configuration.getSkus())){
            validateSkusToConfigure(configuration, brokerId);
        }
        
        configuration.setUpdated((int) (System.currentTimeMillis() / 1000L));
        
        return updateConfiguration(configuration);
    }

    /**
     * To validate if SKUs in configuration are subset of the SKUs in master configuration for the broker
     * 
     * @param configuration
     */
    private void validateSkusToConfigure(Configuration configuration, Integer brokerId) {

        //On terms that master configuration is never null for a broker
        Configuration masterConfiguration = configurationRepository.findFirstByCompanyIdAndMasterConfiguration(brokerId,1);
        Set<Integer> masterSkuIds = masterConfiguration.getSkus().stream().map(a -> a.getId()).collect(Collectors.toSet());
        Set<Integer> requiredSkuIds = configuration.getSkus().stream().map(a -> a.getId()).collect(Collectors.toSet());
        
        requiredSkuIds.removeAll(masterSkuIds);
        
        if(!CollectionUtils.isEmpty(requiredSkuIds)){
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_SKU_IDS, 
                    StringUtils.join(requiredSkuIds, ","),StringUtils.join(masterSkuIds, ","),String.valueOf(brokerId));
        }
    }

    /**
     * Update Configuration
     * 
     * @param configurationJson
     * @param configurationId
     * @return
     */
    @Transactional
    public Configuration updateConfiguration( Configuration configurationInDb) 
            throws IOException {
        
        validateObject(configurationInDb);
        Configuration updatedConfig = configurationRepository.save(configurationInDb);

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
        
        if(!CollectionUtils.isEmpty(configuration.getSkus())){
            validateSkusToConfigure(configuration, brokerId);
        }
        
        //Modification done as master configuration cannot be created via this API
        configuration.setMasterConfiguration(0);
        
        configuration.setCreated((int) (System.currentTimeMillis() / 1000L));
        
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
        
        Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
        Configuration entity = new Configuration();
        Specification<Configuration> spec = getEntitySearchSpecification(searchSpec, requestParams, Configuration.class, entity);
        
        spec = applyAdditionalFilter(spec, Collections.singletonMap("companyId", String.valueOf(brokerId)), entity);
        
        Page<Configuration> configurationList  = configurationRepository.findAll(spec, pageable);

        if (configurationList != null) {
            configurationList.getContent().forEach(c -> configurations.add(c));
        }

        //Get and set the total number of records
        setRequestAttribute(TOTAL_RECORDS, configurationRepository.count(spec));
        
        return configurations;
    }
    
    /**
     * Return default sort field for Configuration service
     * 
     * @return String 
     */
    @Override
    public String getDefaultSortField()  {
        return DEFAULT_SORT_BY_CONFIGURATION_NAME;
    }


 }