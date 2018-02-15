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
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private APIErrorCodes apiErrorCode;
    private String[] errorMessageParameters;
    private HttpStatus httpStatus;

    /**
     * ApplicationException constructor 
     * 
     * @param msg
     */
    public ApplicationException(String msg) {
        super(msg);
    }

    /**
     * ApplicationException constructor 
     * 
     * @param errorCode
     * @param params
     */
    public ApplicationException(APIErrorCodes errorCode, String...params) {
        this.apiErrorCode = errorCode;
        this.errorMessageParameters = params;
    }

    /**
     * ApplicationException constructor 
     * 
     * @param errorCode
     * @param params
     * @param httpStatus
     */
    public ApplicationException(APIErrorCodes errorCode, HttpStatus httpStatus, String...params) {
        this(errorCode, params);
        this.setHttpStatus(httpStatus);
    }

    /**
     * ApplicationException constructor 
     * 
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
     * ApplicationException constructor 
     * 
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createEncryptionError(APIErrorCodes errorCode, String...params) {
        ApplicationException appException = new ApplicationException(errorCode, params);
        appException.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return appException;
    }

    /**
     * ApplicationException constructor 
     * 
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createAuthorizationError(APIErrorCodes errorCode, String...params) {
        ApplicationException appException = new ApplicationException(errorCode, params);
        appException.setHttpStatus(HttpStatus.UNAUTHORIZED);
        return appException;
    }
    
    /**
     * ApplicationException constructor 
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
     * ApplicationException constructor 
     * 
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createEntityNotFoundError(APIErrorCodes errorCode, String...params) {
        ApplicationException appException = new ApplicationException(errorCode, params);
        appException.setHttpStatus(HttpStatus.NOT_FOUND);
        return appException;
    }


    /**
     * TODO
     * @param errorCode
     * @param params
     * @return
     */
    public static ApplicationException createBulkImportError(APIErrorCodes errorCode, String...params) {
        ApplicationException appException = new ApplicationException(errorCode, params);
        appException.setHttpStatus(HttpStatus.NOT_ACCEPTABLE);
        return appException;
    }

    /**
     * 
     * @param errorCode
     * @param ex
     * @return
     */
    public static ApplicationException createSendEmailError(APIErrorCodes errorCode, Exception ex) {
        ApplicationException appException = new ApplicationException(errorCode, ex.getMessage());
        appException.initCause(ex);
        return appException;
    }

}