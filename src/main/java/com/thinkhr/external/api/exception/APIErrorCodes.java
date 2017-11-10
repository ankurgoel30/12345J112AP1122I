package com.thinkhr.external.api.exception;

import lombok.Getter;

/**
 * To make an API's error uniquely identifiable by its error code.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-02
 *
 */
public enum APIErrorCodes {
	
	VALIDATION_FAILED(1000),
	REQUIRED_PARAMETER(1001),
	UNAUTHORIZED_USER(1002),
	REQUEST_PARAM_INVALID(1000),
	ARGUMENT_TYPE_MISMATCH(1003),
	MEDIA_NOT_SUPPORTED(1004),
	ENTITY_NOT_FOUND(1005),
	MALFORMED_JSON_REQUEST(1006),
	ERROR_WRITING_JSON_OUTPUT(1007),
	DATABASE_ERROR(1008);
	
	@Getter
    private final Integer code;

    APIErrorCodes(Integer intCode) {
        this.code = intCode;
    }

}
