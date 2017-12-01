package com.thinkhr.external.api.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.repositories.CustomFieldsRepository;
import com.thinkhr.external.api.repositories.FileDataRepository;
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
    protected CompanyRepository companyRepository;


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
     * 
     * @return Map<String,String> 
     */
    protected Map<String, String> getCustomFieldsMap(int id, String customFieldType) {

        List<CustomFields> list = customFieldRepository.findByCompanyIdAndCustomFieldType(id, customFieldType);

        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, String> customFieldsToHeaderMap = new LinkedHashMap<String, String>();
        for (CustomFields customField : list) {
            String customFieldName = "custom" + customField.getCustomFieldColumnName();
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
    public Company validateAndGetBroker(int brokerId) {
        // Process files if submitted by a valid broker else throw an exception
        Company broker = companyRepository.findOne(brokerId);
        if (null == broker) {
            throw ApplicationException.createFileImportError(APIErrorCodes.INVALID_BROKER_ID, String.valueOf(brokerId));
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
    public Map<String, String> getCompanyColumnHeaderMap(int companyId, String resource) {

        Map<String, String> companyColumnHeaderMap = FileUploadEnum.prepareColumnHeaderMap(resource);

        Map<String, String> customColumnHeaderMap = getCustomFieldsMap(companyId, resource);//customColumnsLookUpId - gets custom fields from database

        if (customColumnHeaderMap != null) {
            companyColumnHeaderMap.putAll(customColumnHeaderMap);
        }
        
        return companyColumnHeaderMap;
    }

    
    



}
