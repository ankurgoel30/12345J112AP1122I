package com.thinkhr.external.api.db.learn.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for the thinkhr_learn.mdl_role database table.
 * 
 * @author Ajay Jain
 * @since 2017-12-19
 *
 */
@Entity
@Table(name = "mdl_role")
@Data
public class LearnRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "description")
    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Column(name = "sortorder")
    private Integer sortOrder;

    @Column(name = "archetype")
    private String archeType;
}