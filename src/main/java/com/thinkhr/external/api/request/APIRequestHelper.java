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
		 HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		 return request;
	}
	
	/**
	 * To set request attributes 
	 * 
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void setRequestAttribute(String attributeName, Object attributeValue) {
		getRequest().setAttribute(attributeName, attributeValue);
	}
	
	/**
	 * To fetch attribute value from request for given attribute name 
	 * 
	 * @param attributeName
	 * @return
	 */
	public static String getRequestAttribute(String attributeName) {
		Object val = getRequest().getAttribute(attributeName);
		return val == null ? null : val.toString();
	}
	
}

