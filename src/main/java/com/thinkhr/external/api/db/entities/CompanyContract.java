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
 * Entity object for CompanyContract
 * 
 * Name of database table is clients_contracts
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-25
 *
 */
@Entity
@Table(name = "clients_contracts")
@Data
public class CompanyContract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relID")
    private Integer relId;
    
    @Column(name = "Client_ID", nullable = false)
    private Integer companyId;
    
    @Column(name = "Product_ID", nullable = false)
    private Integer productId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Start_Date", nullable = false)
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "End_Date", nullable = false)
    private Date endDate;
    
    @Column(name = "tempID")
    private String tempID;

}
