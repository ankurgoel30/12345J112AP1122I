package com.thinkhr.external.api.learn.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.Package;


/**
 * Package repository for Package entity.
 *  
 * @author Ajay Jain
 * @since   2017-12-14
 *
 */

public interface PackageRepository extends PagingAndSortingRepository<Package, Long>, JpaSpecificationExecutor<LearnCompany> {
    Package findFirstByName(String name);
}