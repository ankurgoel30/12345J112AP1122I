package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.entities.Company;


/**
 * Company repository for company entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface CompanyRepository extends PagingAndSortingRepository<Company, Integer> ,JpaSpecificationExecutor<Company> {
	
}