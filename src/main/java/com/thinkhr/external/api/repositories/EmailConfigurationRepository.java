package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.EmailConfiguration;

/**
 * Email Configuration repository for EmailConfiguration entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public interface EmailConfigurationRepository extends CrudRepository<EmailConfiguration, Integer> {

}
