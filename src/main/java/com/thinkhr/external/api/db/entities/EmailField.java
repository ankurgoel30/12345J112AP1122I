package com.thinkhr.external.api.db.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for EmailField
 * 
 * Name of database table is app_throne_email_field
 * 
 * @author Suarbhi Bhawsar
 * @since 2018-01-03
 *
 */
@Entity
@Table(name = "app_throne_email_field")
@Data
public class EmailField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @OneToMany(mappedBy= "emailField" , cascade = CascadeType.ALL)
    private List<EmailConfiguration> emailConfigurations;

}
