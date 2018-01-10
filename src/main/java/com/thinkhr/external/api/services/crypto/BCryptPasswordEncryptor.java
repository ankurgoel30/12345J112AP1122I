package com.thinkhr.external.api.services.crypto;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * To hash password using Bcrypt blowfish alogrithm. 
 * http://www.mindrot.org/projects/jBCrypt/#download
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-09
 *
 */
public class BCryptPasswordEncryptor implements AppEncryptorDecryptor {

    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public String decrypt(String encryptedPassword) {
        throw new UnsupportedOperationException ("Not supported");
    }
}
