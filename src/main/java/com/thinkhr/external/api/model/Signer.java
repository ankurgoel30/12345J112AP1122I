package com.thinkhr.external.api.model;

import java.util.List;

import org.hibernate.validator.constraints.Email;

import lombok.Data;

/**
 * 
 * @author Ajay Jain
 *
 */
@Data
public class Signer {
    private String clientId;
    private String name;

    @Email
    private String email;
    
    private String status;
    
    private List<String> customFeilds;
    
    private String deliveryMethod;
    
    private String recipientId ;
    
    private String roleName ;
    
    private String sentDateTime;
    
    private String signedDateTime;
    
    private String userId;
}
