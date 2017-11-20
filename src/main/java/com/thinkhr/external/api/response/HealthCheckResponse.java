package com.thinkhr.external.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thinkhr.external.api.db.entities.Company;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * HealthCheck Data
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
