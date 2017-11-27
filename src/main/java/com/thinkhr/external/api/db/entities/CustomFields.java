package com.thinkhr.external.api.db.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

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
@Where(clause="t1_is_active=1")
public class CustomFields {
    
    private Integer id;
    private Integer companyId;
    private String customFieldType;
    private String customFieldColumnName;
    private String customFieldDisplayKey;
    private String customFieldDisplayLabel;
    private String isImportRequired;

}
