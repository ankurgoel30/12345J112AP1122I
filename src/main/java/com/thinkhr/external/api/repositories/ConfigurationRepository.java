package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.thinkhr.external.api.db.entities.Configuration;

/**
 * Configuration repository for configuration entity.
 *  
 * @author Surabhi Bhawsar
 * @since 2018-01-23 
 *
 */
public interface ConfigurationRepository
        extends PagingAndSortingRepository<Configuration, Integer>, JpaSpecificationExecutor<Configuration> {

    /**
     * 
     * @param configurationId
     * @param companyId
     * @return
     */
    public Configuration findFirstByConfigurationIdAndCompanyId(Integer configurationId, Integer companyId);
    
    /**
     * 
     * @param configurationId
     * @return
     */
    @Query(value = "select c from Configuration c where c.configurationId = :id and c.masterConfiguration = 1")
    public Configuration findMasterConfiguration(@Param("id") Integer configurationId);

}
