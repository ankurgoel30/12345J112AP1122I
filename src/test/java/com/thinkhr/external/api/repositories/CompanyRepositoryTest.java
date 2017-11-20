package com.thinkhr.external.api.repositories;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanies;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.thinkhr.external.api.services.EntitySearchSpecification;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to verify methods of CompanyRepository with use of H2 database
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
	
	private Integer savedCompanyId;
	
	private String defaultSortField = "+companyName";
	

	/**
	 * To test save method
	 */
	@Test
	public void testSave() {
		Company company = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH", "Test");
		
		Company companySaved = companyRepository.save(company);
		
		savedCompanyId = company.getCompanyId();
		assertNotNull(companySaved);
		assertNotNull(companySaved.getCompanyId());// As company is saved successfully.
		assertEquals(companySaved.getSearchHelp(), company.getSearchHelp());
		assertEquals(companySaved.getCompanySince(), company.getCompanySince());
		assertEquals(companySaved.getSpecialNote(), company.getSpecialNote());
		assertEquals(companySaved.getCompanyName(), company.getCompanyName());
		
	}
	
	/**
	 * To test findAll method
	 */
	@Test
	public void testFindAll() {
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH", "Test");
		
		//SAVE a Company
		companyRepository.save(company1);

		Company company2 = ApiTestDataUtil.createCompany(null, "PEPCUS", "IT", "PEP", "PEPTEST");

		//SAVE second Company
		companyRepository.save(company2);

		List<Company> companyList = (List<Company>) companyRepository.findAll();
		  
		assertNotNull(companyList);
		assertEquals(companyList.size(), 2);
	}

	/**
	 * To test findOne method
	 */
	@Test
	public void testFindOne() {
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH", "Test");
		
		//SAVE a Company
		Company savedCompany = companyRepository.save(company1);

		Company findCompany = (Company) companyRepository.findOne(savedCompany.getCompanyId());
		assertNotNull(findCompany);
		assertEquals(findCompany.getSearchHelp(), "Test");
		assertEquals(findCompany.getCompanyName(), "HDFC");
	}
	
	/**
	 * To test delete method
	 */
	@Test
	public void testDelete() {
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH", "PEPTEST");
		
		//SAVE a Company
		Company savedCompany = companyRepository.save(company1);

		//DELETING record here.
		companyRepository.delete(savedCompany);
		
		//FIND saved company with find and it should not  return
		Company findCompany = (Company) companyRepository.findOne(savedCompany.getCompanyId());
		assertEquals(null, findCompany);
	}
	
	/**
	 * Test to verify get all companies when no parameters are provided 
	 * i.e., all parameters are default provided.  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompaniesWithDefault() throws Exception {
		
		for (Company company : createCompanies()) {
			companyRepository.save(company);
		}
		
		String searchSpec = null;
		Pageable pageable = getPageable(null, null, null, defaultSortField);
    	Specification<Company> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<Company>(searchSpec, new Company());
    	}
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(spec, pageable);
    	
    	assertNotNull(companies.getContent());
    	assertEquals(companies.getContent().size(), 10);
	}

	/**
	 * Test to verify get all companies when searchSpec is default and all other 
	 * parameters are provided (sort is ascending)  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompaniesWithParamsAndSearchSpecNull() throws Exception {
		
		for (Company company : createCompanies()) {
			companyRepository.save(company);
		}
		
		String searchSpec = null;
		Pageable pageable = getPageable(3, 3, "+companyType", defaultSortField);
    	Specification<Company> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<Company>(searchSpec, new Company());
    	}
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(spec, pageable);
    	
    	assertNotNull(companies.getContent());
    	assertEquals(companies.getContent().size(), 3);
	}
	
	/**
	 * Test to verify get all companies searchSpec is provided and other parameters are default.  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompaniesWithParamsAndPageableNull() throws Exception {
		
		for (Company company : createCompanies()) {
			companyRepository.save(company);
		}
		
		String searchSpec = "help3";
		Pageable pageable = getPageable(null, null, null, defaultSortField);
    	Specification<Company> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<Company>(searchSpec, new Company());
    	}
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(spec, pageable);
    	
    	assertNotNull(companies.getContent());
    	assertEquals(companies.getContent().size(), 1);
	}
	
	/**
	 * Test to verify get all companies when all parameters are provided 
	 * and sort is ascending   
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompaniesWithParamsAndAscSort() throws Exception {
		
		for (Company company : createCompanies()) {
			companyRepository.save(company);
		}
		
		String searchSpec = "General";
		Pageable pageable = getPageable(0, null, "+companyType", defaultSortField);
    	Specification<Company> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<Company>(searchSpec, new Company());
    	}
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(spec, pageable);
    	
    	assertNotNull(companies.getContent());
    	assertEquals(companies.getContent().size(), 2);
	}
	
	/**
	 * Test to verify get all companies when all parameters are provided
	 * and sort is descending.  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompaniesWithParamsAndDescSort() throws Exception {
		
		for (Company company : createCompanies()) {
			companyRepository.save(company);
		}
		
		String searchSpec = "Suzuki";
		Pageable pageable = getPageable(null, null, "-companyType", defaultSortField);
    	Specification<Company> spec = null;
    	if(StringUtils.isNotBlank(searchSpec)) {
    		spec = new EntitySearchSpecification<Company>(searchSpec, new Company());
    	}
    	Page<Company> companies  = (Page<Company>) companyRepository.findAll(spec, pageable);
    	
    	
    	assertNotNull(companies.getContent());
    	assertEquals(companies.getContent().size(), 1);
	}
	
}