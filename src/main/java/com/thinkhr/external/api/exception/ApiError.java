package com.thinkhr.external.api.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrapper class to wrap all the exception.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-02
 *
 */
@Data
@JsonInclude(Include.NON_EMPTY) 
class APIError {

	private String apiVersion = "v1"; //TODO: Fix me
	@JsonIgnore
	private HttpStatus status;
	private String timestamp;
	private String errorCode;
	private String message;
	private String exceptionDetail;
	private List<ErrorDetail> errorDetails;

	/**
	 * 
	 */
	private APIError() {
		timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
	}

	/**
	 * @param status
	 */
	APIError(HttpStatus status) {
		this();
		this.status = status;
	}

	/**
	 * @param status
	 * @param ex
	 */
	APIError(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.exceptionDetail = ex.getLocalizedMessage();
	}


	/**
	 * @param status
	 * @param errorCode
	 */
	APIError(HttpStatus status, APIErrorCodes errorCode) {
		this();
		this.status = status;
		this.errorCode = String.valueOf(errorCode.getCode());
	}

	/**
	 * @param status
	 * @param errorCode
	 * @param ex
	 */
	APIError(HttpStatus status, APIErrorCodes errorCode, Throwable ex) {
		this();
		this.status = status;
		this.errorCode = String.valueOf(errorCode.getCode());
		this.exceptionDetail = ex.getLocalizedMessage();
	}

	/**
	 * @param status
	 * @param errorCode
	 * @param ex
	 */
	APIError(HttpStatus status, APIErrorCodes errorCode, FieldError fieldError) {
		this();
		this.status = status;
		this.errorCode = String.valueOf(errorCode.getCode());
		addErrorDetail(fieldError);
	}

	/**
	 * @param status
	 * @param errorDetail
	 */
	APIError(HttpStatus status, ErrorDetail errorDetail) {
		this();
		this.status = status;
		this.addErrorDetail(errorDetail);
	}

	/**
	 * @param status
	 * @param message
	 * @param ex
	 */
	APIError(HttpStatus status, String message, Throwable ex) {
		this();
		this.status = status;
		addErrorDetail(new ErrorDetail(ex.getMessage()));
	}

	/**
	 * @param errorDetail
	 */
	public void addErrorDetail(ErrorDetail errorDetail) {
		if (this.errorDetails == null) {
			this.errorDetails = new ArrayList<ErrorDetail>();
		}

		this.errorDetails.add(errorDetail);
	}

	/**
	 * @param fieldError
	 */
	public void addErrorDetail(FieldError fieldError) {
		this.addErrorDetail(
				fieldError.getObjectName(),
				fieldError.getField(),
				fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}

	/**
	 * @param globalErrors
	 */
	void addErrorDetail(List<FieldError> globalErrors) {
		globalErrors.forEach(this::addErrorDetail);
	}


	/**
	 * @param object
	 * @param field
	 * @param rejectedValue
	 * @param message
	 */
	public void addErrorDetail(String object, String field, Object rejectedValue, String message) {
		this.addErrorDetail(new ErrorDetail(object, field, rejectedValue, message));
	}

	/**
	 * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation fails.
	 * @param cv the ConstraintViolation
	 */
	private void addErrorDetail(ConstraintViolation<?> cv) {
		this.addErrorDetail(
				cv.getRootBeanClass().getSimpleName(),
				((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
				cv.getInvalidValue(),
				cv.getMessage());
	}

	/**
	 * @param constraintViolations
	 */
	void addErrorDetail(Set<ConstraintViolation<?>> constraintViolations) {
		constraintViolations.forEach(this::addErrorDetail);
	}

	@Data
	@AllArgsConstructor
	public class ErrorDetail {
		private String object;
		private String field;
		private Object rejectedValue;
		private String message;

		ErrorDetail(String object, String message) {
			this.object = object;
			this.message = message;
		}

		ErrorDetail(String message) {
			this.message = message;
		}
	}

}