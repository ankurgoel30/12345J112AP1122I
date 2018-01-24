package com.thinkhr.external.api.model;

import lombok.Data;

/**
 * Model class to support JSON bulk user upload
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */

@Data
public class UserJsonBulk extends BulkJsonModel {

    private String firstName;
    private String lastName;
    private String clientName;
    private String email;
    private String userName;
    private String phone;
    private String businessId;

}
