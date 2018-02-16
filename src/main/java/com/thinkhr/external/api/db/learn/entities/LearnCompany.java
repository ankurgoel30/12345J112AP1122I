package com.thinkhr.external.api.db.learn.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 
 * Database entity object for LearnCompany
 * 
 * Name of database table is mdl_company in thinkhr_learn database
 * 
 * @author Ajay Jain
 * @since 2017-12-12
 *
 */
@Entity
@Table(name = "mdl_company")
@Data
@DynamicInsert
@DynamicUpdate
public class LearnCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "thrclientid", unique = true)
    private Integer companyId;

    @Column(name = "company_key", unique = true)
    private String companyKey;

    @Column(name = "createdby" , nullable = false)
    private Long createdBy = 1L;

    @Column(name = "address")
    private String address;

    @Column(name = "address2")
    private String address2;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "phone")
    @Size(max = 25)
    private String phone;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "employee_count")
    private Integer employeeCount;
    
    @Column(name = "timecreated")
    private Long timeCreated = new Date().getTime();

    @Column(name = "timemodified")
    private Long timeModified = new Date().getTime();
    
    @Column(name = "license")
    private Long license = 1000L;

    @Column(name = "enrollmentstart")
    private Long enrollmentStart;
    
    @Column(name = "enrollmentend")
    private Long enrollmentEnd;
    
    @Column(name = "suspended")
    private Integer suspended;
    
    @Column(name = "partnerid")
    private String broker;
    
    @Column(name = "logo")
    private String logo;

    @Column(name = "upgraderequired")
    private Integer upgradeRequired;
    
    @Column(name = "externalapicompany")
    private Integer externalApiCompany;
    
    @ManyToMany(fetch=FetchType.LAZY )
    @JoinTable(
            name = "mdl_package_company", 
            joinColumns = { @JoinColumn(name = "companyid") }, 
            inverseJoinColumns = { @JoinColumn(name = "packageid") }
    )
    private List<LearnPackageMaster> packages;

}