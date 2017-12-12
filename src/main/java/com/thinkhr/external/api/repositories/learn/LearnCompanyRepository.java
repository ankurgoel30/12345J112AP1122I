package com.thinkhr.external.api.repositories.learn;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.entities.learn.LearnCompany;


/**
 * Learn Company repository for Company entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-12
 *
 */

public interface LearnCompanyRepository extends PagingAndSortingRepository<LearnCompany, Long>, JpaSpecificationExecutor<LearnCompany> {

}