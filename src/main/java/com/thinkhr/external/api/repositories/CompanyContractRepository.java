package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.CompanyContract;

public interface CompanyContractRepository extends CrudRepository<CompanyContract, Integer> {

    /**
     * 
     * @param companyId
     * @return
     */
    @Transactional
    public Integer deleteByCompanyId(Integer companyId);

}
