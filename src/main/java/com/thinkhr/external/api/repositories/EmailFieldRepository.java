package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.EmailField;

/**
 * Email Field repository for EmailField entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public interface EmailFieldRepository extends CrudRepository<EmailField, Integer> {

}
