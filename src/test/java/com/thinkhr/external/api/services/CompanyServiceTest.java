package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyModel;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.ApplicationException;
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
		List<Company> companyList = new ArrayList<Company>();
		companyList.add(createCompany(1, "Pepcus", "Software", "PEP"));
		companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR"));
		companyList.add(createCompany(3, "ICICI", "Banking", "ICICI"));
		Pageable pageable = new PageRequest(0, 10);
		when(companyRepository.findAll(pageable)).thenReturn(new PageImpl<>(companyList, pageable, companyList.size()));
		
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
		when(companyRepository.findOne(companyId)).thenReturn(company);
		CompanyModel result = null;
		try {
			result = companyService.updateCompany(companyModel);
		} catch (ApplicationException e) {
		}
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
	public void testDeleteCompany() {
		Integer companyId = 1;
		try {
			companyService.deleteCompany(companyId);
		} catch (ApplicationException e) {
		}
        verify(companyRepository, times(1)).delete(companyId);
	}


}
