package com.thinkhr.external.api.model;

import java.util.List;

import lombok.Data;

/**
 * Model to keep email related attributes
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Data
public class EmailRequest {

    private String fromEmail;
    private List<String> toEmail;
    private String subject;
    private String body;
    private List<KeyValuePair> parameters;
    
    
}
