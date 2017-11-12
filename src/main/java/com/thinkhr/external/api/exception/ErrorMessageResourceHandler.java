package com.thinkhr.external.api.exception;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Helper to simplify accessing i18n messages in code.
 * This finds messages automatically found from 
 * src/main/resources (files named errors_*.properties)
 * 
 *
 * @author Surabhi Bhawsar
 * @since 2017-11-04
 */
@Component
public class ErrorMessageResourceHandler {

    @Autowired
    private MessageSource messageSource;

    private MessageSourceAccessor accessor;
    
    /**
     * TODO
     */
    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
    }
    
    /**
     * TODO
     * @param code
     * @return
     */
    public String get(String code) {
        return accessor.getMessage(code);
    }

}