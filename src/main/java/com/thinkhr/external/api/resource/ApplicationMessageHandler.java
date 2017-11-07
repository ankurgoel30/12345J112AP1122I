package com.thinkhr.external.api.resource;

import java.util.Locale;

import lombok.Data;

/**
 * To manage resources from message bundle files
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-03
 *
 */
@Data
public class ApplicationMessageHandler {

    private Locale locale = null;

    public ApplicationMessageHandler(Locale locale) {
        this.locale = locale;
    }

    /**
     * Get the message from resource bundle
     * @param msg
     * @return
     */
    public static String getApplicationMessage(String msg) {
       //TODO: Add implementation
    	return "";
    }

    /**
     * @param error
     * @return
     */
    public static String getErrorMessage(String error) {
    	  //TODO: Add implementation
    	return "";
    }
    
    
    

}
