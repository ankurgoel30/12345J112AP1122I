package com.thinkhr.external.api;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.thinkhr.external.api.services.crypto.AESEncryptorDecryptor;
import com.thinkhr.external.api.services.crypto.AppEncryptorDecryptor;
import com.thinkhr.external.api.services.crypto.BCryptPasswordEncryptor;
import com.thinkhr.external.api.services.crypto.BlowfishEncryptorDecryptor;

/**
 * Main class for Spring Boot based API application.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-01
 *
 */

@SpringBootApplication
public class ApiApplication {

    @Value("${com.thinkhr.external.api.crypto.algo}")
    private String cryptoAlgo;
    
    @Value("${com.thinkhr.external.api.crypto.encrypt.key}")
    private String key;
    
    @Value("${com.thinkhr.external.api.crypto.initVector}")
    private String initVector;

    
    /**
     * Main method for spring application
     * 
     * @param args command line arguments passed to app
     * 
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    /**
     * Facilitates messageSoruce
     * 
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource () {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    /**
     * Facilitates Encryption/Decryption technique for password field.
     * 
     * @return
     */
    @Bean
    @Lazy(value = true)
    public AppEncryptorDecryptor getEncryptor() {
        if (ApplicationConstants.BLOWFISH_ALGO.equalsIgnoreCase(cryptoAlgo)) {
            return new BlowfishEncryptorDecryptor(key, initVector);
        } else if (ApplicationConstants.BCRYPT_ALGO.equalsIgnoreCase(cryptoAlgo)) {
            return new BCryptPasswordEncryptor();
        }
        
        return new AESEncryptorDecryptor(key, initVector);
        
    }

    /**
    * ModelMapper object for interchanging model and entities
    * 
    * @return ModelMapper
    */
     @Bean
     public ModelMapper modelMapper() {
         return new ModelMapper();
     }
}
