package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.CompanyProduct;

public interface CompanyProductRepository extends CrudRepository<CompanyProduct, Integer> {

    /**
     * 
     * @param companyId
     */
    @Transactional
    public Integer deleteByCompanyId(Integer companyId);

}
