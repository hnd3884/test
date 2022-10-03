package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.Locale;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPException extends Exception
{
    public static final int SUCCESS = 0;
    public static final int OPERATION_ERROR = 1;
    public static final int PROTOCOL_ERROR = 2;
    public static final int TIME_LIMIT_EXCEEDED = 3;
    public static final int SIZE_LIMIT_EXCEEDED = 4;
    public static final int COMPARE_FALSE = 5;
    public static final int COMPARE_TRUE = 6;
    public static final int AUTH_METHOD_NOT_SUPPORTED = 7;
    public static final int STRONG_AUTH_REQUIRED = 8;
    public static final int LDAP_PARTIAL_RESULTS = 9;
    public static final int REFERRAL = 10;
    public static final int ADMIN_LIMIT_EXCEEDED = 11;
    public static final int UNAVAILABLE_CRITICAL_EXTENSION = 12;
    public static final int CONFIDENTIALITY_REQUIRED = 13;
    public static final int SASL_BIND_IN_PROGRESS = 14;
    public static final int NO_SUCH_ATTRIBUTE = 16;
    public static final int UNDEFINED_ATTRIBUTE_TYPE = 17;
    public static final int INAPPROPRIATE_MATCHING = 18;
    public static final int CONSTRAINT_VIOLATION = 19;
    public static final int ATTRIBUTE_OR_VALUE_EXISTS = 20;
    public static final int INVALID_ATTRIBUTE_SYNTAX = 21;
    public static final int NO_SUCH_OBJECT = 32;
    public static final int ALIAS_PROBLEM = 33;
    public static final int INVALID_DN_SYNTAX = 34;
    public static final int IS_LEAF = 35;
    public static final int ALIAS_DEREFERENCING_PROBLEM = 36;
    public static final int INAPPROPRIATE_AUTHENTICATION = 48;
    public static final int INVALID_CREDENTIALS = 49;
    public static final int INSUFFICIENT_ACCESS_RIGHTS = 50;
    public static final int BUSY = 51;
    public static final int UNAVAILABLE = 52;
    public static final int UNWILLING_TO_PERFORM = 53;
    public static final int LOOP_DETECTED = 54;
    public static final int SORT_CONTROL_MISSING = 60;
    public static final int INDEX_RANGE_ERROR = 61;
    public static final int NAMING_VIOLATION = 64;
    public static final int OBJECT_CLASS_VIOLATION = 65;
    public static final int NOT_ALLOWED_ON_NONLEAF = 66;
    public static final int NOT_ALLOWED_ON_RDN = 67;
    public static final int ENTRY_ALREADY_EXISTS = 68;
    public static final int OBJECT_CLASS_MODS_PROHIBITED = 69;
    public static final int AFFECTS_MULTIPLE_DSAS = 71;
    public static final int OTHER = 80;
    public static final int SERVER_DOWN = 81;
    public static final int LDAP_TIMEOUT = 85;
    public static final int PARAM_ERROR = 89;
    public static final int CONNECT_ERROR = 91;
    public static final int LDAP_NOT_SUPPORTED = 92;
    public static final int CONTROL_NOT_FOUND = 93;
    public static final int NO_RESULTS_RETURNED = 94;
    public static final int MORE_RESULTS_TO_RETURN = 95;
    public static final int CLIENT_LOOP = 96;
    public static final int REFERRAL_LIMIT_EXCEEDED = 97;
    private static final long serialVersionUID = 1942111440459840394L;
    private final int resultCode;
    private final String matchedDN;
    private final String serverErrorMessage;
    
    public LDAPException() {
        this(null, 80, null, null);
    }
    
    public LDAPException(final String message) {
        this(message, 80, null, null);
    }
    
    public LDAPException(final String message, final int resultCode) {
        this(message, resultCode, null, null);
    }
    
    public LDAPException(final String message, final int resultCode, final String serverErrorMessage) {
        this(message, resultCode, serverErrorMessage, null);
    }
    
    public LDAPException(final String message, final int resultCode, final String serverErrorMessage, final String matchedDN) {
        super(getMessage(message, serverErrorMessage, resultCode));
        this.resultCode = resultCode;
        this.serverErrorMessage = serverErrorMessage;
        this.matchedDN = matchedDN;
    }
    
    public LDAPException(final com.unboundid.ldap.sdk.LDAPException ldapException) {
        this(ldapException.getMessage(), ldapException.getResultCode().intValue(), ldapException.getMessage(), ldapException.getMatchedDN());
    }
    
    private static String getMessage(final String message, final String serverErrorMessage, final int resultCode) {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        if (serverErrorMessage != null && !serverErrorMessage.isEmpty()) {
            return serverErrorMessage;
        }
        return ResultCode.valueOf(resultCode).getName();
    }
    
    public int getLDAPResultCode() {
        return this.resultCode;
    }
    
    public String getLDAPErrorMessage() {
        return this.serverErrorMessage;
    }
    
    public String getMatchedDN() {
        return this.matchedDN;
    }
    
    public final com.unboundid.ldap.sdk.LDAPException toLDAPException() {
        return new com.unboundid.ldap.sdk.LDAPException(ResultCode.valueOf(this.resultCode), this.getMessage(), this.matchedDN, null);
    }
    
    public String errorCodeToString() {
        return ResultCode.valueOf(this.resultCode).getName();
    }
    
    public String errorCodeToString(final Locale l) {
        return ResultCode.valueOf(this.resultCode).getName();
    }
    
    public static String errorCodeToString(final int code) {
        return ResultCode.valueOf(code).getName();
    }
    
    public static String errorCodeToString(final int code, final Locale locale) {
        return ResultCode.valueOf(code).getName();
    }
    
    @Override
    public String toString() {
        return this.toLDAPException().toString();
    }
}
