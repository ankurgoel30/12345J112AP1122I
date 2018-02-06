package com.thinkhr.external.api.db.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Entity class for thinkhr_portal.app_throne_configurations table
 * @author Surabhi
 *
 */
@Entity
@Table(name = "app_throne_configurations")
@Where(clause="deleted IS NULL")
@Data
@JsonInclude(Include.NON_EMPTY)
public class Configuration implements SearchableEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer configurationId;
    
    @Column(name = "companyId" , updatable=false)
    private Integer companyId;
    
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
    
    @Column(name = "isMasterConfiguration" , updatable=false)
    private Integer masterConfiguration;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "app_throne_configurations_skus", 
            joinColumns = { @JoinColumn(name = "configurationId") }, 
            inverseJoinColumns = { @JoinColumn(name = "skuId") }
    )
    private Set<Sku> skus;
    
    @Override
    @JsonIgnore
    public List<String> getSearchFields() {
        List<String> searchColumns = new ArrayList<String>();
        searchColumns.add("configurationKey");
        searchColumns.add("name");
        return searchColumns;
    }
    
    @Override
    @JsonIgnore
    public String getNodeName() {
        return "configuration";
    }
    
    @Override
    @JsonIgnore
    public String getMultiDataNodeName() {
        return "configurations";
    }
    
}
