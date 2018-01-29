package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.BROKER_API_BASE_PATH;
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

import java.util.List;
import java.util.Map;

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
 * Junit class to test all the methods\APIs written for BrokerController
 * 
 * @author Ajay Jain
 * @since 2018-01-24
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class BrokerControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BrokerController brokerController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * Test to verify Get companies API (/v1/brokers) when no request parameters (default) are provided  
     * 
     * @throws Exception
     */
    @Test
    public void testAllBroker() throws Exception {

        List<Company> brokerList = createCompanies();

        given(brokerController.getAllBrokers(Mockito.any(Integer.class),
                Mockito.any(Integer.class),
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(Map.class))).willReturn(brokerList);

        mockMvc.perform(get(BROKER_API_BASE_PATH + "?limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("limit", is("10")))
                .andExpect(jsonPath("sort", is("companyName ASC")))
                .andExpect(jsonPath("offset", is("0")));
    }

    /**
     * Test to verify Get All Brokers API (/v1/brokers) when No records are available
     * 
     * @throws Exception
     */
    @Test
    public void testAllBrokerWithEmptyResponse() throws Exception {

        List<Company> brokerList = null;

        given(brokerController.getAllBrokers(null, null, null, null, null)).willReturn(brokerList);

        mockMvc.perform(get(BROKER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", IsNot.not("")));
    }

    /**
     * Test to verify Get broker by id API (/v1/brokers/{brokerId}). 
     * 
     * @throws Exception
     */
    @Test
    public void testGetBrokerById() throws Exception {
        Company broker = createCompany();

        given(brokerController.getById(broker.getCompanyId())).willReturn(broker);

        mockMvc.perform(get(BROKER_API_BASE_PATH + broker.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("company.companyName", is(broker.getCompanyName())))
                .andExpect(jsonPath("company.companyId", is(broker.getCompanyId())));

    }

    /**
     * Test to verify Get broker by id API (/v1/brokers/{brokerId}). 
     * API should return NOT_FOUND as response code
     * 
     * @throws Exception
     */
    @Test
    public void testGetBrokerByIdNotExists() throws Exception {
        Integer brokerId = 1;

        given(brokerController.getById(brokerId)).willThrow(ApplicationException
                .createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "broker", "brokerId=" + brokerId));

        MvcResult result = mockMvc.perform(get(BROKER_API_BASE_PATH + brokerId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals("Incorrest Response Status", HttpStatus.NOT_FOUND.value(), status);
    }

    /**
     * Test to verify post broker API (/v1/brokers) with a valid request
     * 
     * @throws Exception
     */
    @Test
    public void testAddBroker() throws Exception {
        Company broker = createCompany();

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(broker, HttpStatus.CREATED);

        given(brokerController.addBroker(Mockito.any(Company.class))).willReturn(responseEntity);

        mockMvc.perform(post(BROKER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(broker)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("company.companyName", is(broker.getCompanyName())));
    }

    /**
     * Test to verify put broker API (/v1/brokers/{brokerId}) without passing
     * brokerId to path parameter.
     * 
     * Expected - Should return 404 Not found response code
     * @throws Exception
     */
    @Test
    public void testUpdateBrokerWithNoBrokerIdInPath() throws Exception {
        Integer brokerId = 10;
        Company broker = createCompany();

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(broker, HttpStatus.OK);
        String brokerJson = getJsonString(broker);

        given(brokerController.updateBroker(broker.getCompanyId(), brokerJson))
                .willReturn(responseEntity);

        mockMvc.perform(put(BROKER_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(broker)))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test to verify put broker API (/v1/brokers/{brokerId}). 
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateBroker() throws Exception {
        Company broker = createCompany();

        ResponseEntity<Company> responseEntity = createCompanyResponseEntity(broker, HttpStatus.OK);

        given(brokerController.updateBroker(Mockito.any(Integer.class), Mockito.any(String.class)))
                .willReturn(responseEntity);

        mockMvc.perform(put(BROKER_API_BASE_PATH + broker.getCompanyId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(broker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("company.companyName", is(broker.getCompanyName())));
    }

    /**
     * Test to verify delete broker API (/v1/brokers/{brokerId}) . 
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteBroker() throws Exception {

        Company broker = createCompany();

        ResponseEntity<Integer> responseEntity = createCompanyIdResponseEntity(broker.getCompanyId(),
                HttpStatus.ACCEPTED);

        given(brokerController.deleteBroker(broker.getCompanyId())).willReturn(responseEntity);

        mockMvc.perform(delete(BROKER_API_BASE_PATH + broker.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    /**
     * Test to verify delete broker API (/v1/brokers/{brokerId}) for EntityNotFound
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteBrokerForEntityNotFound() throws Exception {

        Company broker = createCompany();

        given(brokerController.deleteBroker(broker.getCompanyId())).willThrow(ApplicationException
                .createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, String.valueOf(broker.getCompanyId())));

        mockMvc.perform(delete(BROKER_API_BASE_PATH + broker.getCompanyId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
