package com.thinkhr.external.api.model;

import java.util.LinkedHashMap;
import java.util.List;

import com.thinkhr.external.api.db.entities.User;

import lombok.Data;

/**
 * Model to keep email related attributes
 * TODO: To remove this class and have single class EmailRequest for both single email and bulk email
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Data
public class BulkEmailRequest {

    private String fromEmail;
    private String subject;
    private String body;
    private LinkedHashMap<User,List<KeyValuePair>> recipientToSubstitutionMap = new LinkedHashMap<User,List<KeyValuePair>>();
    
}
