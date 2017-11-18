package com.thinkhr.external.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thinkhr.external.api.db.entities.Company;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Global response object to wrap response from all the APIs and return additional attributes.
 * 
 * TODO: Currently hard-coded company and companies attributes those needs to be replaced by generic 
 * name like object and list to be used by all entities's
 * 
 * @author Sudhakar kaki
 * @since 2017-11-13
 *
 */
@Data
public class HealthCheckResponse {
	private Integer status;
	private String version;
}
