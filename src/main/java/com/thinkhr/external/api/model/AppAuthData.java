package com.thinkhr.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppAuthData {
    
    private Integer brokerId;
    private Integer clientId;
    private String user;
    private String iss;
    private String role;
    private String sub;

}
