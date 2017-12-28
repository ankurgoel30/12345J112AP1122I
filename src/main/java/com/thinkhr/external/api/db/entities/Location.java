package com.thinkhr.external.api.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 
 * Database entity object for Location
 * 
 * Name of database table is location in thinkhr_portal
 * 
 * @author Ajay Jain
 * @since 2017-12-12
 *
 */

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "locations")
@Data
public class Location {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Integer locationId;

    @NotNull
    String address;
    
    @NotNull
    String address2;
    
    @NotNull
    String city;
    
    @NotNull
    String state;
    
    @NotNull
    String zip;
    String tempID;
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="client_id")
    @JsonIgnore
    Company company;
}
