package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.ThroneRole;


/**
 * LearnRole repository for LearnRole entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-28
 *
 */

public interface ThroneRoleRepository extends CrudRepository<ThroneRole, Integer> {

}