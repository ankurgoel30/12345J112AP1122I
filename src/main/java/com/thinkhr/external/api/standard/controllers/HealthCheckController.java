package com.thinkhr.external.api.standard.controllers;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.response.HealthCheckResponse;
import com.thinkhr.external.api.services.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



/**
 * HealthCheck Controller for performing operations
 * related System Health.
 * 
 * @author Sudhakar Kaki
 * @since 2017-11-05
 * 
 * 
 */
@RestController
@RequestMapping(path="/v1/healthcheck")
public class HealthCheckController {
	
    @Autowired
	HealthCheckService healthCheckService;

    @Autowired
    private ApplicationContext appContext;

    /**
     * Get System Health for a given company id
     *
     * @param id clientId
     * @return HealthCheckResponse object
     * @throws ApplicationException
     *
     */
    @RequestMapping(method=RequestMethod.GET, value="/{companyId}")
    public ResponseEntity checkHeartBeat(@PathVariable(name="companyId", value = "companyId") Integer companyId)
    		throws ApplicationException {
        healthCheckService.setAppContext(appContext);
		HealthCheckResponse hr  = healthCheckService.getHeartBeat(companyId);
        return new ResponseEntity<HealthCheckResponse> (hr, HttpStatus.OK);
    }
}
