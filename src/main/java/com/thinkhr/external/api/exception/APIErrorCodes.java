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

    // GENERAL ERROR CODES
    VALIDATION_FAILED(1000),
    REQUEST_PARAM_INVALID(1000),
    REQUIRED_PARAMETER(1001),
    UNAUTHORIZED_USER(1002),
    ARGUMENT_TYPE_MISMATCH(1003),
    MEDIA_NOT_SUPPORTED(1004),
    ALREADY_EXISTS(1000),
    ENTITY_NOT_FOUND(1005),
    MALFORMED_JSON_REQUEST(1006),
    ERROR_WRITING_JSON_OUTPUT(1007),
    DATABASE_ERROR(1008),
    NO_RECORDS_FOUND(1009),
    INVALID_DATE_FORMAT(1010),

    // BULK IMPORT ERROR CODES
    NO_RECORDS_FOUND_FOR_IMPORT(1010),
    MISSING_REQUIRED_HEADERS(1011),
    MAX_RECORD_EXCEEDED(1012),
    INVALID_FILE_EXTENTION(1013),
    FILE_READ_ERROR(1014),
    INVALID_BROKER_ID(1015),
    INVALID_CONFIGURATION_ID(1015),
    INVALID_ROLE_ID(1015),
    UNMAPPED_CUSTOM_HEADERS(1016),
    FAILED_RECORD(1017),
    BLANK_RECORD(1018),
    DUPLICATE_RECORD(1019),
    MISSING_FIELDS(1020),
    RECORD_NOT_ADDED(1021),
    DATA_TRUNCTATION(1022),
    SKIPPED_RECORD(1023),
    FAILURE_CAUSE(1024),
    MISSING_REQUIRED_FIELD(1025),
    ENCRYPTION_ERROR(1026),
    DECRYPTION_ERROR(1027),
    INVALID_EMAIL(1028),
    INVALID_CLIENT_NAME(1029),
    DUPLICATE_USER_RECORD(1030),
    DUPLICATE_COMPANY_RECORD(1030),
    AUTHORIZATION_MISSING(1031),
    AUTHORIZATION_FAILED(1032),
    SG_MAIL_FAILED(1033),
    INVALID_PHONE(1034),
    MISSING_REQUIRED_FIELDS(1035),
    DATE_PARSE_ERROR(1036),
    MASTER_CONFIGURATION_NOT_EXISTS(1037),
    INVALID_MASTER_CONFIGURATION_ID(1038),
    INVALID_ALTERNATE_CONFIGURATION_ID(1039),
    MASTER_CONFIGURATION_NOT_CHANGEABLE(1040),
    UNREMOVABLE_LINKED_CONFIGURATION(1041), 
    UNAUTHORIZED_CONFIGURATION_ACCESS(1042);

    @Getter
    private final Integer code;

    APIErrorCodes(Integer intCode) {
        this.code = intCode;
    }

}