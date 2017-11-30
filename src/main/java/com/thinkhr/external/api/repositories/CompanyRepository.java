package com.thinkhr.external.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.Company;


/**
 * Company repository for company entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface CompanyRepository extends PagingAndSortingRepository<Company, Integer> ,JpaSpecificationExecutor<Company> {

    @Query("update Company c set c.isActive=0, c.deactivationDate=current_date() where c.companyId = ?1")
    @Modifying
    @Transactional
    public void softDelete(int companyID);

    /**
     * Repository method
     * @param companyName
     * @return
     */
    public Company findFirstByCompanyName(String companyName);

    /**
     * Repository method
     * @param companyName
     * @param string
     * @return
     */
    public Company findFirstByCompanyNameAndCustom1(String companyName,
            String customField1);
}