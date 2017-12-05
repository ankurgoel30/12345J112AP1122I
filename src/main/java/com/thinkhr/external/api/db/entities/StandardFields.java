package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

/**
 * Entity for "app_throne_standard_fields" table
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-26
 *
 */
@Entity
@Table(name = "app_throne_standard_field")
@Data
@Where(clause="required=1 and status=1")
public class StandardFields {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id") 
    private Integer id;
    private String label;
    private String type;
    private Integer required;
    private Integer order;
    private Integer status;
    
}
