package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity for "app_throne_custom_fields" table
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-26
 *
 */
@Entity
@Table(name = "app_throne_custom_fields")
@Data
public class CustomFields {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id") 
    private Integer id;
    private Integer companyId;
    private String customFieldType;
    private String customFieldColumnName;
    private String customFieldDisplayKey;
    private String customFieldDisplayLabel;
    private String isImportRequired;

}
