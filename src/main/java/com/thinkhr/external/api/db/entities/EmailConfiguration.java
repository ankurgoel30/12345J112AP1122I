package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for EmailConfiguration
 * 
 * Name of database table is app_throne_email_configuration
 * 
 * @author Suarbhi Bhawsar
 * @since 2018-01-03
 *
 */
@Entity
@Table(name = "app_throne_email_configuration")
@Data
public class EmailConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "fieldId")
    private EmailField emailField;
    
    @Column(name = "value", nullable = false)
    private String value;
    
    @ManyToOne
    @JoinColumn(name = "templateId", nullable = false)
    private EmailTemplate emailTemplate;

}
