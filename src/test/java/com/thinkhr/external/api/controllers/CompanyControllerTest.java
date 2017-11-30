package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.COMPANY_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanies;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyIdResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompanyResponseEntity;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

        List<Company> companyList = createCompanies();

        given(companyController.getAllCompany(Mockito.any(Integer.class), 
                Mockito.any(Integer.class), 
                Mockito.any(String.class), 
                Mockito.any(String.class), 
                Mockito.any(Map.class))).willReturn(companyList);

        mockMvc.perform(get(COMPANY_API_BASE_PATH+"?limit=10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("limit", is("10")))
        .andExpect(jsonPath("sort", is("companyName ASC")))
        .andExpect(jsonPath("offset", is("0")));
    }

    /**
     * Test to verify Get All Companies API (/v1/companies) when No records are available
     * 
     * @throws Exception
     */
    @Test
    public void testAllCompanyWithEmptyResponse() throws Exception {

        List<Company> companyList = null;

        given(companyController.getAllCompany(null, null, null, null, null)).willReturn(companyList);

        mockMvc.perform(get(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("message", IsNot.not(""))); 
    }

    /**
     * Test to verify Get company by id API (/v1/companies/{companyId}). 
     * 
     * @throws Exception
     */
    @Test
    public void testGetCompanyById() throws Exception {
        Company company = createCompany(); 

        given(companyController.getById(company.getCompanyId())).willReturn(company);

        mockMvc.perform(get(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("company.companyName", is(company.getCompanyName())))
        .andExpect(jsonPath("company.companyId", is(company.getCompanyId())));

    }

    /**
     * Test to verify Get company by id API (/v1/companies/{companyId}). 
     * API should return NOT_FOUND as response code
     * 
     * @throws Exception
     */
    @Test
    public void testGetCompanyByIdNotExists() throws Exception {
        Integer companyId = 1;

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
        Company company = createCompany(); 

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(company, HttpStatus.CREATED);

        given(companyController.addCompany(Mockito.any(Company.class))).willReturn(responseEntity);

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("company.companyName", is(company.getCompanyName())));
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
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getSearchHelp())));
    }

    /**
     * Test to verify post company API (/v1/companies) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddCompanyCompanyTypeNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanyType(null);

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companyType")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanyType())));
    }

    /**
     * Test to verify post company API (/v1/companies) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddCompanyCompanyNameNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanyName(null);

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companyName")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanyName())));
    }

    /**
     * Test to verify post company API (/v1/companies) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddCompanyCompanySinceNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanySince(null);

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companySince")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanySince())));
    }

    /**
     * Test to verify post company API (/v1/companies) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddCompanySpecialNoteNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setSpecialNote(null);

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("specialNote")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getSpecialNote())));
    }

    /**
     * Test to verify post company API (/v1/companies) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddCompanyCompanySinceInvalidBadRequest() throws Exception {

        Company company = createCompany(); 

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = simpleDateFormat.parse("2011-07-11");

        company.setCompanySince(date);

        String request = getJsonString(company);
        request = request.replaceAll("2011-07-11", "08/07/2011"); //As mm/dd/yyyy is not supported date format

        mockMvc.perform(post(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.MALFORMED_JSON_REQUEST.getCode().toString())));
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
        Company company = createCompany(); 

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(company, HttpStatus.OK);

        given(companyController.updateCompany(company.getCompanyId(), company)).willReturn(responseEntity);

        mockMvc.perform(put(COMPANY_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isMethodNotAllowed());
    }


    /**
     * Test to verify put company API (/v1/companies/{companyId}). 
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompany() throws Exception {
        Company company = createCompany(); 

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(company, HttpStatus.OK);

        given(companyController.updateCompany(Mockito.any(Integer.class), Mockito.any(Company.class))).willReturn(responseEntity);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("company.companyName", is(company.getCompanyName())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanySearchHelpNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setSearchHelp(null);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("searchHelp")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getSearchHelp())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanyCompanyTypeNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanyType(null);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companyType")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanyType())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanyCompanyNameNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanyName(null);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companyName")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanyName())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanyCompanySinceNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setCompanySince(null);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("companySince")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getCompanySince())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanySpecialNoteNullBadRequest() throws Exception {
        Company company = createCompany(); 
        company.setSpecialNote(null);

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(company)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.VALIDATION_FAILED.getCode().toString())))
        .andExpect(jsonPath("errorDetails[0].field", is("specialNote")))
        .andExpect(jsonPath("errorDetails[0].object", is("company")))
        .andExpect(jsonPath("errorDetails[0].rejectedValue", is(company.getSpecialNote())));
    }

    /**
     * Test to verify put company API (/v1/companies/{companyId}) with a In-valid request
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateCompanyCompanySinceInvalidBadRequest() throws Exception {
        Company company = createCompany(); 

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = simpleDateFormat.parse("2011-07-11");

        company.setCompanySince(date);

        String request = getJsonString(company);
        request = request.replaceAll("2011-07-11", "08/07/2011"); //As mm/dd/yyyy is not supported date format

        mockMvc.perform(put(COMPANY_API_BASE_PATH + company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errorCode", is(APIErrorCodes.MALFORMED_JSON_REQUEST.getCode().toString())));
    }


    /**
     * Test to verify delete company API (/v1/companies/{companyId}) . 
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteCompany() throws Exception {

        Company company = createCompany(); 

        ResponseEntity<Integer> responseEntity = createCompanyIdResponseEntity(company.getCompanyId(), HttpStatus.NO_CONTENT);

        given(companyController.deleteCompany(company.getCompanyId())).willReturn(responseEntity);

        mockMvc.perform(delete(COMPANY_API_BASE_PATH+company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(204));
    }

    /**
     * Test to verify delete company API (/v1/companies/{companyId}) for EntityNotFound
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteCompanyForEntityNotFound() throws Exception {

        Company company = createCompany(); 

        given(companyController.deleteCompany(company.getCompanyId())).willThrow(ApplicationException.
                createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, String.valueOf(company.getCompanyId())));

        mockMvc.perform(delete(COMPANY_API_BASE_PATH+company.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }


}
