package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Entity class for thinkhr_portal.app_throne_skus table
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-23
 *
 */
@Entity
@Table(name = "app_throne_skus")
@Data
@JsonInclude(Include.NON_EMPTY)
public class Skus {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer skuId;
    
    private String skuKey;
    
    private String name;
    
    private String description;
    
    @Column(name = "isBrokerOnly")
    private Integer brokerOnly;
    
    @Column(name = "isActive")
    private Integer active;

}
