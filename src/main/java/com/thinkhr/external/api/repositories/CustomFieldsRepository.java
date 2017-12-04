package com.thinkhr.external.api.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.CustomFields;


/**
 * Company repository for company entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface CustomFieldsRepository extends CrudRepository<CustomFields, Serializable> {

    public List<CustomFields> findByCompanyId(int companyID);

    public List<CustomFields> findByCompanyIdAndCustomFieldType(int id,
            String customFieldType);
}