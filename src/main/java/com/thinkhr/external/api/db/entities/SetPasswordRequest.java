package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for SetPasswordRequest
 * 
 * Name of database table is set_password_requests
 * 
 * @author Suarbhi Bhawsar
 * @since 2018-01-03
 *
 */
@Entity
@Table(name = "set_password_requests")
@Data
public class SetPasswordRequest {
    
    @Id
    @Column(length = 64)
    private String id;
    
    @Column(name = "contact_id", unique = true)
    private Integer contactId;

}
