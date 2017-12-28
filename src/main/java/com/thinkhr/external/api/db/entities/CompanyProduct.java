package com.thinkhr.external.api.db.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 * Entity object for CompanyProduct
 * 
 * Name of database table is clients_products
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-25
 *
 */
@Entity
@Table(name = "clients_products")
@Data
public class CompanyProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relID")
    private Integer relId;

    @Column(name = "contractID", nullable = false)
    private Integer contractId;

    @Column(name = "Client_ID")
    private Integer companyId;

    @Column(name = "Product_ID")
    private Integer productId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Start_Date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "End_Date")
    private Date endDate;

    @Column(name = "authorizationKey")
    private String authorizationKey;

    @Column(name = "numberLicenses")
    private Integer numberLicenses;

    @Column(name = "tempID")
    private String tempID;

}
