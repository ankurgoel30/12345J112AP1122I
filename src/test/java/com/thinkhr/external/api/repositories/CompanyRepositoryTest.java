package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanies;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompaniesWitNameAndBroker;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.EntitySearchSpecification;
import com.thinkhr.external.api.services.utils.EntitySearchUtil;

/**
 * Junit to verify methods of CompanyRepository with use of H2 database
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * To test companyRepository.save method when adding company. 
     */
    @Test
    public void testSaveForAdd() {
        Company company = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        Company companySaved = companyRepository.save(company);

        assertNotNull(companySaved);
        assertNotNull(companySaved.getCompanyId());// As company is saved successfully.
        assertEquals(companySaved.getSearchHelp(), company.getSearchHelp());
        assertEquals(companySaved.getCompanySince(), company.getCompanySince());
        assertEquals(companySaved.getSpecialNote(), company.getSpecialNote());
        assertEquals(companySaved.getCompanyName(), company.getCompanyName());

    }

    /**
     * To test companyRepository.findAll method. Here it just creates first two company records and 
     * expecting to receive two records from company repository.
     */
    @Test
    public void testFindAll() {

        Company company1 = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        //SAVE a Company
        companyRepository.save(company1);

        Company company2 = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        //SAVE second Company
        companyRepository.save(company2);

        List<Company> companyList = (List<Company>) companyRepository.findAll();

        assertNotNull(companyList);
        assertEquals(companyList.size(), 2);
    }

    /**
     * To test companyRepository.findOne method
     */
    @Test
    public void testFindOne() {

        Company company1 = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        //SAVE a Company
        Company savedCompany = companyRepository.save(company1);

        Company findCompany = companyRepository.findOne(savedCompany.getCompanyId());

        assertNotNull(findCompany);
        assertEquals(findCompany.getSearchHelp(), "This is search help");
        assertEquals(findCompany.getCompanyName(), "Pepcus");
    }

    /**
     * To test delete method
     */
    @Test
    public void testDeleteForSuccess() {
        Company company1 = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        //SAVE a Company
        Company savedCompany = companyRepository.save(company1);

        //DELETING record here.
        companyRepository.delete(savedCompany);

        //FIND saved company with find and it should not  return
        Company findCompany = companyRepository.findOne(savedCompany.getCompanyId());
        assertEquals(null, findCompany);
    }

    /**
     * To test delete method when exception is thrown
     */
    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteForFailure() {
        Integer companyId = 1;	// No record is available in H2 DB
        // DELETING record here. 
        companyRepository.delete(companyId);
    }

    /**
     * To verify companyRepository.save method when updating company.
     * 
     */

    @Test
    public void testSaveForUpdate(){
        Company company = createCompany(null, "Pepcus", "Software", "345345435", new Date(), "Special",
                "This is search help", "Other", "10");

        // Updating company name
        company.setCompanyName("Pepcus - Updated");

        Company updatedCompany = null;
        try {
            updatedCompany = companyRepository.save(company);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals(company.getCompanyId(), updatedCompany.getCompanyId());
        assertEquals("Pepcus - Updated", updatedCompany.getCompanyName());
    }

    /**
     * Test companyRepository.pageable with limit = 5 
     * @throws Exception
     */
    @Test
    public void testFindAllWithPageableWithLimit() throws Exception {

        for (Company company : createCompanies()) {
            companyRepository.save(company);
        }

        Pageable pageable = getPageable(0, 5, null, DEFAULT_SORT_BY_COMPANY_NAME);

        Page<Company> companies  = companyRepository.findAll(null, pageable);

        assertNotNull(companies.getContent());
        assertEquals(companies.getContent().size(), 5);
    }

    /**
     * Test companyRepository.pageable with offset = 5 
     * @throws Exception
     */
    @Test
    public void testFindAllWithPageableWithOffset() throws Exception {

        for (Company company : createCompanies()) {
            companyRepository.save(company);
        }

        Pageable pageable = getPageable(5, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        Page<Company> companies  = companyRepository.findAll(null, pageable);

        assertNotNull(companies.getContent());
        assertEquals(companies.getContent().size(), 5); //As offset = 5, so it will pick records by 5th 
    }

    /**
     * Junit to verify search specification
     * 
     * @throws Exception
     */
    @Test
    public void testFindAllWithSpecification() throws Exception {

        for (Company company : createCompanies()) {
            companyRepository.save(company);
        }

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        EntitySearchSpecification<Company> specification = (EntitySearchSpecification<Company>) EntitySearchUtil.
                getEntitySearchSpecification("General Electric", null, Company.class, new Company());

        Page<Company> companies  = companyRepository.findAll(specification, pageable);

        assertNotNull(companies.getContent());
        assertEquals(1, companies.getContent().size()); //As we have only one record have searchKey = "pep"
    }

    /**
     * Test to verify CompanyRepository.findFirstByCompanyNameAndBroker method.
     * 
     * It creates multiple records for company and expects first company record
     * with given parameters companyName and brokerId.
     * 
     * Finds the company record successfully.
     * 
     */
    @Test
    public void testFindFirstByCompanyNameAndBrokerIfCompanyExists() {
        
        for (Company company : createCompaniesWitNameAndBroker()) {
            companyRepository.save(company);
        }
        String companyName = "Pepcus";
        Integer brokerId = 1;

        Company company = companyRepository.findFirstByCompanyNameAndBroker(companyName, brokerId);
        
        assertNotNull(company.getCompanyId());
        assertEquals("Pepcus", company.getCompanyName()); 
        assertEquals(1, company.getBroker().intValue());
        assertEquals("Electric", company.getCompanyType());
        assertEquals("This is search help6", company.getSearchHelp());
    }
    
    /**
     * Test to verify CompanyRepository.findFirstByCompanyNameAndBroker method.
     * 
     * It creates multiple records for company and expects first company record
     * with given parameters companyName and brokerId.
     * 
     * Not able to find the company record.
     * 
     */
    @Test
    public void testFindFirstByCompanyNameAndBrokerIfCompanyNotExists() {
        
        for (Company company : createCompaniesWitNameAndBroker()) {
            companyRepository.save(company);
        }
        String companyName = "Pepcus";
        Integer broker = 2;

        Company company = companyRepository.findFirstByCompanyNameAndBroker(companyName, broker);
        
        assertEquals(null, company);
    }

    /**
     * Test to verify
     * CompanyRepository.findFirstByCompanyNameAndCustom1AndBroker method.
     * 
     * It creates multiple records for company and expects first company record
     * with given parameters companyName,custom1 and brokerId.
     * 
     * Finds the company record successfully.
     * 
     */
    @Test
    public void testFindFirstByCompanyNameAndCustom1AndBrokerIfCompanyExists() {

        for (Company company : createCompaniesWitNameAndBroker()) {
            companyRepository.save(company);
        }

        String companyName = "Pepcus";
        String custom1 = "123213";
        Integer broker = 1;

        Company company = companyRepository
                .findFirstByCompanyNameAndCustom1AndBroker(companyName, custom1,
                        broker);

        assertNotNull(company.getCompanyId());
        assertEquals("Pepcus", company.getCompanyName());
        assertEquals(1, company.getBroker().intValue());
        assertEquals("123213", company.getCustom1());
        assertEquals("IT", company.getCompanyType());
        assertEquals("This is search help10", company.getSearchHelp());
    }

    /**
     * Test to verify
     * CompanyRepository.findFirstByCompanyNameAndCustom1AndBroker method.
     * 
     * It creates multiple records for company and expects first company record
     * with given parameters companyName,custom1 and brokerId.
     * 
     * Not able to find the company record.
     * 
     */
    @Test
    public void testFindFirstByCompanyNameAndCustom1AndBrokerIfCompanyNotExists() {

        for (Company company : createCompaniesWitNameAndBroker()) {
            companyRepository.save(company);
        }

        String companyName = "Pepcus";
        String custom1 = "123213";
        Integer broker = 2;

        Company company = companyRepository
                .findFirstByCompanyNameAndCustom1AndBroker(companyName, custom1,
                        broker);

        assertEquals(null, company);
    }


}