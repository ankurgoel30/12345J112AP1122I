package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnPackageMaster;


/**
 * Package repository for Package entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-14
 *
 */

public interface PackageRepository extends PagingAndSortingRepository<LearnPackageMaster, Long>, JpaSpecificationExecutor<LearnPackageMaster> {
    LearnPackageMaster findFirstByName(String name);
}