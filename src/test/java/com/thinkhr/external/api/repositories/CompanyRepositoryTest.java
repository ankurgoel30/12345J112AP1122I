package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanies;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
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
	 * To test companyRepository.save method. 
	 */
	@Test
	public void testSave() {
		Company company = createCompany();
		
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
		
		Company company1 = createCompany(null, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		
		//SAVE a Company
		companyRepository.save(company1);

		Company company2 = createCompany(null, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp");

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
		
		Company company1 = createCompany(null, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		
		//SAVE a Company
		Company savedCompany = companyRepository.save(company1);

		Company findCompany = (Company) companyRepository.findOne(savedCompany.getCompanyId());
		
		assertNotNull(findCompany);
		assertEquals(findCompany.getSearchHelp(), "PepcusHelp");
		assertEquals(findCompany.getCompanyName(), "Pepcus");
	}
	
	/**
	 * To test delete method
	 */
	@Test
	public void testDelete() {
		Company company1 = createCompany(null, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		
		//SAVE a Company
		Company savedCompany = companyRepository.save(company1);

		//DELETING record here.
		companyRepository.delete(savedCompany);
		
		//FIND saved company with find and it should not  return
		Company findCompany = (Company) companyRepository.findOne(savedCompany.getCompanyId());
		assertEquals(null, findCompany);
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

    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(null, pageable);
    	
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

    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(null, pageable);
    	
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
		
		Pageable pageable = getPageable(null, null, "+companyType", DEFAULT_SORT_BY_COMPANY_NAME);

		Specification specification = EntitySearchUtil.getEntitySearchSpecification("Pepcus", null, Company.class, new Company());
		
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(specification, pageable);
    	
    	assertNotNull(companies.getContent());
    	assertEquals(1, companies.getContent().size()); //As we have only one record have searchKey = "pep"
	}

}