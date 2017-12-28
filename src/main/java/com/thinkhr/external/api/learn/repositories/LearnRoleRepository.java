package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.learn.entities.LearnRole;


/**
 * LearnRole repository for LearnRole entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-20
 *
 */

public interface LearnRoleRepository extends CrudRepository<LearnRole, Integer> {

    /**
     * 
     * @param shortName
     * @return
     */
    public LearnRole findFirstByShortName(String shortName);
}