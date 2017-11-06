package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.Company;


/**
 * User repository for user entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface CompanyRepository extends CrudRepository<Company, Integer> {
}