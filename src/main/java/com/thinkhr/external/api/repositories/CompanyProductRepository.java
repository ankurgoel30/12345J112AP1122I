package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.CompanyProduct;

public interface CompanyProductRepository extends CrudRepository<CompanyProduct, Integer> {

}
