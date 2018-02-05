package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.entities.Sku;

/**
 * Sku repository for sku entity.
 *  
 * @author Surabhi Bhawsar
 * @since 2018-01-23 
 *
 */
public interface SkuRepository extends PagingAndSortingRepository<Sku, Integer>, JpaSpecificationExecutor<Sku> {

}
