package com.thinkhr.external.api.db.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for thinkhr_portal.app_throne_configurations table
 * @author Surabhi
 *
 */
@Entity
@Table(name = "app_throne_configurations")
@Data
public class Configuration {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer configurationId;
    
    @Column(name = "configurationKey")
    @Basic(fetch=FetchType.LAZY)
    private String configurationKey;
    
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    @Basic(fetch=FetchType.LAZY)
    private String description;
    
    @Column(name = "created")
    private Integer created;
    
    @Column(name = "updated")
    private Integer updated;
    
    @Column(name = "deleted")
    private Integer deleted;
    
    @Column(name = "isMasterConfiguration")
    private Integer isMasterConfiguration;
    
}
