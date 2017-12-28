package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.learn.entities.LearnUser;


/**
 * Learn Company repository for LearnCompany entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-12
 *
 */

public interface LearnUserRepository extends PagingAndSortingRepository<LearnUser, Long>, JpaSpecificationExecutor<LearnUser> {

    /**
     * 
     * @param thrUserId
     * @return
     */
    public LearnUser findFirstByThrUserId(Integer thrUserId);

}