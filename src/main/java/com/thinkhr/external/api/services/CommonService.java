package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.APP_AUTH_DATA;
import static com.thinkhr.external.api.services.utils.FileImportUtil.getCustomFieldPrefix;
import static com.thinkhr.external.api.ApplicationConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.db.entities.StandardFields;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;
import com.thinkhr.external.api.learn.repositories.LearnFileDataRepository;
import com.thinkhr.external.api.learn.repositories.LearnRoleRepository;
import com.thinkhr.external.api.learn.repositories.LearnUserRepository;
import com.thinkhr.external.api.learn.repositories.PackageRepository;
import com.thinkhr.external.api.model.AppAuthData;
import com.thinkhr.external.api.model.CsvModel;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.ConfigurationRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.repositories.EmailTemplateRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
import com.thinkhr.external.api.repositories.SetPasswordRequestRepository;
import com.thinkhr.external.api.repositories.SkuRepository;
import com.thinkhr.external.api.repositories.StandardFieldsRepository;
import com.thinkhr.external.api.repositories.UserRepository;
import com.thinkhr.external.api.request.APIRequestHelper;
import com.thinkhr.external.api.services.upload.FileUploadEnum;

import lombok.Data;

/**
 * Common Service to hold all general operations
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-11-04
 *
 */
@Service
@Data
public class CommonService {

    @Autowired
    protected FileDataRepository fileDataRepository;

    @Autowired
    protected MessageResourceHandler resourceHandler;

    @Autowired
    protected CustomFieldsRepository customFieldRepository;

    @Autowired
    protected StandardFieldsRepository standardFieldRepository;

    @Autowired
    protected CompanyRepository companyRepository;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    LearnUserRepository learnUserRepository;

    @Autowired
    LearnFileDataRepository learnFileDataRepository;
    
    @Autowired
    protected ConfigurationRepository configurationRepository;
    
    @Autowired
    protected SkuRepository skuRepository;
    
    @Autowired
    protected LearnRoleRepository learnRoleRepository;

    @Autowired
    protected ModelConvertor modelConvertor;
    
    @Autowired
    LearnCompanyRepository learnCompanyRepository;
    
    @Autowired
    PackageRepository packageRepository;
    
    @Autowired
    protected EmailTemplateRepository emailRepository;

    @Autowired
    protected SetPasswordRequestRepository setPasswordRepository;
    
    @Value("${com.thinkhr.external.api.threadpool.size}")
    protected Integer threadPoolSize;
    
    @PersistenceContext
    protected EntityManager entityManager;
    
    /**
     * @return
     */
    public String getDefaultSortField()  {
        return null;
    }

    /**
     * This function returns a map of custom fields to customFieldDisplayLabel(Header in CSV)
     * map by looking up into app_throne_custom_fields table
     * 
     * @param id
     * @param customFieldType
     * @return Map<String,String> 
     */
    protected Map<String, String> getCustomFieldsMap(int id, String customFieldType) {

        List<CustomFields> list = customFieldRepository.findByCompanyIdAndCustomFieldType(id, customFieldType);

        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, String> customFieldsToHeaderMap = new LinkedHashMap<String, String>();
        for (CustomFields customField : list) {
            String customFieldName = getCustomFieldPrefix(customFieldType) + customField.getCustomFieldColumnName();
            customFieldsToHeaderMap.put(customFieldName, customField.getCustomFieldDisplayLabel());
        }
        return customFieldsToHeaderMap;
    }

    /**
     * Validate and get broker for given brokerId
     * 
     * @param brokerId
     * @return
     */
    public Company validateBrokerId(Integer brokerId) {
        // Process files if submitted by a valid broker else throw an exception
        Company broker = companyRepository.findOne(brokerId);
        if (null == broker) {
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_BROKER_ID, String.valueOf(brokerId));
        }
        return broker;

    }
    
    /**
     * Get a map of Company columns
     * 
     * @param companyId
     * @param resource
     * @return
     */
    public Map<String, String> appendRequiredAndCustomHeaderMap(int companyId, String resource) {

        Map<String, String> requiredColHeaderMap = FileUploadEnum.prepareColumnHeaderMap(resource);

        Map<String, String> customColHeaderMap = getCustomFieldsMap(companyId, resource);//customColumnsLookUpId - gets custom fields from database

        if (customColHeaderMap == null) {
            return requiredColHeaderMap;
        }
        
        requiredColHeaderMap.putAll(customColHeaderMap);
        
        return requiredColHeaderMap;
    }

    
    /**
     * This function looks up table mapped to StandardFields entity and returns list of 
     * headers for which values must be present in a csv record. 
     * @param type
     */
    public List<String> getRequiredHeadersFromStdFields(String type) {
        
        if (type == null) {
            return null;
        }
        
        List<StandardFields> stdFields = standardFieldRepository.findByType(type);
        if (stdFields == null) {
            return null;
        }
        
        List<String> list = new ArrayList<String>();
        stdFields.stream().forEach(field -> list.add(field.getLabel()));
        
        return list;
    }
    
    
    /**
     * This function overwrites values from given json string in to given objectToUpdate
     * 
     * @param json
     * @param objectToUpdate
     * @return
     * @throws IOException
     */
    public <T> T update(String json, T objectToUpdate) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDefaultMergeable(true); // This is required for deep update. Available in jackson-databind from 2.9 version
        ObjectReader updater = objectMapper.readerForUpdating(objectToUpdate);

        return updater.readValue(json);
    }
    
    /**
     * Validates given object for any constraint voilations and throws ConstraintViolationException if any 
     * voilations are found
     * 
     * @param object
     */
    public <T> void validateObject(T object) {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);

        if(constraintViolations!=null && !constraintViolations.isEmpty()) {
            ConstraintViolationException ex = new ConstraintViolationException(constraintViolations);
            throw ex;
        }
    }
    
    /**
     * 
     * @param user
     * @param brokerId
     */
    protected String getAddedBy(Integer brokerId) {
        String addedBy = null;
        AppAuthData authData = (AppAuthData) APIRequestHelper.getRequestAttribute(APP_AUTH_DATA);
        if (authData != null) {
            User authUser = userRepository.findByUserName(authData.getUser());
            addedBy = String.valueOf(authUser.getUserId());
        } else {
            addedBy = String.valueOf(brokerId); 
        }

        return addedBy;
    }
    
    /** To create a Configuration for given companyId
     * 
     * @param companyId
     * @return
     */
    public Configuration createMasterConfiguration(Integer companyId) {
        Configuration configuration = new Configuration();
        configuration.setCompanyId(companyId);
        configuration.setMasterConfiguration(1);
        configuration.setConfigurationKey(MASTER_CONFIG_KEY);
        configuration.setConfigurationName(MASTER_CONFIG_NAME); 
        configuration.setDescription(MASTER_CONFIG_NAME);
        return configuration;
    }
    
    /**
     * Returns true if Json String has the requested field
     * 
     * @param configurationJson
     * @return
     */
    public boolean containsField(String configurationJson, String field) {
       
            try {
                ObjectMapper mapper = new ObjectMapper();
                // convert JSON string to Map
                JsonNode node = mapper.readTree(configurationJson);
                return (node.has(field)) ? true : false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
