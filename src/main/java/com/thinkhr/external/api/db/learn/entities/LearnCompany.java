package com.thinkhr.external.api.db.learn.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Column(name = "thrclientid")
    private Integer thrClientId;

    @Column(name = "company_key")
    private String companyKey;

    @Column(name = "createdby" , nullable = false)
    private Long createdBy;

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
    private String phone;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "employee_count")
    private Integer employeeCount;
    
    @Column(name = "timecreated")
    private Long timeCreated;

    @Column(name = "timemodified")
    private Long timeModified;
    
    @Column(name = "license")
    private Long license;

    @Column(name = "enrollmentstart")
    private Long enrollmentStart;
    
    @Column(name = "enrollmentend")
    private Long enrollmentEnd;
    
    @Column(name = "suspended")
    private Integer suspended;
    
    @Column(name = "partnerid")
    private String partnerId;
    
    @Column(name = "logo")
    private String logo;

    @Column(name = "upgraderequired")
    private Integer upgradeRequired;
    
    @Column(name = "externalapicompany")
    private Integer externalApiCompany;

}