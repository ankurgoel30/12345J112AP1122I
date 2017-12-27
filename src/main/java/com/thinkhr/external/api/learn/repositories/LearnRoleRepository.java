package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.learn.entities.LearnRole;

public interface LearnRoleRepository extends CrudRepository<LearnRole, Integer> {

    /**
     * 
     * @param shortName
     * @return
     */
    public LearnRole findFirstByShortname(String shortName);

}
