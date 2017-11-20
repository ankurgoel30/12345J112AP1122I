package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.LIMIT;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.OFFSET;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.SEARCH_SPEC;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.SORT_BY;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
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
	
	private String defaultSortField = "+companyName";
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * To verify getAllCompany method when no specific (Default) method arguments are provided 
	 * 
	 */
	@Test
	public void testGetAllCompany(){
		List<Company> companyList = new ArrayList<Company>();
		companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
		companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
		companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));
		Pageable pageable = getPageable(null, null, null, defaultSortField);
		
		when(companyRepository.findAll(null, pageable)).thenReturn(new PageImpl<Company>(companyList, pageable, companyList.size()));

		try {
			List<Company> result =  companyService.getAllCompany(null, null, null, null, null);
			assertEquals(3, result.size());
		} catch (ApplicationException ex) {
			fail("Not expected exception");
		}
		
		//TODO: ADD MORE test cases to verify limit, offset, sort and other search parameters.
	}
	
	/**
	 * To verify getAllCompany method when specific method arguments are provided
	 * 
	 */
	@Test
	public void testGetAllCompanyForParams(){
		List<Company> companyList = new ArrayList<Company>();
		companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
		companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
		companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));
		Pageable pageable = getPageable(OFFSET, LIMIT, SORT_BY, defaultSortField);
		Specification<Company> spec = null;
    	if(SEARCH_SPEC != null && SEARCH_SPEC.trim() != "") {
    		spec = new EntitySearchSpecification(SEARCH_SPEC, new Company());
    	}
		when(companyRepository.findAll(spec, pageable)).thenReturn(new PageImpl<Company>(companyList, pageable, companyList.size()));

		List<Company> result;
		try {
			result = companyService.getAllCompany(OFFSET, LIMIT, SORT_BY, SEARCH_SPEC, null);
			assertEquals(3, result.size());
		} catch (ApplicationException e) {
			fail("Not expecting application exception for a valid test case");
		}

	}
	
	/**
	 * To verify createCompany method
	 * 
	 */
	@Test
	public void testGetCompany() {
		Integer companyId = 1;
		Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		when(companyRepository.findOne(companyId)).thenReturn(company);
		Company result = companyService.getCompany(companyId);
		assertEquals(companyId, result.getCompanyId());
		assertEquals("Pepcus", result.getCompanyName());
		assertEquals("Software", result.getCompanyType());
		assertEquals("PEP", result.getDisplayName());
	}
	
	/**
	 * To verify createCompany method
	 * 
	 */
	@Test
	public void testGetCompanyNotExists() {
		Integer companyId = 1;
		when(companyRepository.findOne(companyId)).thenReturn(null);
		Company result = companyService.getCompany(companyId);
		assertNull("companyId " + companyId + " does not exist", result);
	}
	
	/**
	 * To verify addCompany method
	 * 
	 */
	@Test
	public void testAddCompany() {
		//When all data is correct, it should assert true 
		Integer companyId = 1;
		Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		
		when(companyRepository.save(company)).thenReturn(company);
		
		Company result = companyService.addCompany(company);
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

		Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

		when(companyRepository.save(company)).thenReturn(company);
		when(companyRepository.findOne(companyId)).thenReturn(company);
		Company result = null;
		try {
			result = companyService.updateCompany(company);
		} catch (ApplicationException e) {
			fail("Not expecting application exception for a valid test case");
		}
		assertEquals(companyId, result.getCompanyId());
		assertEquals("Pepcus", result.getCompanyName());
		assertEquals("Software", result.getCompanyType());
		assertEquals("PEP", result.getDisplayName());
	}
	
	/**
	 * To verify updateCompany method when companyRepository doesn't find a match for given companyId.
	 * 
	 * 
	 */
	
	@Test
	public void testUpdateCompanyForEntityNotFound(){
		Integer companyId = 1;
		Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
		when(companyRepository.findOne(companyId)).thenReturn(null);
		try {
			companyService.updateCompany(company);
		} catch (ApplicationException e) {
			assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
		}
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

	/**
	 * To verify deleteCompany method throws ApplicationException when internally companyRepository.delete method throws exception.
	 * 
	 */
	@Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
	public void testDeleteCompanyForEntityNotFound() {
		int companyId = 1 ;
		doThrow(new EmptyResultDataAccessException("Not found", 1)).when(companyRepository).delete(companyId);
		companyService.deleteCompany(companyId);
	}

}
