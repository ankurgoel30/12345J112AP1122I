package com.thinkhr.external.api.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.StandardFields;

/**
 * @author Surabhi Bhawsar
 * @since 2017-12-04
 *
 */
public interface StandardFieldsRepository extends CrudRepository<StandardFields, Serializable> {
    public List<StandardFields> findByType(String type);
}
