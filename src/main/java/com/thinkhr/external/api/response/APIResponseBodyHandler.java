package com.thinkhr.external.api.response;

import static com.thinkhr.external.api.ApplicationConstants.*;
import static com.thinkhr.external.api.request.APIRequestHelper.*;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.exception.APIError;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.services.utils.EntitySearchUtil;


/**
 * This class is a single global response handler component wrapping for all 
 * the responses from APIs, prepare a wrapper over response object and put additional informations those are 
 * useful for API consumer like status and code etc.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-13
 *
 */
@ControllerAdvice ("com.thinkhr.external.api.controllers")
public class APIResponseBodyHandler implements ResponseBodyAdvice<Object> {
    
	private static Logger logger = LoggerFactory.getLogger(APIResponseBodyHandler.class);
	
	@Autowired
	MessageResourceHandler resourceHandler;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	/**
	 * This method to identify response body and inject additional information to response. 
	 * 
	 *  @param body
	 *  @param returnType
	 *  @param selectedContenctType
	 *  @param selectedConverterType
	 *  @param request
	 *  @param response
	 */
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
					ServerHttpResponse response) {
		
		if (body instanceof APIError || body instanceof Exception ) {
			return body;
		}
		ServletServerHttpRequest httpRequest = (ServletServerHttpRequest) request;
		ServletServerHttpResponse httpResponse = (ServletServerHttpResponse) response;
		
		APIResponse apiResponse = new APIResponse();
		int statusCode = httpResponse.getServletResponse().getStatus();
		apiResponse.setCode(String.valueOf(statusCode));
		apiResponse.setStatus(HttpStatus.valueOf(statusCode).name());
		/*
		 * TODO: Currently there are references those are Company specific, we need to make them generic so 
		 * the same code reference will be used by all APIs for different entities as well.
		 */
		if (body instanceof List) {
			if ((List)body == null || ((List)body).isEmpty()) {
				if (httpRequest.getURI().getPath().contains("companies")) {
					apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, APIErrorCodes.NO_RECORDS_FOUND, "company"));
				}
				if (httpRequest.getURI().getPath().contains("users")) {
					apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, APIErrorCodes.NO_RECORDS_FOUND, "user"));
				}
			} else {
				if (httpRequest.getURI().getPath().contains("companies")) {
					setCompanyListData((List)body, httpRequest, apiResponse);
				}
				if (httpRequest.getURI().getPath().contains("users")) {
					setUserListData((List)body, httpRequest, apiResponse);
				}
			}
		} else {
			/*
			 * TODO: FIXME for generic object
			 */
			if (body != null && body instanceof Company) {
				apiResponse.setCompany((Company)body);
			} 
			
			if (body != null && body instanceof User) {
				apiResponse.setUser((User)body);
			}
			
			if (body instanceof Integer && statusCode == HttpStatus.ACCEPTED.value()) {
				if (httpRequest.getURI().getPath().contains("companies")) {
					apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, SUCCESS_DELETED, "Company", body.toString()));
				}
				if (httpRequest.getURI().getPath().contains("users")) {
					apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, SUCCESS_DELETED, "User", body.toString()));
				}
			}
		}
		return apiResponse;
	}

	/**
	 * To set list of company specific information into ApiResponse object 
	 * 
	 * @param list
	 * @param httpRequest
	 * @param apiResponse
	 */
	private void setCompanyListData(List list, ServletServerHttpRequest httpRequest, APIResponse apiResponse) {
		
		String limit = httpRequest.getServletRequest().getParameter(LIMIT_PARAM);
		limit = StringUtils.isNotBlank(limit) ? limit : String.valueOf(DEFAULT_LIMIT);
		apiResponse.setLimit(limit);
		
		String offset = httpRequest.getServletRequest().getParameter(OFFSET_PARAM);
		offset = StringUtils.isNotBlank(offset) ? offset : String.valueOf(DEFAULT_OFFSET);
		apiResponse.setOffset(offset);
		
		String sort = httpRequest.getServletRequest().getParameter(SORT_PARAM);
		sort = StringUtils.isNotBlank(sort) ? sort : DEFAULT_SORT_BY_COMPANY_NAME;
		apiResponse.setSort(EntitySearchUtil.getFormattedString(sort));
		
		Object totalRecObj = getRequestAttribute(TOTAL_RECORDS);
		if (totalRecObj != null) {
			apiResponse.setTotalRecords(String.valueOf(totalRecObj));
		}
		
		/*
		 * TODO: FIXME for generic list
		 */
		apiResponse.setCompanies(list);
		
	}
	
	/**
	 * To set list of user specific information into ApiResponse object 
	 * 
	 * @param list
	 * @param httpRequest
	 * @param apiResponse
	 */
	private void setUserListData(List list, ServletServerHttpRequest httpRequest, APIResponse apiResponse) {
		
		String limit = httpRequest.getServletRequest().getParameter(LIMIT_PARAM);
		limit = StringUtils.isNotBlank(limit) ? limit : String.valueOf(DEFAULT_LIMIT);
		apiResponse.setLimit(limit);
		
		String offset = httpRequest.getServletRequest().getParameter(OFFSET_PARAM);
		offset = StringUtils.isNotBlank(offset) ? offset : String.valueOf(DEFAULT_OFFSET);
		apiResponse.setOffset(offset);
		
		String sort = httpRequest.getServletRequest().getParameter(SORT_PARAM);
		sort = StringUtils.isNotBlank(sort) ? sort : DEFAULT_SORT_BY_USER_NAME;
		apiResponse.setSort(EntitySearchUtil.getFormattedString(sort));
		
		Object totalRecObj = getRequestAttribute(TOTAL_RECORDS);
		if (totalRecObj != null) {
			apiResponse.setTotalRecords(String.valueOf(totalRecObj));
		}
		
		/*
		 * TODO: FIXME for generic list
		 */
		apiResponse.setUsers(list);
		
	}

}
