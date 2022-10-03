package com.me.devicemanagement.framework.server.util;

import java.util.HashMap;
import java.util.Map;

public class FrameworkStatusCodes
{
    public static final Map<Integer, String> ERROR_CODES_AND_STATUS;
    public static final String DB_UPDATE_SUCCESS_MSG = "DB_UPDATE_SUCCESSFUL";
    public static final int DB_UPDATE_SUCCESS_CODE = 1000;
    public static final String DB_UPDATE_FAILED_MSG = "DB_UPDATE_FAILED";
    public static final int DB_UPDATE_FAILED_CODE = 1001;
    public static final String DATA_OBJECT_CREATION_FAILURE_MSG = "DB Related Issue";
    public static final Integer DATA_OBJECT_CREATION_FAILURE_CODE;
    public static final String SUCCESS_MSG = "Success";
    public static final Integer SUCCESS_RESPONSE_CODE;
    public static final String ERROR_MSG = "Bad Request";
    public static final Integer ERROR_CODE;
    public static final String CONFLICT_ERROR_MSG = "Conflict : Duplicate Data Found";
    public static final Integer CONFLICT_ERROR_CODE;
    public static final String UNPROCESSABLE_DATA_MSG = "Unprocessable Data";
    public static final Integer UNPROCESSABLE_DATA_CODE;
    public static final String NO_CONTENT_MESSAGE = "Expected Detail Not Found";
    public static final Integer NO_CONTENT_RESPONSE_CODE;
    public static final String NOT_SUPPORTED_RESPONSE_MSG = "Not Supported";
    public static final Integer NOT_SUPPORTED_RESPONSE_CODE;
    public static final String CR_MICKEY_CLIENT_VIEW_CREATION_SUCCESS_MSG = "CR_MICKEY_CLIENT_VIEW_CREATION_SUCCESS";
    public static final int CR_MICKEY_CLIENT_VIEW_CREATION_SUCCESS_CODE = 1100;
    public static final String CR_MICKEY_CLIENT_VIEW_CREATION_FAILURE_MSG = "CR_MICKEY_CLIENT_VIEW_CREATION_FAILURE";
    public static final int CR_MICKEY_CLIENT_VIEW_CREATION_FAILURE_CODE = 1101;
    public static final String CR_VIEW_INFO_DELETION_SUCCESS_MSG = "CR_MICKEY_CLIENT_VIEW_DELETION_SUCCESS";
    public static final int CR_VIEW_INFO_DELETION_SUCCESS_CODE = 1102;
    public static final String CR_VIEW_INFO_DELETION_FAILURE_MSG = "CR_MICKEY_CLIENT_VIEW_DELETION_FAILURE";
    public static final int CR_VIEW_INFO_DELETION_FAILURE_CODE = 1103;
    public static final String CR_REPORT_INFO_DELETION_SUCCESS_MSG = "CR_REPORT_INFO_DELETION_SUCCESS";
    public static final int CR_REPORT_INFO_DELETION_SUCCESS_CODE = 1104;
    public static final String CR_REPORT_INFO_DELETION_FAILURE_MSG = "CR_REPORT_INFO_DELETION_FAILURE";
    public static final int CR_REPORT_INFO_DELETION_FAILURE_CODE = 1105;
    public static final String CSV_REPORT_GENERATION_SUCCESS_MSG = "CSV_REPORT_GENERATION_SUCCESS";
    public static final int CSV_REPORT_GENERATION_SUCCESS_CODE = 1106;
    public static final String CSV_REPORT_GENERATION_FAILURE_MSG = "CSV_REPORT_GENERATION_FAILURE";
    public static final int CSV_REPORT_GENERATION_FAILURE_CODE = 1107;
    public static final String XSL_REPORT_GENERATION_SUCCESS_MSG = "XSL_REPORT_GENERATION_SUCCESS";
    public static final int XSL_REPORT_GENERATION_SUCCESS_CODE = 1108;
    public static final String XSL_REPORT_GENERATION_FAILURE_MSG = "XSL_REPORT_GENERATION_FAILURE";
    public static final int XSL_REPORT_GENERATION_FAILURE_CODE = 1109;
    public static final String REPORT_ZIP_GENERATION_SUCCESS_MSG = "REPORT_ZIP_GENERATION_SUCCESS";
    public static final int REPORT_ZIP_GENERATION_SUCCESS_CODE = 1110;
    public static final String REPORT_ZIP_GENERATION_FAILURE_MSG = "REPORT_ZIP_GENERATION_FAILURE";
    public static final int REPORT_ZIP_GENERATION_FAILURE_CODE = 1111;
    public static final int REPORT_IS_EMPTY = 1112;
    public static final int PDF_REPORT_GENERATION_SUCCESS_CODE = 1113;
    public static final int PDF_REPORT_GENERATION_FAILURE_CODE = 1114;
    public static final String MESSAGE_UPDATE_SUCCESS_MSG = "MESSAGE_UPDATE_SUCCESSFUL";
    public static final int MESSAGE_UPDATE_SUCCESS_CODE = 1200;
    public static final String MESSAGE_UPDATE_FAILED_MSG = "MESSAGE_UPDATE_FAILED";
    public static final int MESSAGE_UPDATE_FAILED_CODE = 1201;
    public static final String MAIL_HAS_BEEN_SENT_SUCCESSFUL_MSG = "MAIL_HAS_BEEN_SENT_SUCCESSFUL";
    public static final int MAIL_HAS_BEEN_SENT_SUCCESS_CODE = 1300;
    public static final String MAIL_SENDING_FAILED_MSG = "MAIL_SENDING_FAILED";
    public static final int MAIL_SENDING_FAILED_CODE = 1301;
    public static final String SCHEDULER_CLASS_NOT_DEFINED_MSG = "Scheduler can not be configured";
    public static final Integer SCHEDULER_CLASS_NOT_DEFINED_CODE;
    public static final String SCHEDULER_TIME_EXPIRED_MSG = "Scheduler time has been expired";
    public static final Integer SCHEDULER_TIME_EXPIRED_CODE;
    public static final String CONSENT_LOGGER_UPDATE_FAILED_MSG = "CONSENT_LOGGER_FAILED";
    public static final int CONSENT_LOGGER_UPDATE_FAILED_CODE = 1401;
    public static final String CONSENT_EVENT_LOGGER_UPDATE_FAILED_MSG = "CONSENT_LOGGER_FAILED";
    public static final int CONSENT_EVENT_LOGGER_UPDATE_FAILED_CODE = 1403;
    public static final String CONSENT_STATUS_UPDATE_FAILED_MSG = "CONSENT_LOGGER_FAILED";
    public static final int CONSENT_STATUS_UPDATE_FAILED_CODE = 1405;
    public static final String SERVER_URL_NOT_FOUND_MSG = "SERVER_URL_NOT_FOUND";
    public static final int SERVER_URL_NOT_FOUND_CODE = 1401;
    public static final String UNABLE_TO_ESTABLISH_HTTP_CONNECTION_MSG = "dc.admin.proxy.Unable_to_establish_http_connection";
    public static final int UNABLE_TO_ESTABLISH_HTTP_CONNECTION_CODE = 1501;
    public static final String UNABLE_TO_ESTABLISH_DIRECT_CONNECTION_MSG = "dc.admin.proxy.error.direct_connection_error";
    public static final int UNABLE_TO_ESTABLISH_DIRECT_CONNECTION_CODE = 1502;
    public static final String INCORRECT_HOST_PORT_MSG = "dc.admin.proxy.error.incorrect_host";
    public static final int INCORRECT_HOST_PORT_CODE = 1503;
    public static final String PROXY_ESTABLISHED_SUCCESSFULLY_MSG = "PROXY_CONNECTION_ESTABLISHED_SUCCESSFULLY";
    public static final int PROXY_ESTABLISHED_SUCCESSFULLY_CODE = 1504;
    public static final String FILE_WRITE_SUCCESS_MSG = "FILE_WRITE_SUCCESS";
    public static final int FILE_WRITE_SUCCESS_CODE = 3001;
    public static final int FILE_DELETE_FAILURE_CODE = 3002;
    public static final String UNABLE_TO_SET_LOCALE_MSG = "UNABLE_TO_SET_LOCALE";
    public static final int UNABLE_TO_SET_LOCALE_ERROR_CODE = 4001;
    public static final String UNABLE_TO_SET_URL_BUNDLE_MSG = "UNABLE_TO_SET_URL_BUNDLE";
    public static final int UNABLE_TO_SET_URL_BUNDLE_ERROR_CODE = 4002;
    public static final String UNABLE_TO_GET_IDENTITY_KEYS_FROM_DB_MSG = "UNABLE_TO_GET_IDENTITY_KEYS_FROM_DB";
    public static final int UNABLE_TO_GET_IDENTITY_KEYS_FROM_DB_ERROR_CODE = 4003;
    public static final String UNABLE_TO_GET_URL_FROM_DB_MSG = "UNABLE_TO_GET_URL_FROM_DB";
    public static final int UNABLE_TO_GET_URL_FROM_DB_ERROR_CODE = 4004;
    public static final String UNABLE_TO_REPLACE_URL_FROM_DB_MSG = "UNABLE TO GENERATE THE URL SINCE KEY WAS NOT AVAILABLE FROM DB";
    public static final int UNABLE_TO_REPLACE_URL_FROM_DB_ERROR_CODE = 4005;
    public static final String UNABLE_TO_FIND_KEY_FROM_PROPS_MSG = "UNABLE TO FIND THE GIVEN KEY FROM URL PROPERTY FILES";
    public static final int UNABLE_TO_FIND_KEY_FROM_PROPS_ERROR_CODE = 4006;
    public static final String VALID_PATH_MSG = "VALID_PATH";
    public static final int VALID_PATH_SUCCESS_CODE = 5000;
    public static final String UNABLE_TO_SET_EMPTY_PATH_MSG = "UNABLE_TO_EMPTY_PATH";
    public static final int UNABLE_TO_SET_EMPTY_PATH_ERROR_CODE = 5001;
    public static final String UNABLE_TO_SET_PATH_UNDER_SERVER_HOME_MSG = "UNABLE_TO_SET_PATH_UNDER_SERVER_HOME";
    public static final int UNABLE_TO_SET_PATH_UNDER_SERVER_HOME_ERROR_CODE = 5002;
    public static final String UNABLE_TO_SET_RESERVED_PATH_MSG = "UNABLE_TO_SET_RESERVED_PATH";
    public static final int UNABLE_TO_SET_RESERVED_PATH_ERROR_CODE = 5003;
    public static final String UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED_MSG = "UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED";
    public static final int UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED_ERROR_CODE = 5004;
    public static final String FAILED_TO_VALIDATE_GIVEN_PATH_MSG = "FAILED_TO_VALIDATE_GIVEN_PATH";
    public static final int FAILED_TO_VALIDATE_GIVEN_PATH_ERROR_CODE = 5009;
    public static final int UNABLE_TO_GET_QUICK_LINK_LIST = 6000;
    public static final int UNABLE_TO_SET_QUICKLINK_FOR_ACTION = 6001;
    public static final int UNABLE_TO_GET_APPLICABLE_PREDEFINED_QUICK_LINK = 6002;
    public static final int UNABLE_TO_GET_DYNAMIC_QUICK_LINK_LIST = 6003;
    public static final int UNABLE_TO_GET_CLOSED_QUICK_LINK_LIST = 6004;
    public static final int UNABLE_TO_GET_ALL_PREDEFINED_QUICK_LINK_LIST = 6005;
    public static final int UNABLE_TO_GET_ONDEMAND_QUICK_LINK_LIST = 6006;
    
    public static String getErrorMessageByCode(final Integer code) {
        return FrameworkStatusCodes.ERROR_CODES_AND_STATUS.get(code);
    }
    
    public static Integer getErrorCode(final String message) {
        return DBConstants.getValueForKey(FrameworkStatusCodes.ERROR_CODES_AND_STATUS, message);
    }
    
    public static ResponseStatusBean setResponseCodeInBean(final int errorCode) {
        return new ResponseStatusBean(errorCode);
    }
    
    public static ResponseStatusBean setResponseCodeInBean(final int errorCode, final String msg) {
        return new ResponseStatusBean(errorCode, msg);
    }
    
    static {
        ERROR_CODES_AND_STATUS = new HashMap<Integer, String>();
        DATA_OBJECT_CREATION_FAILURE_CODE = 1003;
        SUCCESS_RESPONSE_CODE = 200;
        ERROR_CODE = 400;
        CONFLICT_ERROR_CODE = 409;
        UNPROCESSABLE_DATA_CODE = 422;
        NO_CONTENT_RESPONSE_CODE = 204;
        NOT_SUPPORTED_RESPONSE_CODE = 505;
        SCHEDULER_CLASS_NOT_DEFINED_CODE = 3000;
        SCHEDULER_TIME_EXPIRED_CODE = 3001;
        FrameworkStatusCodes.ERROR_CODES_AND_STATUS.put(1501, "dc.admin.proxy.Unable_to_establish_http_connection");
        FrameworkStatusCodes.ERROR_CODES_AND_STATUS.put(1502, "dc.admin.proxy.error.direct_connection_error");
        FrameworkStatusCodes.ERROR_CODES_AND_STATUS.put(1503, "dc.admin.proxy.error.incorrect_host");
        FrameworkStatusCodes.ERROR_CODES_AND_STATUS.put(1504, "PROXY_CONNECTION_ESTABLISHED_SUCCESSFULLY");
    }
}
