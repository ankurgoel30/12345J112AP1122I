package com.thinkhr.external.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
     * Find first configuration by Configuration Id and company ID
     * 
     * @param configurationId
     * @param companyId
     * @return
     */
    public Configuration findFirstByConfigurationIdAndCompanyId(Integer configurationId, Integer companyId);

    /**
     * @param brokerId
     * @return
     */
    public List<Configuration> findByCompanyId(Integer brokerId);

    /**
     * Soft Delete Configuration
     * 
     * @param configurationId
     */
    @Query("update Configuration c set deleted=UNIX_TIMESTAMP() where c.configurationId = ?1")
    @Modifying
    @Transactional
    public void softDelete(Integer configurationId);

    /**
     * Find first configuration by Company Id and Master Configuration flag
     * 
     * @param brokerId
     * @param i
     * @return 
     */
    public Configuration findFirstByCompanyIdAndMasterConfiguration(Integer brokerId,
            int i);

}
