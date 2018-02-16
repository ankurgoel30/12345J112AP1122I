package com.thinkhr.external.api.controllers;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.CONFIG_API_BASE_PATH;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createConfiguration;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createConfigurationList;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.getJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.utils.ApiTestDataUtil;
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
public class ConfigurationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ConfigurationController configurationController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * Test to verify Get Configuration by id API (/v1/configurations/{configurationId}). 
     * 
     * @throws Exception
     */
    @Test
    public void test_GetConfigurationById() throws Exception {
        Integer brokerId = 12345;
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0, "test description");

        given(configurationController.getById(configuration.getConfigurationId(), brokerId)).willReturn(configuration);

        mockMvc.perform(get(CONFIG_API_BASE_PATH + configuration.getConfigurationId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test to verify post configuration API (/v1/configurations) with a valid request
     * 
     * @throws Exception
     */
    @Test
    public void test_AddConfiguration() throws Exception {

        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0, "test description");

        ResponseEntity<Configuration> responseEntity = ApiTestDataUtil.createConfigurationResponseEntity(configuration,
                HttpStatus.CREATED);

        given(configurationController.addConfiguration(Mockito.any(Configuration.class), Mockito.anyInt())).willReturn(responseEntity);

        mockMvc.perform(post(CONFIG_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(configuration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("configuration.masterConfiguration", is(0)))
                .andExpect(jsonPath("configuration.companyId", is(configuration.getCompanyId())));
    }


    /**
     * Test to verify put broker API (/v1/configurations/{configurationId}). 
     * 
     * @throws Exception
     */
    @Test
    public void test_UpdateConfiguration() throws Exception {
        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", 0, "test description");

        ResponseEntity<Configuration> responseEntity = ApiTestDataUtil.createConfigurationResponseEntity(configuration,
                HttpStatus.OK);

        given(configurationController.updateConfiguration(Mockito.any(Integer.class), Mockito.any(String.class), Mockito.any(Integer.class)))
                .willReturn(responseEntity);

        mockMvc.perform(put(CONFIG_API_BASE_PATH + configuration.getConfigurationId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(configuration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("configuration.masterConfiguration", is(0)))
                .andExpect(jsonPath("configuration.companyId", is(configuration.getCompanyId())));
    }

    /**
     * Test to verify delete broker API (/v1/configurations/{configurationId}) . 
     * 
     * @throws Exception
     */
    @Test
    public void test_DeleteConfiguration() throws Exception {

        Configuration configuration = createConfiguration(1, 2, "ABC", "test config", "test description");

        ResponseEntity<Integer> responseEntity = ApiTestDataUtil.createConfigurationIdResponseEntity(
                configuration.getConfigurationId(),
                HttpStatus.ACCEPTED);

        given(configurationController.deleteConfiguration(Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(responseEntity);

        mockMvc.perform(delete(CONFIG_API_BASE_PATH + configuration.getConfigurationId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }
    
    /**
     * Test to verify get Configurations API (/v1/configurations) . 
     * 
     * @throws Exception
     */
    @Test
    public void test_GetConfigurations() throws Exception {
        
        Integer brokerId = 12345;
        
        List<Configuration> configurationList = createConfigurationList();
        
        given(configurationController.getConfigurations(null, null, null, null, null, brokerId))
            .willReturn(configurationList);
        
        mockMvc.perform(get(CONFIG_API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", IsNot.not(""))); 
    }
}