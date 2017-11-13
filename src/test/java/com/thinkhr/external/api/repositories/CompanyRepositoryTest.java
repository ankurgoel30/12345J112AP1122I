package com.thinkhr.external.api.repositories;

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
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
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
	

	/**
	 * To test save method
	 */
	@Test
	public void testSave() {
		Company company = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH");
		company.setSearchHelp("Test");
		company.setCompanySince(new Date());
		company.setSpecialNote("111");
		
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
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH");
		company1.setSearchHelp("Test");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("111");
		
		//SAVE a Company
		companyRepository.save(company1);

		Company company2 = ApiTestDataUtil.createCompany(null, "PEPCUS", "IT", "PEP");
		company2.setSearchHelp("PEPTEST");
		company2.setCompanySince(new Date());
		company2.setSpecialNote("22");

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
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH");
		company1.setSearchHelp("Test");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("111");
		
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
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH");
		company1.setSearchHelp("Test");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("111");
		
		//SAVE a Company
		Company savedCompany = companyRepository.save(company1);

		//DELETING record here.
		companyRepository.delete(savedCompany);
		
		//FIND saved company with find and it should not  return
		Company findCompany = (Company) companyRepository.findOne(savedCompany.getCompanyId());
		assertEquals(null, findCompany);
	}
}