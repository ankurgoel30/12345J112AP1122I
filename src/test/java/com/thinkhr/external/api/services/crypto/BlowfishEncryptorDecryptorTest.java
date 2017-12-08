package com.thinkhr.external.api.services.crypto;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.BLOWFISH_ENCRYPTED_VALUE;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.INVALID_INIT_VECTOR;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.ORIGINAL_VALUE;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.SOME_OTHER_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class BlowfishEncryptorDecryptorTest {
    
    @Value("${com.thinkhr.external.api.crypto.encrypt.key}")
    private String key;

    @Value("${com.thinkhr.external.api.crypto.initVector_Blowfish}")
    private String initVectorForBlowfish;
    
    private BlowfishEncryptorDecryptor blowfishEncDec;

    /**
     * Test to verify when constructor is instantiated properly, i.e. encipher
     * and decipher are initialized.
     * 
     */
    @Test
    public void testConstructorBlowfishEncryptorDecryptorWhenSuccess() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        assertNotNull(blowfishEncDec.getEncipher());
        assertNotNull(blowfishEncDec.getDecipher());
    }

    /**
     * Test to verify when size of initVector parameter is not equal to 8
     * bytes.
     * 
     * @throws ApplicationException
     */
    @Test
    public void testConstructorBlowfishEncryptorDecryptorForInvalidInitVector() {
        try {
            blowfishEncDec = new BlowfishEncryptorDecryptor(key, INVALID_INIT_VECTOR);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.ENCRYPTION_ERROR, ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify when both constructor parameters are null.
     *
     * @throws ApplicationException
     */
    @Test
    public void testConstructorlBlowfishEncryptorDecryptorForNullParameters() {
        try {
            blowfishEncDec = new BlowfishEncryptorDecryptor(null, null);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.ENCRYPTION_ERROR, ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify when value is encrypted properly.
     * 
     */
    @Test
    public void testEncryptWhenSuccess() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        String encryptedValue = blowfishEncDec.encrypt(ORIGINAL_VALUE);
        assertEquals(BLOWFISH_ENCRYPTED_VALUE, encryptedValue);
    }
    
    /**
     * Test to verify when expected encrypted value is not the actual one.
     * 
     */
    @Test
    public void testEncryptWhenExpectedNotSame() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        String encryptedValue = blowfishEncDec.encrypt(SOME_OTHER_VALUE);
        assertNotEquals(BLOWFISH_ENCRYPTED_VALUE, encryptedValue);
    }

    /**
     * Test to verify when value is not encrypted.
     * 
     */
    @Test
    public void testEncryptForNullValue() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        try {
            String encryptedValue = blowfishEncDec.encrypt(null);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.ENCRYPTION_ERROR, ae.getApiErrorCode());
        }
    }

    /**
     * Test to verify when value is decrypted properly.
     * 
     */
    @Test
    public void testDecryptWhenSuccess() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        String value = blowfishEncDec.decrypt(BLOWFISH_ENCRYPTED_VALUE);
        assertEquals(ORIGINAL_VALUE, value);
    }
    
    /**
     * Test to verify when expected value is not the actual one.
     * 
     */
    @Test
    public void testDecryptWhenExpectedNotSame() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        String value = blowfishEncDec.decrypt(BLOWFISH_ENCRYPTED_VALUE);
        assertNotEquals(SOME_OTHER_VALUE, value);
    }

    /**
     * Test to verify when value is not encrypted.
     * 
     */
    @Test
    public void testDecryptForNullValue() {
        blowfishEncDec = new BlowfishEncryptorDecryptor(key, initVectorForBlowfish);
        try {
            String value = blowfishEncDec.encrypt(null);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.ENCRYPTION_ERROR, ae.getApiErrorCode());
        }
    }

}
