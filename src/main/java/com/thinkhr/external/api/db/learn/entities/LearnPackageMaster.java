package com.thinkhr.external.api.db.learn.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

/**
 * Database entity object for the thinkhr_learn.mdl_package_master database table.
 * 
 * @author Ajay Jain
 * @since 2017-12-14
 *
 */
@Entity
@Table(name = "mdl_package_master")
@Data
public class LearnPackageMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "is_premium")
    private Integer isPremium;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "img_thumb")
    private String imgThumb;

    @Column(name = "img_large")
    private String imgLarge;
    
    @Column(name = "is_active")
    private int isActive;

    @Column(name = "price_user_year")
    private Double priceUserYear;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}