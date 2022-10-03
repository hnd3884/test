package com.adventnet.iam.security;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;

public class IAMSecurityException extends RuntimeException
{
    private static final long serialVersionUID = -4917011881729693873L;
    public static final String NOT_AUTHENTICATED = "NOT_AUTHENTICATED";
    public static final String UNAUTHORISED = "UNAUTHORISED";
    public static final String UPLOAD_RULE_NOT_CONFIGURED = "UPLOAD_RULE_NOT_CONFIGURED";
    public static final String URL_RULE_NOT_CONFIGURED = "URL_RULE_NOT_CONFIGURED";
    public static final String INVALID_METHOD = "INVALID_METHOD";
    public static final String POST_ONLY_URL = "POST_ONLY_URL";
    public static final String INVALID_CSRF_TOKEN = "INVALID_CSRF_TOKEN";
    public static final String MIGRATION_CSRF_COOKIE_PARAM_MISMATCH = "MIGRATION_CSRF_COOKIE_PARAM_MISMATCH";
    public static final String LESS_THAN_MIN_OCCURANCE = "LESS_THAN_MIN_OCCURANCE";
    public static final String MORE_THAN_MAX_OCCURANCE = "MORE_THAN_MAX_OCCURANCE";
    public static final String LESS_THAN_MIN_LENGTH = "LESS_THAN_MIN_LENGTH";
    public static final String MORE_THAN_MAX_LENGTH = "MORE_THAN_MAX_LENGTH";
    public static final String XSS_DETECTED = "XSS_DETECTED";
    public static final String PATTERN_NOT_MATCHED = "PATTERN_NOT_MATCHED";
    public static final String PATTERN_NOT_DEFINED = "PATTERN_NOT_DEFINED";
    public static final String EXTRA_PARAM_FOUND = "EXTRA_PARAM_FOUND";
    public static final String EXTRA_PARAM_LIMIT_EXCEEDED = "EXTRA_PARAM_LIMIT_EXCEEDED";
    public static final String URL_ACTION_PARAM_MISSING = "URL_ACTION_PARAM_MISSING";
    public static final String UNMATCHED_FILE_CONTENT_TYPE = "UNMATCHED_FILE_CONTENT_TYPE";
    public static final String FILE_SIZE_MORE_THAN_ALLOWED_SIZE = "FILE_SIZE_MORE_THAN_ALLOWED_SIZE";
    public static final String URL_FILE_UPLOAD_MAX_SIZE_LIMIT_EXCEEDED = "URL_FILE_UPLOAD_MAX_SIZE_LIMIT_EXCEEDED";
    public static final String INVALID_FILE_NAME = "INVALID_FILE_NAME";
    public static final String INVALID_FILE_EXTENSION = "INVALID_FILE_EXTENSION";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String INTERNAL_IP_ACCESS_ONLY = "INTERNAL_IP_ACCESS_ONLY";
    public static final String TRUSTED_IP_ACCESS_ONLY = "TRUSTED_IP_ACCESS_ONLY";
    public static final String DATATYPE_NOT_MATCHED = "DATATYPE_NOT_MATCHED";
    public static final String MISSING_INPUT_VALIDATION = "MISSING_INPUT_VALIDATION";
    public static final String UNABLE_TO_DECRYPT = "UNABLE_TO_DECRYPT";
    public static final String UNABLE_TO_PARSE_DATA_TYPE = "UNABLE_TO_PARSE_DATA_TYPE";
    public static final String INVALID_TICKET = "INVALID_TICKET";
    public static final String INVALID_TICKET_SALT = "INVALID_TICKET_SALT";
    public static final String INVALID_TIME_SALT = "INVALID_TIME_SALT";
    public static final String REQUEST_SIZE_MORE_THAN_ALLOWED_SIZE = "REQUEST_SIZE_MORE_THAN_ALLOWED_SIZE";
    public static final String BLOCK_LISTED_IP = "BLOCK_LISTED_IP";
    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String SERVICE_NOT_CONFIGURED = "SERVICE_NOT_CONFIGURED";
    public static final String USERNAME_NOT_SET = "USERNAME_NOT_SET";
    public static final String IP_NOT_ALLOWED = "IP_NOT_ALLOWED";
    public static final String PASSWORD_EXPIRED = "PASSWORD_EXPIRED";
    public static final String REGISTRATION_NOT_ALLOWED = "REGISTRATION_NOT_ALLOWED";
    public static final String REMOTE_SERVER_ERROR = "REMOTE_SERVER_ERROR";
    public static final String ACCOUNT_DOES_NOT_EXIST = "ACCOUNT_DOES_NOT_EXIST";
    public static final String ACCOUNT_NOT_ACTIVE = "ACCOUNT_NOT_ACTIVE";
    public static final String ACCOUNT_REGISTRATION_NOT_CONFIRMED = "ACCOUNT_REGISTRATION_NOT_CONFIRMED";
    public static final String ISCSCOPE_MISMATCH = "ISCSCOPE_MISMATCH";
    public static final String INVALID_ISCZUID = "INVALID_ISCZUID";
    public static final String INVALID_ISCTICKET = "INVALID_ISCTICKET";
    public static final String INVALID_SCOPE = "INVALID_SCOPE";
    public static final String INVALID_OAUTHTOKEN = "INVALID_OAUTHTOKEN";
    public static final String INVALID_OAUTHSCOPE = "INVALID_OAUTHSCOPE";
    public static final String OAUTHTOKEN_EXPIRED = "OAUTHTOKEN_EXPIRED";
    public static final String ISC_PARAMS_MISSING = "ISC_PARAMS_MISSING";
    public static final String EXCEEDS_ISCTICKETS_THRESHOLD = "EXCEEDS_ISCTICKETS_THRESHOLD";
    public static final String CANONICALIZE_FAILED = "CANONICALIZE_FAILED";
    public static final String INVALID_XSSFILTER_CONFIGURATION = "INVALID_XSSFILTER_CONFIGURATION";
    public static final String WRITE_OPERATION_NOT_ALLOWED = "WRITE_OPERATION_NOT_ALLOWED";
    public static final String OUT_OF_RANGE = "OUT_OF_RANGE";
    public static final String EMPTY_VALUE_NOT_ALLOWED = "EMPTY_VALUE_NOT_ALLOWED";
    public static final String INVALID_VALUE_NOT_ALLOWED = "INVALID_VALUE_NOT_ALLOWED";
    public static final String EMPTY_FILE_NOT_ALLOWED = "EMPTY_FILE_NOT_ALLOWED";
    public static final String INVALID_ARGUMENTS_PASSED = "INVALID_ARGUMENT(S)_PASSED";
    public static final String INVALID_CONTEXT_PATH = "INVALID_CONTEXT_PATH";
    public static final String IMPORT_URL_CERTIFICATE = "IMPORT_URL_CERTIFICATE";
    public static final String IMPORT_URL_SSL_HANDSHAKE_FAILED = "IMPORT_URL_SSL_HANDSHAKE_FAILED";
    public static final String REQUEST_HEADER_NOT_CONFIGURED = "REQUEST_HEADER_NOT_CONFIGURED";
    public static final String VERSION_MISMATCHED = "VERSION_MISMATCHED";
    public static final String UNABLE_TO_DETECT_VALUE = "UNABLE_TO_DETECT_VALUE";
    public static final String DEVICE_NOT_ALLOWED = "DEVICE_NOT_ALLOWED";
    public static final String JSON_TEMPLATE_RULE_NOT_DEFINED = "JSON_TEMPLATE_RULE_NOT_DEFINED";
    public static final String JSON_PARSE_ERROR = "JSON_PARSE_ERROR";
    public static final String INVALID_JSON_CONFIGURATION = "INVALID_JSON_CONFIGURATION";
    public static final String EXTRA_KEY_FOUND = "EXTRA_KEY_FOUND_IN_JSON";
    public static final String EXTRA_VALUE_FOUND = "EXTRA_VALUE_FOUND_IN_JSONARRAY";
    public static final String EXCEEDS_EXTRA_KEY_LIMIT = "EXCEEDS_EXTRA_KEY_LIMIT_IN_JSON";
    public static final String ARRAY_SIZE_OUT_OF_RANGE = "ARRAY_SIZE_OUT_OF_RANGE";
    public static final String TEMPLATE_RULE_NOT_DEFINED = "TEMPLATE_RULE_NOT_DEFINED";
    public static final String INVALID_TEMPLATE_CONFIGURATION = "INVALID_TEMPLATE_CONFIGURATION";
    public static final String VCARD_PARSE_ERROR = "VCARD/VCARDARRAY PARSE ERROR";
    public static final String INVALID_VCARD_CONFIGURATION = "INVALID_VCARD_CONFIGURATION";
    public static final String INVALID_VCARD = "INVALID VCARD OBJECT/ARRAY";
    public static final String PROPERTIES_PARSE_ERROR = "PROPERTIES_PARSE_ERROR";
    public static final String CSV_PARSE_ERROR = "CSV PARSE ERROR";
    public static final String INVALID_CSV_HEADER = "INVALID CSV HEADER";
    public static final String INVALID_CSV_FORMAT = " INVALID CSV FORMAT ";
    public static final String CORS_NOT_CONFIGURED = "CORS_NOT_CONFIGURED";
    public static final String UNAUTHORIZED_CORS_REQUEST = "UNAUTHORIZED_CORS_REQUEST";
    public static final String UNABLE_TO_PARSE_DOCUMENT = "UNABLE_TO_PARSE_DOCUMENT";
    public static final String XML_VALIDATION_ERROR = "XML_VALIDATION_ERROR";
    public static final String SCHEMA_NOT_DEFINED = "SCHEMA_NOT_DEFINED";
    public static final String UNABLE_TO_READ_INPUTSTREAM = "UNABLE_TO_READ_INPUTSTREAM";
    public static final String UNABLE_TO_ENCODE_INPUTSTREAM = "UNABLE_TO_ENCODE_INPUTSTREAM";
    public static final String INVALID_CONFIGURATION = "INVALID_CONFIGURATION";
    public static final String INVALID_THROTTLES_CONFIGURATION = "INVALID_THROTTLES_CONFIGURATION";
    public static final String INVALID_THROTTLE_CONFIGURATION = "INVALID_THROTTLE_CONFIGURATION";
    public static final String INVALID_INPUTSTREAM = "INVALID_INPUTSTREAM";
    public static final String ISC_UNSCOPED_SERVICE = "ISC_UNSCOPED_SERVICE";
    public static final String ISC_INVALID_SIGNATURE = "ISC_INVALID_SIGNATURE";
    public static final String ISC_SIGNATURE_EXPIRED = "ISC_SIGNATURE_EXPIRED";
    public static final String ISC_SIGNATURE_NOT_PRESENT = "ISC_SIGNATURE_NOT_PRESENT";
    public static final String ISC_SIGNATURE_NOT_SUPPORTED = "ISC_SIGNATURE_NOT_SUPPORTED";
    public static final String ISC_PUBLIC_KEY_NOT_CONFIGURED = "ISC_PUBLIC_KEY_NOT_CONFIGURED";
    public static final String ISC_PRIVATE_KEY_NOT_CONFIGURED = "ISC_PRIVATE_KEY_NOT_CONFIGURED";
    public static final String ISC_SIGNATURE_VERIFICATION_FAILED = "ISC_SIGNATURE_VERIFICATION_FAILED";
    public static final String ISC_SIGNATURE_INVALID_SERVICE_NAME = "ISC_SIGNATURE_INVALID_SERVICE_NAME";
    public static final String ISC_SCOPED_SERVICES_NOT_CONFIGURED = "ISC_SCOPED_SERVICES_NOT_CONFIGURED";
    public static final String BROWSER_COOKIES_DISABLED = "BROWSER_COOKIES_DISABLED";
    public static final String UNSUPPORTED_BROWSER_VERSION = "UNSUPPORTED_BROWSER_VERSION";
    public static final String HTML_PARSE_FAILED = "HTML_PARSE_FAILED";
    public static final String CSS_PARSE_FAILED = "CSS_PARSE_FAILED";
    ArrayList<ErrorInfo> errorsAsList;
    private static final Logger logger;
    public static final String INVALID_IMPORT_URL = "INVALID_IMPORT_URL";
    public static final String UNABLE_TO_IMPORT = "UNABLE_TO_IMPORT";
    public static final String INVALID_URL = "INVALID_URL";
    public static final String UNSUPPORTED_URL_PROTOCOL = "UNSUPPORTED_URL_PROTOCOL";
    public static final String INVALID_DOMAIN_NAME = "INVALID_DOMAIN_NAME";
    public static final String LAN_ACCESS_DENIED = "LAN_ACCESS_DENIED";
    public static final String URL_REDIRECT_LIMIT_EXCEEEDED = "URL_REDIRECT_LIMIT_EXCEEEDED";
    public static final String DISALLOWED_URL_REDIRECT = "DISALLOWED_URL_REDIRECT";
    public static final String VIRUS_DETECTED = "VIRUS_DETECTED";
    public static final String INVALID_AV_CONFIGURATION = "INVALID_AV_CONFIGURATION";
    public static final String URL_VALIDATOR_TEMPLATE_NAME_NOT_CONFIGURED = "URL_VALIDATOR_TEMPLATE_NAME_NOT_CONFIGURED";
    public static final String URL_VALIDATOR_RULE_NOT_DEFINED = "URL_VALIDATOR_RULE_NOT_DEFINED";
    public static final String UNSUPPORTED_ENCODING_IN_DATAURI = "UNSUPPORTED_ENCODING_IN_DATAURI";
    public static final String URL_INVALID_SCHEME = "URL_INVALID_SCHEME";
    public static final String URL_INVALID_CHARACTER = "URL_INVALID_CHARACTER";
    public static final String DATAURL_INVALID_HEADER = "DATAURL_INVALID_HEADER";
    public static final String URL_MAX_LENGTH_EXCEEDED = "URL_MAX_LENGTH_EXCEEDED";
    public static final String INVALID_PROXY_CONFIGURATION = "INVALID_PROXY_CONFIGURATION";
    public static final String UNAUTHORIZED_PROXY_REQUEST = "UNAUTHORIZED_PROXY_REQUEST";
    public static final String PROXYPOLICY_NOT_CONFIGURED = "PROXYPOLICY_NOT_CONFIGURED";
    public static final String PROXY_ACCESS_DENIED_INCOMPATIBLE_CONFIGURATION = "PROXY_ACCESS_DENIED_INCOMPATIBLE_CONFIGURATION";
    public static final String INVALID_REDIRECT_URL = "INVALID_REDIRECT_URL";
    public static final String XML_SCHEMA_RULE_NOT_DEFINED = "XML_SCHEMA_RULE_NOT_DEFINED";
    public static final String INVALID_STATUS_CODE = "INVALID_STATUS_CODE";
    public static final String INVALID_SYNTAX = "INVALID_SYNTAX";
    public static final String INVALID_PROXY_HOST_HEADER = "INVALID_PROXY_HOST_HEADER";
    public static final String UNKNOWN_HOST = "UNKNOWN_HOST";
    public static List<String> anomalous_Error_Codes;
    public static final String INVALID_CHARACTER_ENCODING = "INVALID_CHARACTER_ENCODING";
    public static final String INVALID_DECIMAL_VALUE = "INVALID_DECIMAL_VALUE";
    public static final String INCOMPLETE_ESCAPE_PATTERN = "INCOMPLETE_ESCAPE_PATTERN";
    public static final String URL_THROTTLES_LIMIT_EXCEEDED = "URL_THROTTLES_LIMIT_EXCEEDED";
    public static final String URL_LIVE_THROTTLES_LIMIT_EXCEEDED = "URL_LIVE_THROTTLES_LIMIT_EXCEEDED";
    public static final String URL_ROLLING_THROTTLES_LIMIT_EXCEEDED = "URL_ROLLING_THROTTLES_LIMIT_EXCEEDED";
    public static final String URL_FIXED_THROTTLES_LIMIT_EXCEEDED = "URL_FIXED_THROTTLES_LIMIT_EXCEEDED";
    public static final String HIP_REQUIRED = "HIP_REQUIRED";
    public static final String THROTTLE_HIP_VERIFICATION_FAILED = "THROTTLE_CAPTCHA_VERIFICATION_FAILED";
    public static final String CAPTCHA_VERIFICATION_FAILED = "CAPTCHA_VERIFICATION_FAILED";
    public static final String REMOTE_IP_LOCKED = "REMOTE_IP_LOCKED";
    public static final String SPAM_DETECTED = "SPAM_DETECTED";
    public static final String SPAM_DETECTION_FAILED = "SPAM_DETECTION_FAILED";
    public static final String REGEX_MAX_RECURSION_LIMIT_EXCEEEDED = "REGEX_MAX_RECURSION_LIMIT_EXCEEEDED";
    public static final String ZIPSANITIZER_RULE_NOT_CONFIGURED = "ZIPSANITIZER_RULE_NOT_CONFIGURED";
    public static final String ZIPSANITIZER_LEVEL_EXCEEDED = "ZIPSANITIZER_LEVEL_EXCEEDED";
    public static final String ZIPSANITIZER_FILES_SIZE_EXCEEDED = "ZIPSANITIZER_FILES_SIZE_EXCEEDED";
    public static final String ZIPSANITIZER_FILES_COUNT_EXCEEDED = "ZIPSANITIZER_FILES_COUNT_EXCEEDED";
    public static final String ZIPSANITIZER_ZIPSLIP_ATTACK = "ZIPSANITIZER_ZIPSLIP_ATTACK";
    public static final String ZIPSANITIZER_INVALID_FILE_EXTENSION = "ZIPSANITIZER_INVALID_FILE_EXTENSION";
    public static final String ZIPSANITIZER_INVALID_CONTENT_TYPE_FOUND = "ZIPSANITIZER_INVALID_CONTENT_TYPE_FOUND";
    public static final String ZIPSANITIZER_ZIP_DELETED = "ZIPSANITIZER_ZIP_DELETED";
    public static final String ZIPSANITIZER_UNSAFE_ZIP = "ZIPSANITIZER_UNSAFE_ZIP";
    public static final String ZIPSANITIZER_INVALID_ZIP = "ZIPSANITIZER_INVALID_ZIP";
    public static final String ZIPSANITIZER_UNSUPPORTED_ZIP = "ZIPSANITIZER_UNSUPPORTED_ZIP";
    public static final String ZIPSANITIZER_FILE_NOT_FOUND = "ZIPSANITIZER_FILE_NOT_FOUND";
    public static final String ZIPSANITIZER_ERROR = "ZIPSANITIZER_ERROR";
    public static final String URL_CONNECTION_FAILED = "URL_CONNECTION_FAILED";
    public static final String REQUEST_HEADER_NOT_FOUND = "REQUEST_HEADER_NOT_FOUND";
    public static final String COOKIE_NOT_FOUND = "COOKIE_NOT_FOUND";
    public static final String INPUTSTREAM_RULE_NOT_CONFIGURED = "INPUTSTREAM_RULE_NOT_CONFIGURED";
    public static final String PATTERN_MATCHER_TIMEDOUT = "PATTERN_MATCHER_TIMEDOUT";
    public static final String PATTERN_MATCHER_ITERATION_LIMIT_EXCEEDED = "PATTERN_MATCHER_ITERATION_LIMIT_EXCEEDED";
    public static final String INVALID_REQUEST_DISPATCH_PATH_URI = "INVALID_REQUEST_DISPATCH_PATH_URI";
    public static final String SENSITIVE_PARAM_NOT_ALLOWED_IN_QUERYSTRING = "SENSITIVE_PARAM_NOT_ALLOWED_IN_QUERYSTRING";
    public static final String BLACKLISTED_URL = "BLACKLISTED_URL";
    
    public IAMSecurityException() {
        this.errorsAsList = new ArrayList<ErrorInfo>();
    }
    
    public IAMSecurityException(final String errorCode) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0}", errorCode);
    }
    
    IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final List<String> fileFieldNames) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, (List)fileFieldNames);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\" ", new Object[] { errorCode, requestURI, remoteAddr, referrer });
    }
    
    IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String fieldName, final UploadFileRule fileRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, fieldName, fileRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", FieldName: \"{4}\", UploadFileRule: \"{5}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, fieldName, fileRule });
    }
    
    IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String contentType, final String fileName, final long fileSize, final String fieldName, final UploadFileRule uploadFileRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, contentType, fileName, fileSize, fieldName, uploadFileRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", Content-type: \"{4}\", Filename: \"{5}\", Filesize: \"{6}\" , FieldName : \"{7}\", UploadFileRule: \"{8}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, contentType, fileName, fileSize, fieldName, uploadFileRule });
    }
    
    public void addIAMSecurityException(final IAMSecurityException e) {
        this.errorsAsList.addAll(e.getErrorsList());
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final AccessInfo accessInfo, final AccessInfoLock accessInfoLock) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, accessInfo, accessInfoLock);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", [ {4}], [{5}].", new Object[] { errorCode, requestURI, remoteAddr, referrer, accessInfo.getThrottlesRule(), accessInfoLock.getViolatedThrottle() });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final int noOfOccurance) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, noOfOccurance);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", No Of Occurance: \"{5}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, noOfOccurance });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParameterValue: \"{5}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, embedParam);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParameterValue: \"{5}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final int noOfOccurance, final ParameterRule parameterRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, noOfOccurance, parameterRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", No Of Occurance: \"{5}\", Parameter Rule: \"{6}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, noOfOccurance, parameterRule });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final ParameterRule parameterRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, parameterRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", Parameter Rule: \"{5}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, parameterRule });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final ParameterRule parameterRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, parameterRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParameterValue: \"{5}\", Parameter Rule: \"{6}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, parameterRule });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam, final ParameterRule parameterRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, embedParam, parameterRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParameterValue: \"{5}\", Parameter Rule: \"{6}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, parameterRule });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam, final ParameterRule parameterRule, final ParameterRule embedParameterRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, embedParam, parameterRule, embedParameterRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParameterValue: \"{5}\", Parameter Rule: \"{6}\",Embed Parameter Rule: \"{7}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, parameterRule, embedParameterRule });
    }
    
    public IAMSecurityException(final String errorCode, final String xmlErrorMessage, final int xmlErrorLineNumber, final int xmlErrorColumnNumber) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, xmlErrorMessage, xmlErrorLineNumber, xmlErrorColumnNumber);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0}, XML Error Message: \"{1}\", Line Number: \"{2}\", Column Number: \"{3}\"", new Object[] { errorCode, xmlErrorMessage, xmlErrorLineNumber, xmlErrorColumnNumber });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final ParameterRule parameterRule, final String xmlErrorMessage, final int xmlErrorLineNumber, final int xmlErrorColumnNumber) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, parameterRule, xmlErrorMessage, xmlErrorLineNumber, xmlErrorColumnNumber);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", Parameter Rule: \"{5}\",  XML Error Message: \"{6}\", Line Number: \"{7}\", Column Number: \"{8}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, parameterRule, xmlErrorMessage, xmlErrorLineNumber, xmlErrorColumnNumber });
    }
    
    public IAMSecurityException(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final SecurityUtil.SENSITIVE_PARAM_TYPE paramType, final ParameterRule paramRule) {
        super(errorCode);
        this.errorsAsList = new ArrayList<ErrorInfo>();
        final ErrorInfo error = new ErrorInfo(errorCode, requestURI, remoteAddr, referrer, parameterName, paramType, paramRule);
        this.errorsAsList.add(error);
        IAMSecurityException.logger.log("INVALID_TICKET".equals(errorCode) ? Level.FINE : Level.INFO, "IAMSecurityException ErrorCode: {0},  RequestURI: \"{1}\", RemoteAddr: \"{2}\", Referrer: \"{3}\", ParameterName: \"{4}\", ParamType : \"{5}\", ParameterRule: \"{6}\"", new Object[] { errorCode, requestURI, remoteAddr, referrer, parameterName, paramType, paramRule });
    }
    
    public ArrayList<ErrorInfo> getErrorsList() {
        return this.errorsAsList;
    }
    
    public List<String> getErrorCodes() {
        final List<String> errorCodes = new ArrayList<String>(this.errorsAsList.size());
        for (final ErrorInfo errorInfo : this.errorsAsList) {
            errorCodes.add(errorInfo.getErrorCode());
        }
        return errorCodes;
    }
    
    public List<String> getFileFieldNames() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getFileFieldNames();
        }
        return null;
    }
    
    public String getErrorCode() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getErrorCode();
        }
        return null;
    }
    
    public List<JSONObject> getJsonExceptionTrace() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getJsonExceptionTraceList();
        }
        return null;
    }
    
    public void setJsonExceptionTrace(final List<JSONObject> jsonExceptionTraceList) {
        if (this.errorsAsList.size() > 0 && jsonExceptionTraceList != null && jsonExceptionTraceList.size() > 0) {
            this.errorsAsList.get(0).setJsonExceptionTraceList(jsonExceptionTraceList);
        }
    }
    
    public String getUri() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getUri();
        }
        return null;
    }
    
    public String getRemoteAddr() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getRemoteAddr();
        }
        return null;
    }
    
    public String getRemoteHost() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getRemoteAddr();
        }
        return null;
    }
    
    public String getReferrer() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getReferrer();
        }
        return null;
    }
    
    public String getEmbedParameterName() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getEmbedParameterName();
        }
        return null;
    }
    
    public String getParameterName() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getParameterName();
        }
        return null;
    }
    
    public ParameterRule getParameterRule() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getParameterRule();
        }
        return null;
    }
    
    public int getNoOfOccurance() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getNoOfOccurance();
        }
        return -1;
    }
    
    public String getParameterValue() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getParameterValue();
        }
        return null;
    }
    
    public ParameterRule getEmbedParameterRule() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getEmbedParameterRule();
        }
        return null;
    }
    
    public UploadFileRule getUploadFileRule() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getUploadFileRule();
        }
        return null;
    }
    
    public long getFileSize() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getFileSize();
        }
        return -1L;
    }
    
    public String getFieldName() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getFieldName();
        }
        return null;
    }
    
    public String getFileName() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getFileName();
        }
        return null;
    }
    
    public String getContentType() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getContentType();
        }
        return null;
    }
    
    public AccessInfo getURLAccessInfo() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getAccessInfo();
        }
        return null;
    }
    
    public String getXMLErrorMessage() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getXMLErrorMessage();
        }
        return "";
    }
    
    public int getXMLErrorLineNumber() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getXMLErrorLineNumber();
        }
        return -1;
    }
    
    public int getXMLErrorColumnNumber() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getXMLErrorColumnNumber();
        }
        return -1;
    }
    
    public AccessInfo getAccessInfo() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getAccessInfo();
        }
        return null;
    }
    
    public AccessInfoLock getAccessInfoLock() {
        if (this.errorsAsList.size() > 0) {
            return this.errorsAsList.get(0).getAccessInfoLock();
        }
        return null;
    }
    
    public String getHipDigest() {
        AccessInfo accessInfo = null;
        if (this.errorsAsList.size() > 0 && (accessInfo = this.errorsAsList.get(0).getAccessInfo()) != null) {
            return ((RollingWindowAccessInfoLock)accessInfo.getLock()).getHipDigest();
        }
        return null;
    }
    
    static {
        logger = Logger.getLogger(IAMSecurityException.class.getName());
        IAMSecurityException.anomalous_Error_Codes = null;
        (IAMSecurityException.anomalous_Error_Codes = new ArrayList<String>()).add("INVALID_TICKET");
        IAMSecurityException.anomalous_Error_Codes.add("INVALID_ISCTICKET");
        IAMSecurityException.anomalous_Error_Codes.add("INVALID_SCOPE");
        IAMSecurityException.anomalous_Error_Codes.add("INVALID_OAUTHTOKEN");
        IAMSecurityException.anomalous_Error_Codes.add("INVALID_OAUTHSCOPE");
    }
    
    public static class ErrorInfo
    {
        private String errorCode;
        private String uri;
        private String remoteAddr;
        private String referrer;
        private String parameterName;
        private int noOfOccurance;
        private String parameterValue;
        private String contentType;
        private long fileSize;
        private String fileName;
        private String fieldName;
        private String embedParameterName;
        private ParameterRule parameterRule;
        private ParameterRule embedParameterRule;
        private AccessInfo accessInfo;
        private AccessInfoLock accessInfoLock;
        private UploadFileRule uploadFileRule;
        private int xmlErrorLineNumber;
        private int xmlErrorColumnNumber;
        private String xmlErrorMessage;
        private List<JSONObject> jsonExceptionTraceList;
        private List<String> fileFieldNames;
        private SecurityUtil.SENSITIVE_PARAM_TYPE paramType;
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer) {
            this.errorCode = null;
            this.uri = null;
            this.remoteAddr = null;
            this.referrer = null;
            this.parameterName = null;
            this.noOfOccurance = 0;
            this.contentType = null;
            this.fileSize = -1L;
            this.fileName = null;
            this.fieldName = null;
            this.embedParameterName = null;
            this.parameterRule = null;
            this.embedParameterRule = null;
            this.accessInfo = null;
            this.accessInfoLock = null;
            this.uploadFileRule = null;
            this.xmlErrorLineNumber = -1;
            this.xmlErrorColumnNumber = -1;
            this.xmlErrorMessage = null;
            this.jsonExceptionTraceList = null;
            this.fileFieldNames = null;
            this.errorCode = errorCode;
            this.uri = requestURI;
            this.remoteAddr = remoteAddr;
            this.referrer = referrer;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final AccessInfo resourceAccessInfo, final AccessInfoLock accessInfoLock) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.accessInfo = resourceAccessInfo;
            this.accessInfoLock = accessInfoLock;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final int noOfOccurance) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
            this.noOfOccurance = noOfOccurance;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
            this.parameterValue = paramValue;
        }
        
        public ErrorInfo(final String errorCode) {
            this.errorCode = null;
            this.uri = null;
            this.remoteAddr = null;
            this.referrer = null;
            this.parameterName = null;
            this.noOfOccurance = 0;
            this.contentType = null;
            this.fileSize = -1L;
            this.fileName = null;
            this.fieldName = null;
            this.embedParameterName = null;
            this.parameterRule = null;
            this.embedParameterRule = null;
            this.accessInfo = null;
            this.accessInfoLock = null;
            this.uploadFileRule = null;
            this.xmlErrorLineNumber = -1;
            this.xmlErrorColumnNumber = -1;
            this.xmlErrorMessage = null;
            this.jsonExceptionTraceList = null;
            this.fileFieldNames = null;
            this.errorCode = errorCode;
        }
        
        private ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final List<String> fileFieldNames) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.fileFieldNames = fileFieldNames;
        }
        
        private ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String fieldName, final UploadFileRule fileRule) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.fieldName = fieldName;
            this.uploadFileRule = fileRule;
        }
        
        private ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String contentType, final String fileName, final long fileSize, final String fieldName, final UploadFileRule fileRule) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.contentType = contentType;
            this.fileSize = fileSize;
            this.fileName = fileName;
            this.fieldName = fieldName;
            this.uploadFileRule = fileRule;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam) {
            this(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue);
            this.embedParameterName = embedParam;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final int noOfOccurance, final ParameterRule paramRule) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
            this.noOfOccurance = noOfOccurance;
            this.parameterRule = paramRule;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final ParameterRule paramRule) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
            this.parameterRule = paramRule;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final ParameterRule paramRule) {
            this(errorCode, requestURI, remoteAddr, referrer);
            this.parameterName = parameterName;
            this.parameterValue = paramValue;
            this.parameterRule = paramRule;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam, final ParameterRule parameterRule) {
            this(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, parameterRule);
            this.embedParameterName = embedParam;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final String paramValue, final String embedParam, final ParameterRule parameterRule, final ParameterRule embedParameterRule) {
            this(errorCode, requestURI, remoteAddr, referrer, parameterName, paramValue, embedParam, parameterRule);
            this.embedParameterRule = embedParameterRule;
        }
        
        public ErrorInfo(final String errorCode, final String xmlErrorMessage, final int tempxmlErrorLineNumber, final int tempxmlErrorColumnNumber) {
            this(errorCode);
            this.xmlErrorMessage = xmlErrorMessage;
            this.xmlErrorLineNumber = tempxmlErrorLineNumber;
            this.xmlErrorColumnNumber = tempxmlErrorColumnNumber;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final ParameterRule paramRule, final String xmlErrorMessage, final int tempxmlErrorLineNumber, final int tempxmlErrorColumnNumber) {
            this(errorCode, requestURI, remoteAddr, referrer, parameterName, paramRule);
            this.xmlErrorMessage = xmlErrorMessage;
            this.xmlErrorLineNumber = tempxmlErrorLineNumber;
            this.xmlErrorColumnNumber = tempxmlErrorColumnNumber;
        }
        
        public ErrorInfo(final String errorCode, final String requestURI, final String remoteAddr, final String referrer, final String parameterName, final SecurityUtil.SENSITIVE_PARAM_TYPE paramType, final ParameterRule paramRule) {
            this(errorCode, requestURI, remoteAddr, referrer, parameterName, paramRule);
            this.paramType = paramType;
        }
        
        public void setUploadFileRule(final UploadFileRule uploadFileRule) {
            this.uploadFileRule = uploadFileRule;
        }
        
        public UploadFileRule getUploadFileRule() {
            return this.uploadFileRule;
        }
        
        public List<String> getFileFieldNames() {
            return this.fileFieldNames;
        }
        
        public void setEmbedParameterRule(final ParameterRule embedParamRule) {
            this.embedParameterRule = embedParamRule;
        }
        
        public ParameterRule getEmbedParameterRule() {
            return this.embedParameterRule;
        }
        
        public void setJsonExceptionTraceList(final List<JSONObject> jsonExceptionTraceList) {
            this.jsonExceptionTraceList = jsonExceptionTraceList;
        }
        
        public List<JSONObject> getJsonExceptionTraceList() {
            return this.jsonExceptionTraceList;
        }
        
        public void setFileSize(final long fileSize) {
            this.fileSize = fileSize;
        }
        
        public AccessInfo getAccessInfo() {
            return this.accessInfo;
        }
        
        public AccessInfoLock getAccessInfoLock() {
            return this.accessInfoLock;
        }
        
        public String getHipDigest() {
            return (this.accessInfo == null) ? null : ((RollingWindowAccessInfoLock)this.accessInfo.getLock()).getHipDigest();
        }
        
        public void setAccessInfo(final AccessInfo accessInfo) {
            this.accessInfo = accessInfo;
        }
        
        public void setParameterRule(final ParameterRule paramRule) {
            this.parameterRule = paramRule;
        }
        
        public ParameterRule getParameterRule() {
            return this.parameterRule;
        }
        
        public String getErrorCode() {
            return this.errorCode;
        }
        
        public void setErrorCode(final String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getUri() {
            return this.uri;
        }
        
        public void setUri(final String uri) {
            this.uri = uri;
        }
        
        public String getRemoteHost() {
            return this.remoteAddr;
        }
        
        public String getRemoteAddr() {
            return this.remoteAddr;
        }
        
        public void setRemoteAddr(final String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }
        
        public String getReferrer() {
            return this.referrer;
        }
        
        public void setReferrer(final String referrer) {
            this.referrer = referrer;
        }
        
        public String getEmbedParameterName() {
            return this.embedParameterName;
        }
        
        public void setEmbedParameterName(final String embedParam) {
            this.embedParameterName = embedParam;
        }
        
        public String getParameterName() {
            return this.parameterName;
        }
        
        public void setParameterName(final String parameterName) {
            this.parameterName = parameterName;
        }
        
        public int getNoOfOccurance() {
            return this.noOfOccurance;
        }
        
        public void setNoOfOccurance(final int noOfOccurance) {
            this.noOfOccurance = noOfOccurance;
        }
        
        public String getParameterValue() {
            return this.parameterValue;
        }
        
        public void setParameterValue(final String parameterValue) {
            this.parameterValue = parameterValue;
        }
        
        public String getContentType() {
            return this.contentType;
        }
        
        public long getFileSize() {
            return this.fileSize;
        }
        
        public String getFileName() {
            return this.fileName;
        }
        
        public String getFieldName() {
            return this.fieldName;
        }
        
        public String getXMLErrorMessage() {
            return this.xmlErrorMessage;
        }
        
        public int getXMLErrorLineNumber() {
            return this.xmlErrorLineNumber;
        }
        
        public int getXMLErrorColumnNumber() {
            return this.xmlErrorColumnNumber;
        }
    }
}
