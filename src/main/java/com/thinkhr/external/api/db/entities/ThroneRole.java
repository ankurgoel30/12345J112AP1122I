package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "app_throne_roles")
@Data
public class ThroneRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private Integer companyId;
    
    private String name;
    
    private String description;
    
    @Column(name = "isAdministrator")
    private Integer administrator;
    
    @Column(name = "isImportDefault")
    private Integer importDefault;
    
    private String learnRole;

}
