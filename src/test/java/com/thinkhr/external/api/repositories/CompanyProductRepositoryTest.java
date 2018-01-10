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

import com.thinkhr.external.api.db.entities.CompanyProduct;
import com.thinkhr.external.api.services.CompanyService;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to verify methods of CompanyProductRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class CompanyProductRepositoryTest {

    @Autowired
    private CompanyProductRepository companyProductRepository;

    /**
     * Test to verify repository.save method
     * 
     */
    @Test
    public void testSaveCompanyProduct() {

        Integer contractId = 5;
        Integer companyId = 10;
        String authorizationKey = CompanyService.getAuthorizationKeyFromCompanyId(companyId);

        CompanyProduct companyProduct = ApiTestDataUtil.createCompanyProduct(null, contractId, companyId, new Date(),
                authorizationKey, 50, "test tempID");

        // Saving record into H2 DB
        CompanyProduct savedProduct = companyProductRepository.save(companyProduct);

        assertNotNull(savedProduct.getRelId());
        assertEquals(companyProduct.getCompanyId(), savedProduct.getCompanyId());
        assertEquals(companyProduct.getContractId(), savedProduct.getContractId());
        assertEquals(companyProduct.getStartDate(), savedProduct.getStartDate());
        assertEquals(companyProduct.getAuthorizationKey(), savedProduct.getAuthorizationKey());
        assertEquals(companyProduct.getNumberLicenses(), savedProduct.getNumberLicenses());
        assertEquals(companyProduct.getTempID(), savedProduct.getTempID());
    }

    /**
     * Test to verify deleteByCompanyId method.
     * 
     */
    @Test
    public void testDeleteByCompanyId() {

        Integer contractId = 5;
        Integer companyId = 10;
        String authorizationKey = CompanyService.getAuthorizationKeyFromCompanyId(companyId);

        CompanyProduct companyProduct = ApiTestDataUtil.createCompanyProduct(null, contractId, companyId, new Date(),
                authorizationKey, 50, "test tempID");

        // Saving record into H2 DB
        CompanyProduct savedProduct = companyProductRepository.save(companyProduct);

        // Deleting record from H2 DB
        Integer relId = companyProductRepository.deleteByCompanyId(savedProduct.getCompanyId());

        CompanyProduct foundProduct = companyProductRepository.findOne(relId);

        assertNotNull(relId);
        assertNull(foundProduct);
    }
}