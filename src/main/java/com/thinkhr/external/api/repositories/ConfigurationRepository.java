package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thinkhr.external.api.db.entities.Configuration;

public interface ConfigurationRepository
        extends PagingAndSortingRepository<Configuration, Integer>, JpaSpecificationExecutor<Configuration> {

    /**
     * 
     * @param configurationId
     * @param companyId
     * @return
     */
    public Configuration findFirstByConfigurationIdAndCompanyId(Integer configurationId, Integer companyId);

}
