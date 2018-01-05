package com.thinkhr.external.api.request;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Helper class to deal with HttpRequest object for application
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-16
 *
 */
public class APIRequestHelper {

    /**
     * Fetch request object from RequestContextHolder 
     * 
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletReqAttr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if ( servletReqAttr != null) {
            return servletReqAttr.getRequest();
        }
        return null;
    }

    /**
     * To set request attributes 
     * 
     * @param attributeName
     * @param attributeValue
     */
    public static void setRequestAttribute(String attributeName, Object attributeValue) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            request.setAttribute(attributeName, attributeValue);
        }
    }

    /**
     * To fetch attribute value from request for given attribute name 
     * 
     * @param attributeName
     * @return
     */
    public static Object getRequestAttribute(String attributeName) {
        HttpServletRequest request = getRequest();
        Object attrVal = null; 
        if (request != null) {
            return request.getAttribute(attributeName);
        }
        return attrVal;
    }
    
}