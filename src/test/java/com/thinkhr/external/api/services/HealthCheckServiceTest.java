package com.thinkhr.external.api.services;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.repositories.CompanyRepository;
import com.thinkhr.external.api.response.HealthCheckResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Junit to test all the methods of HealthCheckService.
 * 
 * @author Sudhakar Kaki
 * @since 2017-11-18
 *
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class HealthCheckServiceTest {

    @InjectMocks
    private HealthCheckService healthCheckService;

    @Value("${application.version}")
    private String version;

    @Autowired
    private ApplicationContext ac;

    @Before
    public void setup(){
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test to verify Get HealthCheck API (/v1/healthcheck/{companyId}).
	 *
	 * @throws Exception
	 */
	@Test
	public void testHealthCheckAPI() throws Exception {
        healthCheckService.setAppContext(ac);
	    HealthCheckResponse result = healthCheckService.getHeartBeat(1);
	    Integer r = result.getStatus();
		assertTrue(r==1);
	}
}
