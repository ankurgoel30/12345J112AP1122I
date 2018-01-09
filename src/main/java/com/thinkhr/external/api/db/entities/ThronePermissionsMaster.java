package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "app_throne_permissions")
@Data
public class ThronePermissionsMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "skuId" , nullable= false)
    private Integer skuId;
    
    @Column(name = "displayLabel")
    private String displayLabel;
    
    @Column(name = "featureKey" , nullable=false)
    private String featureKey;
    
    @Column(name = "apiResource")
    private Integer apiResource;
    
    @Column(name = "privilege")
    private String privilege;
    
    @Column(name = "permissionKey")
    private String permissionKey;
    
    @Column(name = "description")
    private String description;

}
