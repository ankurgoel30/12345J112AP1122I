package com.thinkhr.external.api.response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.MessageResourceHandler;

/**
 * Utility to handle API's response messages using resource bundles 
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-13
 *
 */
public class APIMessageUtil {

    /**
     * Prepare error message for the apiErrorCode from resource bundle
     * It will replace all dynamic arguments from error message by paramList values 
     * E.g. 
     *   '{0}' is a required parameter and paramList = userName then output of method will be as 
     *   'userName' is a required parameter.
     * @param apiErrorCode
     * @param paramList
     */
    public static String getMessageFromResourceBundle(MessageResourceHandler resourceHandler, APIErrorCodes apiErrorCode, String...paramList) {

        String errorMessage = resourceHandler.get(apiErrorCode.name());
        return replaceParameters(errorMessage, paramList);
    }

    /**
     * To fetch message from resource bundle for given message key and replace dynamic parameters with passed parameter values
     * 
     * @param resourceHandler
     * @param messageKey
     * @param paramList
     * @return
     */
    public static String getMessageFromResourceBundle(MessageResourceHandler resourceHandler, String messageKey, String...paramList) {

        String errorMessage = resourceHandler.get(messageKey);
        return replaceParameters(errorMessage, paramList);
    }

    /**
     * Method to replace dynamic parameters from given message with passed parameter values.
     * 
     * @param message
     * @param paramList
     * @return
     */
    public static String replaceParameters(String message, String... paramList) {
        if(paramList != null && paramList.length > 0) {
            Pattern pattern = Pattern.compile("\\{(\\d+)([^\\}.]*)\\.\\.(n?)(\\d*)\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(message);
            if ( matcher.find() ) {
                int offset = Integer.parseInt(matcher.group(1));
                String sep = matcher.group(2);
                int length = ("n".equals(matcher.group(3))?paramList.length:Math.min(paramList.length,Integer.parseInt(matcher.group(4))-offset+1));
                StringBuilder sb = new StringBuilder();
                for (int i=offset; i<length+offset; i++) {
                    if (i > offset) {
                        sb.append(sep);
                    }
                    sb.append("{").append(i).append("}");
                }
                message = matcher.replaceAll(sb.toString());
            }
            //It will replace "{0}, {1},{2}... {n}" number of parameter from error message
            for (int i=0; i<paramList.length; i++) {
                String param = paramList[i];
                message = message.replace("{"+i+"}", param);
            }
        }

        return message;
    }
}
