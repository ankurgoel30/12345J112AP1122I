package com.thinkhr.external.api.response;

import lombok.Data;

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
