package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.SetPasswordRequest;

/**
 * Repository for SetPasswordRequest entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public interface SetPasswordRequestRepository extends CrudRepository<SetPasswordRequest, String> {

}
