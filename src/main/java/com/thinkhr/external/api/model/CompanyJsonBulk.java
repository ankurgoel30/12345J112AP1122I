package com.thinkhr.external.api.model;

import lombok.Data;

/**
 * Model class to support JSON bulk company upload
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */

@Data
public class CompanyJsonBulk extends BulkJsonModel {

    private String companyName;
    private String displayName;
    private String phone;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String industry;
    private String companySize;
    private String producer;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
}
