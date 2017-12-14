package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.learn.entities.LearnCompany;


/**
 * Learn Company repository for LearnCompany entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-12
 *
 */

public interface LearnCompanyRepository extends PagingAndSortingRepository<LearnCompany, Long>, JpaSpecificationExecutor<LearnCompany> {

    /**
     * 
     * @param thrCompanyId
     * @param companyKey
     * @return
     */
    public LearnCompany findFirstByCompanyIdAndCompanyKey(Integer thrCompanyId, String companyKey);
}