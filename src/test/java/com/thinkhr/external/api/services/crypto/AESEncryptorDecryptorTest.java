package com.thinkhr.external.api.services.crypto;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.AES_ENCRYPTED_VALUE;
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
public class AESEncryptorDecryptorTest {

    @Value("${com.thinkhr.external.api.crypto.encrypt.key}")
    private String key;

    @Value("${com.thinkhr.external.api.crypto.initVector}")
    private String initVector;

    private AESEncryptorDecryptor aesEncDec;

    /**
     * Test to verify when constructor is instantiated properly, i.e. encipher
     * and decipher are initialized.
     * 
     */
    @Test
    public void testConstructorAESEncryptorDecryptorWhenSuccess() {
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        assertNotNull(aesEncDec.getEncipher());
        assertNotNull(aesEncDec.getDecipher());
    }

    /**
     * Test to verify when size of initVector parameter is not equal to 16
     * bytes.
     * 
     * @throws ApplicationException
     */
    @Test
    public void testConstructorAESEncryptorDecryptorForInvalidInitVector() {
        try {
            aesEncDec = new AESEncryptorDecryptor(key, INVALID_INIT_VECTOR);
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
    public void testConstructorAESEncryptorDecryptorForNullParameters() {
        try {
            aesEncDec = new AESEncryptorDecryptor(null, null);
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
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        String encryptedValue = aesEncDec.encrypt(ORIGINAL_VALUE);
        assertEquals(AES_ENCRYPTED_VALUE, encryptedValue);
    }

    /**
     * Test to verify when expected encrypted value is not the actual one.
     * 
     */
    @Test
    public void testEncryptWhenExpectedNotSame() {
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        String encryptedValue = aesEncDec.encrypt(SOME_OTHER_VALUE);
        assertNotEquals(AES_ENCRYPTED_VALUE, encryptedValue);
    }

    /**
     * Test to verify when value is not encrypted.
     * 
     */
    @Test
    public void testEncryptForNullValue() {
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        try {
            String encryptedValue = aesEncDec.encrypt(null);
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
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        String value = aesEncDec.decrypt(AES_ENCRYPTED_VALUE);
        assertEquals(ORIGINAL_VALUE, value);
    }

    /**
     * Test to verify when expected value is not the actual one.
     * 
     */
    @Test
    public void testDecryptWhenExpectedNotSame() {
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        String value = aesEncDec.decrypt(AES_ENCRYPTED_VALUE);
        assertNotEquals(SOME_OTHER_VALUE, value);
    }

    /**
     * Test to verify when value is not encrypted.
     * 
     */
    @Test
    public void testDecryptForNullValue() {
        aesEncDec = new AESEncryptorDecryptor(key, initVector);
        try {
            String value = aesEncDec.encrypt(null);
        } catch (ApplicationException ae) {
            assertNotNull(ae);
            assertEquals(APIErrorCodes.ENCRYPTION_ERROR, ae.getApiErrorCode());
        }
    }

}
