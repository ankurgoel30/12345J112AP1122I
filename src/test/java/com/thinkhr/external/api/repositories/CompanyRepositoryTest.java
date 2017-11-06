package com.thinkhr.external.api.repositories;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;

/**
 * Junit to verify methods of CompanyRepository with use of H2 database
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class CompanyRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CompanyRepository companyRepository;

	/**
	 * To test findAll method
	 */
	@Test
	public void testFindAll() {
		//given
		Company company1 = createCompany(null, "Pepcus", "Software", "PEP");
		company1.setSearchHelp("Test");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("111");
		entityManager.persist(company1);
		entityManager.flush();
		
		//when
		List<Company> companies = (List)companyRepository.findAll();

		//then
		assertThat(companies.size()).isGreaterThan(0);
	}

	/**
	 * To test findOne method
	 */
	@Test
	public void testFindOne() {
		//given
		Company company1 = createCompany(null, "Pepcus2", "Software", "PEP2");
		company1.setSearchHelp("Test3");
		company1.setCompanySince(new Date());
		company1.setSpecialNote("TEST3");
		entityManager.persist(company1);
		entityManager.flush();

		//when
		Company compFromRepo = companyRepository.findOne(company1.getCompanyId());

		//then
		assertThat(compFromRepo.getCompanyName()).isEqualTo(compFromRepo.getCompanyName());
	}
	
	@Test
	public void testSave() {
	   //TODO: Add implementation	
	}
	
	@Test
	public void testDelete() {
		//TODO: Add implementation
	}
}