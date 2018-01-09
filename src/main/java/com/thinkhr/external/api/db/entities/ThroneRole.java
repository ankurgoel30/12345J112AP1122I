package com.thinkhr.external.api.db.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "app_throne_roles")
@Data
public class ThroneRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @JsonIgnore
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="companyId")
    private Company company;
    
    private String name;
    
    private String description;
    
    @Column(name = "isAdministrator")
    private Integer administrator;
    
    @Column(name = "isImportDefault")
    private Integer importDefault;
    
    private String learnRole;
    
    @ManyToMany(fetch=FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinTable(
            name = "app_throne_permissions_roles", 
            joinColumns = { @JoinColumn(name = "roleId") }, 
            inverseJoinColumns = { @JoinColumn(name = "permissionId") }
    )
    private List<ThronePermissionsMaster> permissions;

}
