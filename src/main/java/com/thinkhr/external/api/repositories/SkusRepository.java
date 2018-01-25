package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.entities.Skus;

public interface SkusRepository extends PagingAndSortingRepository<Skus, Integer>, JpaSpecificationExecutor<Skus> {

}
