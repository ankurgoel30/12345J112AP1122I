package com.thinkhr.external.api.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.ThronePermissionsMaster;


/**
 * ThronePermissionsRepository repository for ThronePermissionsMaster entity.
 *  
 * @author Ajay Jain
 * @since   2018-01-09
 *
 */

public interface ThronePermissionsRepository extends CrudRepository<ThronePermissionsMaster, Integer> {
    List<ThronePermissionsMaster> findByDisplayLabelIn(List<String> displayLables);
}