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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

	/**
	 * To test findAll method
	 */
	@Test
	public void testFindAll() {
		Company company1 = ApiTestDataUtil.createCompany(null, "HDFC", "Banking", "HHH");
		company1.setSearchHelp("Test");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("111");
		
		companyRepository.save(company1);
		
		List<Company> companyList = (List<Company>) companyRepository.findAll();
		  
        assertNotNull(companyList);
        assertEquals(companyList.size(), 1);
        assertEquals(companyList.get(0).getCompanyName(), company1.getCompanyName());
	}

	/**
	 * To test findOne method
	 */
	@Test
	public void testFindOne() {
		//TODO: Add implementation
	}
	
	/**
	 * To test save method
	 */
	@Test
	public void testSave() {
	   //TODO: Add implementation	
	}
	
	/**
	 * To test delete method
	 */
	@Test
	public void testDelete() {
		//TODO: Add implementation
	}
}