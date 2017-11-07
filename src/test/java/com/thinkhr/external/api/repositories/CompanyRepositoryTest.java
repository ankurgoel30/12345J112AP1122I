package com.thinkhr.external.api.repositories;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

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
		   //TODO: Add implementation	
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