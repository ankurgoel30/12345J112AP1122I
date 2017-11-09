package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.COMPANY_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hibernate.JDBCException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

/**
 * Junit class to test all the methods\APIs written for CompanyController
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class CompanyControllerTest {

	private MockMvc mockMvc;

	@MockBean
	private CompanyController companyController;
	
	@Autowired
    private WebApplicationContext wac;

	@Before
	public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	/**
	 * Test to verify Get companies API (/v1/companies) when no request parameters (default) are provided  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompany() throws Exception {
		
		Company Company = createCompany(); 

		List<Company> companyList = singletonList(Company);

		given(companyController.getAllCompany(null, null, null, null)).willReturn(companyList);
		
		mockMvc.perform(get(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].companyName", is(Company.getCompanyName())));	
	}
	/**
	 * Test to verify Get All Companies API (/v1/companies) when No records are available
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompanyWithEmptyResponse() throws Exception {
		
		List<Company> companyList = null;

		given(companyController.getAllCompany(null, null, null, null)).willReturn(companyList);
		
		mockMvc.perform(get(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	/**
	 * Test to verify Get All Companies API (/v1/companies) when service layer throws an exception.
	 * API status code should be 400 for bad request.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompanyWithException() throws Exception {
		
		List<Company> companyList = singletonList(createCompany());

		given(companyController.getAllCompany(null, null, null, null)).willThrow(JDBCException.class);
		
		mockMvc.perform(get(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$[0].errorCode", is(APIErrorCodes.DATABASE_ERROR.getCode())));
	}

	
	/**
	 * Test to verify Get company by id API (/v1/companies/{companyId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCompanyById() throws Exception {
		Company Company = createCompany(); 
		
		given(companyController.getById(Company.getCompanyId())).willReturn(Company);

		mockMvc.perform(get(COMPANY_API_BASE_PATH + Company.getCompanyId())
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("companyName", is(Company.getCompanyName())));
	}
	
	/**
	 * Test to verify Get company by id API (/v1/companies/{companyId}). 
	 * API should return NOT_FOUND as response code
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCompanyByIdNotExists() throws Exception {
		Integer companyId = new Integer(15);
		
		given(companyController.getById(companyId)).willThrow(ApplicationException.
				createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId=" + companyId));

		MvcResult result = mockMvc.perform(get(COMPANY_API_BASE_PATH + companyId)
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andReturn();
		
		int status = result.getResponse().getStatus();
		assertEquals("Incorrest Response Status", HttpStatus.NOT_FOUND.value(), status);
	}

	/**
	 * Test to verify post company API (/v1/companies) with a valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCompany() throws Exception {
		Company Company = createCompany(); 
		
		ResponseEntity<Company> responseEntity = createCompanyResponseEntity(Company, HttpStatus.CREATED);
		
		//given(companyController.addCompany(Company)).willReturn(responseEntity);

		mockMvc.perform(post(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(Company)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("companyName", is(Company.getCompanyName())));
	}

	/**
	 * Test to verify post company API (/v1/companies) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCompanySearchHelpNullBadRequest() throws Exception {
		Company company = createCompany(); 
		company.setSearchHelp(null);
		
		mockMvc.perform(post(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(company)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("searchHelp")))
		.andExpect(jsonPath("errorDetails[0].object", is("company")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is("null")))
		;
	}
	
	/**
	 * Test to verify post company API (/v1/companies) with a In-valid request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCompanyCompanyTypeNullBadRequest() throws Exception {
		Company company = createCompany(); 
		company.setSearchHelp(null);
		
		mockMvc.perform(post(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(company)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
		.andExpect(jsonPath("errorDetails[0].field", is("searchHelp")))
		.andExpect(jsonPath("errorDetails[0].object", is("company")))
		.andExpect(jsonPath("errorDetails[0].rejectedValue", is("null")))
		;
	}
	
	/**
	 * Test to verify put company API (/v1/companies/{companyId}) without passing
	 * companyId to path parameter.
	 * 
	 * Expected - Should return 404 Not found response code
	 * @throws Exception
	 */
	@Test
	public void testUpdateCompanyWithNoCompanyIdInPath() throws Exception {
		Company Company = createCompany(); 
		
		ResponseEntity<Company> responseEntity = createCompanyResponseEntity(Company, HttpStatus.OK);
		
		given(companyController.updateCompany(Company.getCompanyId(), Company)).willReturn(responseEntity);

		mockMvc.perform(put(COMPANY_API_BASE_PATH)
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(Company)))
		.andExpect(status().isMethodNotAllowed());
	}


	/**
	 * Test to verify put company API (/v1/companies/{companyId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateCompany() throws Exception {
		Company Company = createCompany(); 
		
		ResponseEntity<Company> responseEntity = createCompanyResponseEntity(Company, HttpStatus.OK);
		
		given(companyController.updateCompany(Company.getCompanyId(), Company)).willReturn(responseEntity);

		mockMvc.perform(put(COMPANY_API_BASE_PATH+Company.getCompanyId())
			   .accept(MediaType.APPLICATION_JSON)
			   .contentType(MediaType.APPLICATION_JSON)
		       .content(getJsonString(Company)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("companyName", is(Company.getCompanyName())));
	}

	
	/**
	 * Test to verify delete company API (/v1/companies/{companyId}) . 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteCompany() throws Exception {
		
		Company Company = createCompany(); 
		
		ResponseEntity<Integer> responseEntity = createCompanyIdResponseEntity(Company.getCompanyId(), HttpStatus.NO_CONTENT);

		given(companyController.deleteCompany(Company.getCompanyId())).willReturn(responseEntity);

		mockMvc.perform(delete(COMPANY_API_BASE_PATH+Company.getCompanyId())
			   .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(204));
	}

}
