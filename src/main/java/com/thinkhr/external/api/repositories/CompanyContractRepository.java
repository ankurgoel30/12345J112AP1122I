package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.CompanyContract;

public interface CompanyContractRepository extends CrudRepository<CompanyContract, Integer> {

}
