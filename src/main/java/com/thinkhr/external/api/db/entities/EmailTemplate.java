package com.thinkhr.external.api.db.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for EmailTemplate
 * 
 * Name of database table is app_throne_email_template
 * 
 * @author Suarbhi Bhawsar
 * @since 2018-01-03
 *
 */
@Entity
@Table(name = "app_throne_email_template")
@Data
public class EmailTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private Integer brokerId;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "sendgridTemplateId", nullable = false)
    private String sendgridTemplateId;
    
    @OneToMany(mappedBy= "emailTemplate" , cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<EmailConfiguration> emailConfigurations;

}
