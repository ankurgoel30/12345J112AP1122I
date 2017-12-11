package com.thinkhr.external.api.services.crypto;

import static com.thinkhr.external.api.ApplicationConstants.BLOWFISH_ALGO;
import static com.thinkhr.external.api.ApplicationConstants.BLOWFISH_PKC_PADDING;
import static com.thinkhr.external.api.ApplicationConstants.UTF8;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

import lombok.Data;

/**
 * Encryptor\Decryptor with use of Blowfish crypto alogrithm
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-05
 *
 */
@Data
public class BlowfishEncryptorDecryptor implements AppEncryptorDecryptor{
    
    private Cipher encipher;
    private Cipher decipher;

    /**
     * 
     * @param key
     * @param initVector
     */
    public BlowfishEncryptorDecryptor(String key, String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(UTF8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UTF8), BLOWFISH_ALGO);
            encipher = Cipher.getInstance(BLOWFISH_PKC_PADDING);
            encipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            decipher = Cipher.getInstance(BLOWFISH_PKC_PADDING);
            decipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        } catch (Exception ex) {
            throw ApplicationException.createEncryptionError(APIErrorCodes.ENCRYPTION_ERROR, ex.getMessage());
        }
    }

    /**
     * This method is used to encrypt given value
     * 
     * @param value
     * @return
     */
    @Override
    public String encrypt(String value) {
        try {
            byte[] encrypted = encipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            throw ApplicationException.createEncryptionError(APIErrorCodes.ENCRYPTION_ERROR, ex.getMessage());
        }
    }

    /**
     * This method is used to decrypt the encrypted value.
     * 
     * @param encryptedValue
     * @return
     */
    @Override
    public String decrypt(String encryptedValue) {
        try {
            byte[] original = decipher.doFinal(Base64.decodeBase64(encryptedValue));
            return new String(original);
        } catch (Exception ex) {
            throw ApplicationException.createEncryptionError(APIErrorCodes.DECRYPTION_ERROR, ex.getMessage());
        }
    }

}
