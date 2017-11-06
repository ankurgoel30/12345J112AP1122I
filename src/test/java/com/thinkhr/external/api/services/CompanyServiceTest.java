package com.thinkhr.external.api.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.model.CompanyModel;
import com.thinkhr.external.api.repositories.CompanyRepository;

/**
 * Junit to test all the methods of CompanyService.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */

@RunWith(SpringRunner.class)
public class CompanyServiceTest {
	
	@Mock
	private CompanyRepository companyRepository;
	
	@InjectMocks
	private CompanyService companyService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		companyService.setModelMapper(new ModelMapper());
	}
	
	/**
	 * To verify getAllCompany method
	 * 
	 */
	@Test
	public void testGetAllCompany(){
		List<Company> toDoList = new ArrayList<Company>();
		toDoList.add(createCompany(1, "Pepcus", "Software", "PEP"));
		toDoList.add(createCompany(2, "ThinkHR", "Service Provider", "THR"));
		toDoList.add(createCompany(3, "ICICI", "Banking", "ICICI"));
		when(companyRepository.findAll()).thenReturn(toDoList);
		
		List<CompanyModel> result = companyService.getAllCompany();
		assertEquals(3, result.size());
	}
	
	/**
	 * To verify createCompany method
	 * 
	 */
	@Test
	public void testGetCompany(){
		Integer companyId = 1;
		Company company = createCompany(companyId, "Pepcus", "Software", "PEP");
		when(companyRepository.findOne(companyId)).thenReturn(company);
		CompanyModel result = companyService.getCompany(companyId);
		assertEquals(companyId, result.getCompanyId());
		assertEquals("Pepcus", result.getCompanyName());
		assertEquals("Software", result.getCompanyType());
		assertEquals("PEP", result.getDisplayName());
	}
	
	/**
	 * To verify addCompany method
	 * 
	 */
	@Test
	public void testAddCompany(){
		Integer companyId = 1;
		CompanyModel companyModel = createCompanyModel(1, "Pepcus", "Software", "PEP");
		Company company = (Company)companyService.convert(companyModel, Company.class);
		when(companyRepository.save(company)).thenReturn(company);
		CompanyModel result = companyService.addCompany(companyModel);
		assertEquals(companyId, result.getCompanyId());
		assertEquals("Pepcus", result.getCompanyName());
		assertEquals("Software", result.getCompanyType());
		assertEquals("PEP", result.getDisplayName());
	}

	/**
	 * To verify updateCompany method
	 * 
	 */
	
	@Test
	public void testUpdateCompany(){
		Integer companyId = 1;
		CompanyModel companyModel = createCompanyModel(1, "Pepcus", "Software", "PEP");
		Company company = (Company)companyService.convert(companyModel, Company.class);
		when(companyRepository.save(company)).thenReturn(company);
		CompanyModel result = companyService.updateCompany(companyModel);
		assertEquals(companyId, result.getCompanyId());
		assertEquals("Pepcus", result.getCompanyName());
		assertEquals("Software", result.getCompanyType());
		assertEquals("PEP", result.getDisplayName());
	}
	
	/**
	 * To verify deleteCompany method
	 * 
	 */
	@Test
	public void testDeleteCompany(){
		Integer companyId = 1;
		companyService.deleteCompany(companyId);
        verify(companyRepository, times(1)).delete(companyId);
	}


}
