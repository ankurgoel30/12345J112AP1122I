package com.thinkhr.external.api.model;

import java.util.List;

import com.thinkhr.external.api.db.entities.Role;

import lombok.Data;

/**
 * User Model is a user facing POJO object for
 * User entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-04
 *
 */
@Data
public class UserModel {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private List<Role> roles;
}
