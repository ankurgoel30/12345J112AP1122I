package com.thinkhr.external.api.model;

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
}
