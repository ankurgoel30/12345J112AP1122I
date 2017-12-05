package com.thinkhr.external.api.services.crypto;

/**
 * Generic interface for password encryption.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-05
 *
 */
public interface AppEncryptorDecryptor {

    public String encrypt(String plainPassword);

    public String decrypt(String encryptedPassword);

}
