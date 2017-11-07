package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.COMPANY_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyModel;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.model.CompanyModel;

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
	 * Test to verify Get companies API (/v1/companies)  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllCompany() throws Exception {

		CompanyModel companyModel = createCompanyModel(); 

		List<CompanyModel> companyList = singletonList(companyModel);

		given(companyController.getAllCompany()).willReturn(companyList);
		
		mockMvc.perform(get(COMPANY_API_BASE_PATH).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].companyName", is(companyModel.getCompanyName())));	
	}
	
	/**
	 * Test to verify Get company by id API (/v1/companies/{companyId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCompanyById() throws Exception {
		CompanyModel companyModel = createCompanyModel(); 
		
		given(companyController.getById(companyModel.getCompanyId())).willReturn(companyModel);

		mockMvc.perform(get(COMPANY_API_BASE_PATH + companyModel.getCompanyId())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("companyName", is(companyModel.getCompanyName())));
	}

	/**
	 * Test to verify post company API (/v1/companies). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCompany() throws Exception {
		CompanyModel companyModel = createCompanyModel(); 
		
		ResponseEntity<CompanyModel> responseEntity = createCompanyResponseEntity(companyModel, HttpStatus.CREATED);
		
		given(companyController.addCompany(companyModel)).willReturn(responseEntity);

		mockMvc.perform(post(COMPANY_API_BASE_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		        .content(getJsonString(companyModel)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("companyName", is(companyModel.getCompanyName())));
	}

	/**
	 * Test to verify put company API (/v1/companies/{companyId}). 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateCompany() throws Exception {
		CompanyModel companyModel = createCompanyModel(); 
		
		ResponseEntity<CompanyModel> responseEntity = createCompanyResponseEntity(companyModel, HttpStatus.OK);
		
		given(companyController.updateCompany(companyModel.getCompanyId(), companyModel)).willReturn(responseEntity);

		mockMvc.perform(put(COMPANY_API_BASE_PATH+companyModel.getCompanyId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		        .content(getJsonString(companyModel)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("companyName", is(companyModel.getCompanyName())));
	}

	/**
	 * Test to verify delete company API (/v1/companies/{companyId}) . 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteCompany() throws Exception {
		
		CompanyModel companyModel = createCompanyModel(); 
		
		ResponseEntity<Integer> responseEntity = createCompanyIdResponseEntity(companyModel.getCompanyId(), HttpStatus.NO_CONTENT);

		given(companyController.deleteCompany(companyModel.getCompanyId())).willReturn(responseEntity);

		mockMvc.perform(delete(COMPANY_API_BASE_PATH+companyModel.getCompanyId())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(204));
	}

}
