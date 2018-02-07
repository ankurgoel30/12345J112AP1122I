package com.thinkhr.external.api.services.docusign;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocusignAuthHeader {
    @JsonProperty("UserName")
    String userName;
    
    @JsonProperty("Password")
    String password;
    
    @JsonProperty("IntegratorKey")
    String integratorKey;
}