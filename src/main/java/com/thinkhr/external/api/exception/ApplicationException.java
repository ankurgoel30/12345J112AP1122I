package com.thinkhr.external.api.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;

/**
 * Custom exception class to handle exception those are related with Application
 * 
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-03
 *
 */
@Data
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 1L;
    private APIErrorCodes apiErrorCode;
    private String[] errorMessageParameters;
    private HttpStatus httpStatus;
    
	/**
	 * TODO 
	 * @param msg
	 */
	public ApplicationException(String msg) {
		super(msg);
	}

    /**
     * TODO
     * 
     * @param errorCode
     * @param params
     */
    public ApplicationException(APIErrorCodes errorCode, String...params) {
    	this.apiErrorCode = errorCode;
    	this.errorMessageParameters = params;
    }
    
    /**
     * TODO
     * @param errorCode
     * @param params
     * @param httpStatus
     */
    public ApplicationException(APIErrorCodes errorCode, HttpStatus httpStatus, String...params) {
    	this(errorCode, params);
    	this.setHttpStatus(httpStatus);
    }

    /**
     * TODO
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createBadRequest(APIErrorCodes errorCode, String...params) {
    	ApplicationException appException = new ApplicationException(errorCode, params);
    	appException.setHttpStatus(HttpStatus.BAD_REQUEST);
    	return appException;
    }
    
    /**
     * TODO
     * 
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createInternalError(APIErrorCodes errorCode, String...params) {
    	ApplicationException appException = new ApplicationException(errorCode, params);
    	appException.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    	return appException;
    }
    
    /**
     * TODO
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createEntityNotFoundError(APIErrorCodes errorCode, String...params) {
    	ApplicationException appException = new ApplicationException(errorCode, params);
    	appException.setHttpStatus(HttpStatus.NOT_FOUND);
    	return appException;
    }
}
