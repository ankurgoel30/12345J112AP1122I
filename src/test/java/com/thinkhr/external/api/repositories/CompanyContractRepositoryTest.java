package com.thinkhr.external.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.CompanyContract;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to verify methods of CompanyContractRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class CompanyContractRepositoryTest {

    @Autowired
    private CompanyContractRepository companyContractRepository;

    /**
     * Test to verify repository.save method
     * 
     */
    @Test
    public void testSaveCompanyContract() {

        CompanyContract companyContract = ApiTestDataUtil.createCompanyContract(null, 10, 25, new Date(),
                "Test tempId");

        // Setting end date
        companyContract.setEndDate(new Date());

        // Saving record into H2 DB
        CompanyContract savedContract = companyContractRepository.save(companyContract);

        assertNotNull(savedContract.getRelId());
        assertEquals(companyContract.getCompanyId(), savedContract.getCompanyId());
        assertEquals(companyContract.getProductId(), savedContract.getProductId());
        assertEquals(companyContract.getStartDate(), savedContract.getStartDate());
        assertEquals(companyContract.getTempID(), savedContract.getTempID());
    }

    /**
     * Test to verify deleteByCompanyId method.
     * 
     */
    @Test
    public void testDeleteByCompanyId() {

        CompanyContract companyContract = ApiTestDataUtil.createCompanyContract(null, 10, 25, new Date(),
                "Test tempId");

        // Setting end date
        companyContract.setEndDate(new Date());

        // Saving record into H2 DB
        CompanyContract savedContract = companyContractRepository.save(companyContract);

        // Deleting record from H2 DB
        Integer relId = companyContractRepository.deleteByCompanyId(savedContract.getCompanyId());

        CompanyContract foundContract = companyContractRepository.findOne(relId);

        assertNotNull(relId);
        assertNull(foundContract);
    }

}
