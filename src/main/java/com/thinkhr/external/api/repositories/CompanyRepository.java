package com.thinkhr.external.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.Company;


/**
 * Company repository for company entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface CompanyRepository extends CrudRepository<Company, Integer> {
	
	/**
	 * To limit records by given pageable
	 * @param pageable
	 * @return
	 */
	Page<Company> findAll(Pageable pageable);
}