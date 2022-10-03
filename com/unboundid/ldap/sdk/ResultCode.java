package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResultCode implements Serializable
{
    public static final int SUCCESS_INT_VALUE = 0;
    public static final ResultCode SUCCESS;
    public static final int OPERATIONS_ERROR_INT_VALUE = 1;
    public static final ResultCode OPERATIONS_ERROR;
    public static final int PROTOCOL_ERROR_INT_VALUE = 2;
    public static final ResultCode PROTOCOL_ERROR;
    public static final int TIME_LIMIT_EXCEEDED_INT_VALUE = 3;
    public static final ResultCode TIME_LIMIT_EXCEEDED;
    public static final int SIZE_LIMIT_EXCEEDED_INT_VALUE = 4;
    public static final ResultCode SIZE_LIMIT_EXCEEDED;
    public static final int COMPARE_FALSE_INT_VALUE = 5;
    public static final ResultCode COMPARE_FALSE;
    public static final int COMPARE_TRUE_INT_VALUE = 6;
    public static final ResultCode COMPARE_TRUE;
    public static final int AUTH_METHOD_NOT_SUPPORTED_INT_VALUE = 7;
    public static final ResultCode AUTH_METHOD_NOT_SUPPORTED;
    public static final int STRONG_AUTH_REQUIRED_INT_VALUE = 8;
    public static final ResultCode STRONG_AUTH_REQUIRED;
    public static final int REFERRAL_INT_VALUE = 10;
    public static final ResultCode REFERRAL;
    public static final int ADMIN_LIMIT_EXCEEDED_INT_VALUE = 11;
    public static final ResultCode ADMIN_LIMIT_EXCEEDED;
    public static final int UNAVAILABLE_CRITICAL_EXTENSION_INT_VALUE = 12;
    public static final ResultCode UNAVAILABLE_CRITICAL_EXTENSION;
    public static final int CONFIDENTIALITY_REQUIRED_INT_VALUE = 13;
    public static final ResultCode CONFIDENTIALITY_REQUIRED;
    public static final int SASL_BIND_IN_PROGRESS_INT_VALUE = 14;
    public static final ResultCode SASL_BIND_IN_PROGRESS;
    public static final int NO_SUCH_ATTRIBUTE_INT_VALUE = 16;
    public static final ResultCode NO_SUCH_ATTRIBUTE;
    public static final int UNDEFINED_ATTRIBUTE_TYPE_INT_VALUE = 17;
    public static final ResultCode UNDEFINED_ATTRIBUTE_TYPE;
    public static final int INAPPROPRIATE_MATCHING_INT_VALUE = 18;
    public static final ResultCode INAPPROPRIATE_MATCHING;
    public static final int CONSTRAINT_VIOLATION_INT_VALUE = 19;
    public static final ResultCode CONSTRAINT_VIOLATION;
    public static final int ATTRIBUTE_OR_VALUE_EXISTS_INT_VALUE = 20;
    public static final ResultCode ATTRIBUTE_OR_VALUE_EXISTS;
    public static final int INVALID_ATTRIBUTE_SYNTAX_INT_VALUE = 21;
    public static final ResultCode INVALID_ATTRIBUTE_SYNTAX;
    public static final int NO_SUCH_OBJECT_INT_VALUE = 32;
    public static final ResultCode NO_SUCH_OBJECT;
    public static final int ALIAS_PROBLEM_INT_VALUE = 33;
    public static final ResultCode ALIAS_PROBLEM;
    public static final int INVALID_DN_SYNTAX_INT_VALUE = 34;
    public static final ResultCode INVALID_DN_SYNTAX;
    public static final int ALIAS_DEREFERENCING_PROBLEM_INT_VALUE = 36;
    public static final ResultCode ALIAS_DEREFERENCING_PROBLEM;
    public static final int INAPPROPRIATE_AUTHENTICATION_INT_VALUE = 48;
    public static final ResultCode INAPPROPRIATE_AUTHENTICATION;
    public static final int INVALID_CREDENTIALS_INT_VALUE = 49;
    public static final ResultCode INVALID_CREDENTIALS;
    public static final int INSUFFICIENT_ACCESS_RIGHTS_INT_VALUE = 50;
    public static final ResultCode INSUFFICIENT_ACCESS_RIGHTS;
    public static final int BUSY_INT_VALUE = 51;
    public static final ResultCode BUSY;
    public static final int UNAVAILABLE_INT_VALUE = 52;
    public static final ResultCode UNAVAILABLE;
    public static final int UNWILLING_TO_PERFORM_INT_VALUE = 53;
    public static final ResultCode UNWILLING_TO_PERFORM;
    public static final int LOOP_DETECT_INT_VALUE = 54;
    public static final ResultCode LOOP_DETECT;
    public static final int SORT_CONTROL_MISSING_INT_VALUE = 60;
    public static final ResultCode SORT_CONTROL_MISSING;
    public static final int OFFSET_RANGE_ERROR_INT_VALUE = 61;
    public static final ResultCode OFFSET_RANGE_ERROR;
    public static final int NAMING_VIOLATION_INT_VALUE = 64;
    public static final ResultCode NAMING_VIOLATION;
    public static final int OBJECT_CLASS_VIOLATION_INT_VALUE = 65;
    public static final ResultCode OBJECT_CLASS_VIOLATION;
    public static final int NOT_ALLOWED_ON_NONLEAF_INT_VALUE = 66;
    public static final ResultCode NOT_ALLOWED_ON_NONLEAF;
    public static final int NOT_ALLOWED_ON_RDN_INT_VALUE = 67;
    public static final ResultCode NOT_ALLOWED_ON_RDN;
    public static final int ENTRY_ALREADY_EXISTS_INT_VALUE = 68;
    public static final ResultCode ENTRY_ALREADY_EXISTS;
    public static final int OBJECT_CLASS_MODS_PROHIBITED_INT_VALUE = 69;
    public static final ResultCode OBJECT_CLASS_MODS_PROHIBITED;
    public static final int AFFECTS_MULTIPLE_DSAS_INT_VALUE = 71;
    public static final ResultCode AFFECTS_MULTIPLE_DSAS;
    public static final int VIRTUAL_LIST_VIEW_ERROR_INT_VALUE = 76;
    public static final ResultCode VIRTUAL_LIST_VIEW_ERROR;
    public static final int OTHER_INT_VALUE = 80;
    public static final ResultCode OTHER;
    public static final int SERVER_DOWN_INT_VALUE = 81;
    public static final ResultCode SERVER_DOWN;
    public static final int LOCAL_ERROR_INT_VALUE = 82;
    public static final ResultCode LOCAL_ERROR;
    public static final int ENCODING_ERROR_INT_VALUE = 83;
    public static final ResultCode ENCODING_ERROR;
    public static final int DECODING_ERROR_INT_VALUE = 84;
    public static final ResultCode DECODING_ERROR;
    public static final int TIMEOUT_INT_VALUE = 85;
    public static final ResultCode TIMEOUT;
    public static final int AUTH_UNKNOWN_INT_VALUE = 86;
    public static final ResultCode AUTH_UNKNOWN;
    public static final int FILTER_ERROR_INT_VALUE = 87;
    public static final ResultCode FILTER_ERROR;
    public static final int USER_CANCELED_INT_VALUE = 88;
    public static final ResultCode USER_CANCELED;
    public static final int PARAM_ERROR_INT_VALUE = 89;
    public static final ResultCode PARAM_ERROR;
    public static final int NO_MEMORY_INT_VALUE = 90;
    public static final ResultCode NO_MEMORY;
    public static final int CONNECT_ERROR_INT_VALUE = 91;
    public static final ResultCode CONNECT_ERROR;
    public static final int NOT_SUPPORTED_INT_VALUE = 92;
    public static final ResultCode NOT_SUPPORTED;
    public static final int CONTROL_NOT_FOUND_INT_VALUE = 93;
    public static final ResultCode CONTROL_NOT_FOUND;
    public static final int NO_RESULTS_RETURNED_INT_VALUE = 94;
    public static final ResultCode NO_RESULTS_RETURNED;
    public static final int MORE_RESULTS_TO_RETURN_INT_VALUE = 95;
    public static final ResultCode MORE_RESULTS_TO_RETURN;
    public static final int CLIENT_LOOP_INT_VALUE = 96;
    public static final ResultCode CLIENT_LOOP;
    public static final int REFERRAL_LIMIT_EXCEEDED_INT_VALUE = 97;
    public static final ResultCode REFERRAL_LIMIT_EXCEEDED;
    public static final int CANCELED_INT_VALUE = 118;
    public static final ResultCode CANCELED;
    public static final int NO_SUCH_OPERATION_INT_VALUE = 119;
    public static final ResultCode NO_SUCH_OPERATION;
    public static final int TOO_LATE_INT_VALUE = 120;
    public static final ResultCode TOO_LATE;
    public static final int CANNOT_CANCEL_INT_VALUE = 121;
    public static final ResultCode CANNOT_CANCEL;
    public static final int ASSERTION_FAILED_INT_VALUE = 122;
    public static final ResultCode ASSERTION_FAILED;
    public static final int AUTHORIZATION_DENIED_INT_VALUE = 123;
    public static final ResultCode AUTHORIZATION_DENIED;
    public static final int E_SYNC_REFRESH_REQUIRED_INT_VALUE = 4096;
    public static final ResultCode E_SYNC_REFRESH_REQUIRED;
    public static final int NO_OPERATION_INT_VALUE = 16654;
    public static final ResultCode NO_OPERATION;
    public static final int INTERACTIVE_TRANSACTION_ABORTED_INT_VALUE = 30221001;
    public static final ResultCode INTERACTIVE_TRANSACTION_ABORTED;
    public static final int DATABASE_LOCK_CONFLICT_INT_VALUE = 30221002;
    public static final ResultCode DATABASE_LOCK_CONFLICT;
    public static final int MIRRORED_SUBTREE_DIGEST_MISMATCH_INT_VALUE = 30221003;
    public static final ResultCode MIRRORED_SUBTREE_DIGEST_MISMATCH;
    public static final int TOKEN_DELIVERY_MECHANISM_UNAVAILABLE_INT_VALUE = 30221004;
    public static final ResultCode TOKEN_DELIVERY_MECHANISM_UNAVAILABLE;
    public static final int TOKEN_DELIVERY_ATTEMPT_FAILED_INT_VALUE = 30221005;
    public static final ResultCode TOKEN_DELIVERY_ATTEMPT_FAILED;
    public static final int TOKEN_DELIVERY_INVALID_RECIPIENT_ID_INT_VALUE = 30221006;
    public static final ResultCode TOKEN_DELIVERY_INVALID_RECIPIENT_ID;
    public static final int TOKEN_DELIVERY_INVALID_ACCOUNT_STATE_INT_VALUE = 30221007;
    public static final ResultCode TOKEN_DELIVERY_INVALID_ACCOUNT_STATE;
    private static final ConcurrentHashMap<Integer, ResultCode> UNDEFINED_RESULT_CODES;
    private static final long serialVersionUID = 7609311304252378100L;
    private final int intValue;
    private final String name;
    private final String stringRepresentation;
    
    private ResultCode(final int intValue) {
        this.intValue = intValue;
        this.name = String.valueOf(intValue);
        this.stringRepresentation = this.name;
    }
    
    private ResultCode(final String name, final int intValue) {
        this.name = name;
        this.intValue = intValue;
        this.stringRepresentation = intValue + " (" + name + ')';
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ResultCode valueOf(final int intValue) {
        return valueOf(intValue, null);
    }
    
    public static ResultCode valueOf(final int intValue, final String name) {
        return valueOf(intValue, name, true);
    }
    
    public static ResultCode valueOf(final int intValue, final String name, final boolean createNewResultCode) {
        switch (intValue) {
            case 0: {
                return ResultCode.SUCCESS;
            }
            case 1: {
                return ResultCode.OPERATIONS_ERROR;
            }
            case 2: {
                return ResultCode.PROTOCOL_ERROR;
            }
            case 3: {
                return ResultCode.TIME_LIMIT_EXCEEDED;
            }
            case 4: {
                return ResultCode.SIZE_LIMIT_EXCEEDED;
            }
            case 5: {
                return ResultCode.COMPARE_FALSE;
            }
            case 6: {
                return ResultCode.COMPARE_TRUE;
            }
            case 7: {
                return ResultCode.AUTH_METHOD_NOT_SUPPORTED;
            }
            case 8: {
                return ResultCode.STRONG_AUTH_REQUIRED;
            }
            case 10: {
                return ResultCode.REFERRAL;
            }
            case 11: {
                return ResultCode.ADMIN_LIMIT_EXCEEDED;
            }
            case 12: {
                return ResultCode.UNAVAILABLE_CRITICAL_EXTENSION;
            }
            case 13: {
                return ResultCode.CONFIDENTIALITY_REQUIRED;
            }
            case 14: {
                return ResultCode.SASL_BIND_IN_PROGRESS;
            }
            case 16: {
                return ResultCode.NO_SUCH_ATTRIBUTE;
            }
            case 17: {
                return ResultCode.UNDEFINED_ATTRIBUTE_TYPE;
            }
            case 18: {
                return ResultCode.INAPPROPRIATE_MATCHING;
            }
            case 19: {
                return ResultCode.CONSTRAINT_VIOLATION;
            }
            case 20: {
                return ResultCode.ATTRIBUTE_OR_VALUE_EXISTS;
            }
            case 21: {
                return ResultCode.INVALID_ATTRIBUTE_SYNTAX;
            }
            case 32: {
                return ResultCode.NO_SUCH_OBJECT;
            }
            case 33: {
                return ResultCode.ALIAS_PROBLEM;
            }
            case 34: {
                return ResultCode.INVALID_DN_SYNTAX;
            }
            case 36: {
                return ResultCode.ALIAS_DEREFERENCING_PROBLEM;
            }
            case 48: {
                return ResultCode.INAPPROPRIATE_AUTHENTICATION;
            }
            case 49: {
                return ResultCode.INVALID_CREDENTIALS;
            }
            case 50: {
                return ResultCode.INSUFFICIENT_ACCESS_RIGHTS;
            }
            case 51: {
                return ResultCode.BUSY;
            }
            case 52: {
                return ResultCode.UNAVAILABLE;
            }
            case 53: {
                return ResultCode.UNWILLING_TO_PERFORM;
            }
            case 54: {
                return ResultCode.LOOP_DETECT;
            }
            case 60: {
                return ResultCode.SORT_CONTROL_MISSING;
            }
            case 61: {
                return ResultCode.OFFSET_RANGE_ERROR;
            }
            case 64: {
                return ResultCode.NAMING_VIOLATION;
            }
            case 65: {
                return ResultCode.OBJECT_CLASS_VIOLATION;
            }
            case 66: {
                return ResultCode.NOT_ALLOWED_ON_NONLEAF;
            }
            case 67: {
                return ResultCode.NOT_ALLOWED_ON_RDN;
            }
            case 68: {
                return ResultCode.ENTRY_ALREADY_EXISTS;
            }
            case 69: {
                return ResultCode.OBJECT_CLASS_MODS_PROHIBITED;
            }
            case 71: {
                return ResultCode.AFFECTS_MULTIPLE_DSAS;
            }
            case 76: {
                return ResultCode.VIRTUAL_LIST_VIEW_ERROR;
            }
            case 80: {
                return ResultCode.OTHER;
            }
            case 81: {
                return ResultCode.SERVER_DOWN;
            }
            case 82: {
                return ResultCode.LOCAL_ERROR;
            }
            case 83: {
                return ResultCode.ENCODING_ERROR;
            }
            case 84: {
                return ResultCode.DECODING_ERROR;
            }
            case 85: {
                return ResultCode.TIMEOUT;
            }
            case 86: {
                return ResultCode.AUTH_UNKNOWN;
            }
            case 87: {
                return ResultCode.FILTER_ERROR;
            }
            case 88: {
                return ResultCode.USER_CANCELED;
            }
            case 89: {
                return ResultCode.PARAM_ERROR;
            }
            case 90: {
                return ResultCode.NO_MEMORY;
            }
            case 91: {
                return ResultCode.CONNECT_ERROR;
            }
            case 92: {
                return ResultCode.NOT_SUPPORTED;
            }
            case 93: {
                return ResultCode.CONTROL_NOT_FOUND;
            }
            case 94: {
                return ResultCode.NO_RESULTS_RETURNED;
            }
            case 95: {
                return ResultCode.MORE_RESULTS_TO_RETURN;
            }
            case 96: {
                return ResultCode.CLIENT_LOOP;
            }
            case 97: {
                return ResultCode.REFERRAL_LIMIT_EXCEEDED;
            }
            case 118: {
                return ResultCode.CANCELED;
            }
            case 119: {
                return ResultCode.NO_SUCH_OPERATION;
            }
            case 120: {
                return ResultCode.TOO_LATE;
            }
            case 121: {
                return ResultCode.CANNOT_CANCEL;
            }
            case 122: {
                return ResultCode.ASSERTION_FAILED;
            }
            case 123: {
                return ResultCode.AUTHORIZATION_DENIED;
            }
            case 4096: {
                return ResultCode.E_SYNC_REFRESH_REQUIRED;
            }
            case 16654: {
                return ResultCode.NO_OPERATION;
            }
            case 30221001: {
                return ResultCode.INTERACTIVE_TRANSACTION_ABORTED;
            }
            case 30221002: {
                return ResultCode.DATABASE_LOCK_CONFLICT;
            }
            case 30221003: {
                return ResultCode.MIRRORED_SUBTREE_DIGEST_MISMATCH;
            }
            case 30221004: {
                return ResultCode.TOKEN_DELIVERY_MECHANISM_UNAVAILABLE;
            }
            case 30221005: {
                return ResultCode.TOKEN_DELIVERY_ATTEMPT_FAILED;
            }
            case 30221006: {
                return ResultCode.TOKEN_DELIVERY_INVALID_RECIPIENT_ID;
            }
            case 30221007: {
                return ResultCode.TOKEN_DELIVERY_INVALID_ACCOUNT_STATE;
            }
            default: {
                ResultCode rc = ResultCode.UNDEFINED_RESULT_CODES.get(intValue);
                if (rc == null) {
                    if (!createNewResultCode) {
                        return null;
                    }
                    if (name == null) {
                        rc = new ResultCode(intValue);
                    }
                    else {
                        rc = new ResultCode(name, intValue);
                    }
                    final ResultCode existingRC = ResultCode.UNDEFINED_RESULT_CODES.putIfAbsent(intValue, rc);
                    if (existingRC != null) {
                        return existingRC;
                    }
                }
                return rc;
            }
        }
    }
    
    public static ResultCode[] values() {
        return new ResultCode[] { ResultCode.SUCCESS, ResultCode.OPERATIONS_ERROR, ResultCode.PROTOCOL_ERROR, ResultCode.TIME_LIMIT_EXCEEDED, ResultCode.SIZE_LIMIT_EXCEEDED, ResultCode.COMPARE_FALSE, ResultCode.COMPARE_TRUE, ResultCode.AUTH_METHOD_NOT_SUPPORTED, ResultCode.STRONG_AUTH_REQUIRED, ResultCode.REFERRAL, ResultCode.ADMIN_LIMIT_EXCEEDED, ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ResultCode.CONFIDENTIALITY_REQUIRED, ResultCode.SASL_BIND_IN_PROGRESS, ResultCode.NO_SUCH_ATTRIBUTE, ResultCode.UNDEFINED_ATTRIBUTE_TYPE, ResultCode.INAPPROPRIATE_MATCHING, ResultCode.CONSTRAINT_VIOLATION, ResultCode.ATTRIBUTE_OR_VALUE_EXISTS, ResultCode.INVALID_ATTRIBUTE_SYNTAX, ResultCode.NO_SUCH_OBJECT, ResultCode.ALIAS_PROBLEM, ResultCode.INVALID_DN_SYNTAX, ResultCode.ALIAS_DEREFERENCING_PROBLEM, ResultCode.INAPPROPRIATE_AUTHENTICATION, ResultCode.INVALID_CREDENTIALS, ResultCode.INSUFFICIENT_ACCESS_RIGHTS, ResultCode.BUSY, ResultCode.UNAVAILABLE, ResultCode.UNWILLING_TO_PERFORM, ResultCode.LOOP_DETECT, ResultCode.SORT_CONTROL_MISSING, ResultCode.OFFSET_RANGE_ERROR, ResultCode.NAMING_VIOLATION, ResultCode.OBJECT_CLASS_VIOLATION, ResultCode.NOT_ALLOWED_ON_NONLEAF, ResultCode.NOT_ALLOWED_ON_RDN, ResultCode.ENTRY_ALREADY_EXISTS, ResultCode.OBJECT_CLASS_MODS_PROHIBITED, ResultCode.AFFECTS_MULTIPLE_DSAS, ResultCode.VIRTUAL_LIST_VIEW_ERROR, ResultCode.OTHER, ResultCode.SERVER_DOWN, ResultCode.LOCAL_ERROR, ResultCode.ENCODING_ERROR, ResultCode.DECODING_ERROR, ResultCode.TIMEOUT, ResultCode.AUTH_UNKNOWN, ResultCode.FILTER_ERROR, ResultCode.USER_CANCELED, ResultCode.PARAM_ERROR, ResultCode.NO_MEMORY, ResultCode.CONNECT_ERROR, ResultCode.NOT_SUPPORTED, ResultCode.CONTROL_NOT_FOUND, ResultCode.NO_RESULTS_RETURNED, ResultCode.MORE_RESULTS_TO_RETURN, ResultCode.CLIENT_LOOP, ResultCode.REFERRAL_LIMIT_EXCEEDED, ResultCode.CANCELED, ResultCode.NO_SUCH_OPERATION, ResultCode.TOO_LATE, ResultCode.CANNOT_CANCEL, ResultCode.ASSERTION_FAILED, ResultCode.AUTHORIZATION_DENIED, ResultCode.E_SYNC_REFRESH_REQUIRED, ResultCode.NO_OPERATION, ResultCode.INTERACTIVE_TRANSACTION_ABORTED, ResultCode.DATABASE_LOCK_CONFLICT, ResultCode.MIRRORED_SUBTREE_DIGEST_MISMATCH, ResultCode.TOKEN_DELIVERY_MECHANISM_UNAVAILABLE, ResultCode.TOKEN_DELIVERY_ATTEMPT_FAILED, ResultCode.TOKEN_DELIVERY_INVALID_RECIPIENT_ID, ResultCode.TOKEN_DELIVERY_INVALID_ACCOUNT_STATE };
    }
    
    public boolean isClientSideResultCode() {
        return isClientSideResultCode(this);
    }
    
    public static boolean isClientSideResultCode(final ResultCode resultCode) {
        switch (resultCode.intValue()) {
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isConnectionUsable() {
        return isConnectionUsable(this);
    }
    
    public static boolean isConnectionUsable(final ResultCode resultCode) {
        switch (resultCode.intValue()) {
            case 1:
            case 2:
            case 51:
            case 52:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 90:
            case 91: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.intValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof ResultCode && this.intValue == ((ResultCode)o).intValue));
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
    
    static {
        SUCCESS = new ResultCode(LDAPMessages.INFO_RC_SUCCESS.get(), 0);
        OPERATIONS_ERROR = new ResultCode(LDAPMessages.INFO_RC_OPERATIONS_ERROR.get(), 1);
        PROTOCOL_ERROR = new ResultCode(LDAPMessages.INFO_RC_PROTOCOL_ERROR.get(), 2);
        TIME_LIMIT_EXCEEDED = new ResultCode(LDAPMessages.INFO_RC_TIME_LIMIT_EXCEEDED.get(), 3);
        SIZE_LIMIT_EXCEEDED = new ResultCode(LDAPMessages.INFO_RC_SIZE_LIMIT_EXCEEDED.get(), 4);
        COMPARE_FALSE = new ResultCode(LDAPMessages.INFO_RC_COMPARE_FALSE.get(), 5);
        COMPARE_TRUE = new ResultCode(LDAPMessages.INFO_RC_COMPARE_TRUE.get(), 6);
        AUTH_METHOD_NOT_SUPPORTED = new ResultCode(LDAPMessages.INFO_RC_AUTH_METHOD_NOT_SUPPORTED.get(), 7);
        STRONG_AUTH_REQUIRED = new ResultCode(LDAPMessages.INFO_RC_STRONG_AUTH_REQUIRED.get(), 8);
        REFERRAL = new ResultCode(LDAPMessages.INFO_RC_REFERRAL.get(), 10);
        ADMIN_LIMIT_EXCEEDED = new ResultCode(LDAPMessages.INFO_RC_ADMIN_LIMIT_EXCEEDED.get(), 11);
        UNAVAILABLE_CRITICAL_EXTENSION = new ResultCode(LDAPMessages.INFO_RC_UNAVAILABLE_CRITICAL_EXTENSION.get(), 12);
        CONFIDENTIALITY_REQUIRED = new ResultCode(LDAPMessages.INFO_RC_CONFIDENTIALITY_REQUIRED.get(), 13);
        SASL_BIND_IN_PROGRESS = new ResultCode(LDAPMessages.INFO_RC_SASL_BIND_IN_PROGRESS.get(), 14);
        NO_SUCH_ATTRIBUTE = new ResultCode(LDAPMessages.INFO_RC_NO_SUCH_ATTRIBUTE.get(), 16);
        UNDEFINED_ATTRIBUTE_TYPE = new ResultCode(LDAPMessages.INFO_RC_UNDEFINED_ATTRIBUTE_TYPE.get(), 17);
        INAPPROPRIATE_MATCHING = new ResultCode(LDAPMessages.INFO_RC_INAPPROPRIATE_MATCHING.get(), 18);
        CONSTRAINT_VIOLATION = new ResultCode(LDAPMessages.INFO_RC_CONSTRAINT_VIOLATION.get(), 19);
        ATTRIBUTE_OR_VALUE_EXISTS = new ResultCode(LDAPMessages.INFO_RC_ATTRIBUTE_OR_VALUE_EXISTS.get(), 20);
        INVALID_ATTRIBUTE_SYNTAX = new ResultCode(LDAPMessages.INFO_RC_INVALID_ATTRIBUTE_SYNTAX.get(), 21);
        NO_SUCH_OBJECT = new ResultCode(LDAPMessages.INFO_RC_NO_SUCH_OBJECT.get(), 32);
        ALIAS_PROBLEM = new ResultCode(LDAPMessages.INFO_RC_ALIAS_PROBLEM.get(), 33);
        INVALID_DN_SYNTAX = new ResultCode(LDAPMessages.INFO_RC_INVALID_DN_SYNTAX.get(), 34);
        ALIAS_DEREFERENCING_PROBLEM = new ResultCode(LDAPMessages.INFO_RC_ALIAS_DEREFERENCING_PROBLEM.get(), 36);
        INAPPROPRIATE_AUTHENTICATION = new ResultCode(LDAPMessages.INFO_RC_INAPPROPRIATE_AUTHENTICATION.get(), 48);
        INVALID_CREDENTIALS = new ResultCode(LDAPMessages.INFO_RC_INVALID_CREDENTIALS.get(), 49);
        INSUFFICIENT_ACCESS_RIGHTS = new ResultCode(LDAPMessages.INFO_RC_INSUFFICIENT_ACCESS_RIGHTS.get(), 50);
        BUSY = new ResultCode(LDAPMessages.INFO_RC_BUSY.get(), 51);
        UNAVAILABLE = new ResultCode(LDAPMessages.INFO_RC_UNAVAILABLE.get(), 52);
        UNWILLING_TO_PERFORM = new ResultCode(LDAPMessages.INFO_RC_UNWILLING_TO_PERFORM.get(), 53);
        LOOP_DETECT = new ResultCode(LDAPMessages.INFO_RC_LOOP_DETECT.get(), 54);
        SORT_CONTROL_MISSING = new ResultCode(LDAPMessages.INFO_RC_SORT_CONTROL_MISSING.get(), 60);
        OFFSET_RANGE_ERROR = new ResultCode(LDAPMessages.INFO_RC_OFFSET_RANGE_ERROR.get(), 61);
        NAMING_VIOLATION = new ResultCode(LDAPMessages.INFO_RC_NAMING_VIOLATION.get(), 64);
        OBJECT_CLASS_VIOLATION = new ResultCode(LDAPMessages.INFO_RC_OBJECT_CLASS_VIOLATION.get(), 65);
        NOT_ALLOWED_ON_NONLEAF = new ResultCode(LDAPMessages.INFO_RC_NOT_ALLOWED_ON_NONLEAF.get(), 66);
        NOT_ALLOWED_ON_RDN = new ResultCode(LDAPMessages.INFO_RC_NOT_ALLOWED_ON_RDN.get(), 67);
        ENTRY_ALREADY_EXISTS = new ResultCode(LDAPMessages.INFO_RC_ENTRY_ALREADY_EXISTS.get(), 68);
        OBJECT_CLASS_MODS_PROHIBITED = new ResultCode(LDAPMessages.INFO_RC_OBJECT_CLASS_MODS_PROHIBITED.get(), 69);
        AFFECTS_MULTIPLE_DSAS = new ResultCode(LDAPMessages.INFO_RC_AFFECTS_MULTIPLE_DSAS.get(), 71);
        VIRTUAL_LIST_VIEW_ERROR = new ResultCode(LDAPMessages.INFO_RC_VIRTUAL_LIST_VIEW_ERROR.get(), 76);
        OTHER = new ResultCode(LDAPMessages.INFO_RC_OTHER.get(), 80);
        SERVER_DOWN = new ResultCode(LDAPMessages.INFO_RC_SERVER_DOWN.get(), 81);
        LOCAL_ERROR = new ResultCode(LDAPMessages.INFO_RC_LOCAL_ERROR.get(), 82);
        ENCODING_ERROR = new ResultCode(LDAPMessages.INFO_RC_ENCODING_ERROR.get(), 83);
        DECODING_ERROR = new ResultCode(LDAPMessages.INFO_RC_DECODING_ERROR.get(), 84);
        TIMEOUT = new ResultCode(LDAPMessages.INFO_RC_TIMEOUT.get(), 85);
        AUTH_UNKNOWN = new ResultCode(LDAPMessages.INFO_RC_AUTH_UNKNOWN.get(), 86);
        FILTER_ERROR = new ResultCode(LDAPMessages.INFO_RC_FILTER_ERROR.get(), 87);
        USER_CANCELED = new ResultCode(LDAPMessages.INFO_RC_USER_CANCELED.get(), 88);
        PARAM_ERROR = new ResultCode(LDAPMessages.INFO_RC_PARAM_ERROR.get(), 89);
        NO_MEMORY = new ResultCode(LDAPMessages.INFO_RC_NO_MEMORY.get(), 90);
        CONNECT_ERROR = new ResultCode(LDAPMessages.INFO_RC_CONNECT_ERROR.get(), 91);
        NOT_SUPPORTED = new ResultCode(LDAPMessages.INFO_RC_NOT_SUPPORTED.get(), 92);
        CONTROL_NOT_FOUND = new ResultCode(LDAPMessages.INFO_RC_CONTROL_NOT_FOUND.get(), 93);
        NO_RESULTS_RETURNED = new ResultCode(LDAPMessages.INFO_RC_NO_RESULTS_RETURNED.get(), 94);
        MORE_RESULTS_TO_RETURN = new ResultCode(LDAPMessages.INFO_RC_MORE_RESULTS_TO_RETURN.get(), 95);
        CLIENT_LOOP = new ResultCode(LDAPMessages.INFO_RC_CLIENT_LOOP.get(), 96);
        REFERRAL_LIMIT_EXCEEDED = new ResultCode(LDAPMessages.INFO_RC_REFERRAL_LIMIT_EXCEEDED.get(), 97);
        CANCELED = new ResultCode(LDAPMessages.INFO_RC_CANCELED.get(), 118);
        NO_SUCH_OPERATION = new ResultCode(LDAPMessages.INFO_RC_NO_SUCH_OPERATION.get(), 119);
        TOO_LATE = new ResultCode(LDAPMessages.INFO_RC_TOO_LATE.get(), 120);
        CANNOT_CANCEL = new ResultCode(LDAPMessages.INFO_RC_CANNOT_CANCEL.get(), 121);
        ASSERTION_FAILED = new ResultCode(LDAPMessages.INFO_RC_ASSERTION_FAILED.get(), 122);
        AUTHORIZATION_DENIED = new ResultCode(LDAPMessages.INFO_RC_AUTHORIZATION_DENIED.get(), 123);
        E_SYNC_REFRESH_REQUIRED = new ResultCode(LDAPMessages.INFO_RC_E_SYNC_REFRESH_REQUIRED.get(), 4096);
        NO_OPERATION = new ResultCode(LDAPMessages.INFO_RC_NO_OPERATION.get(), 16654);
        INTERACTIVE_TRANSACTION_ABORTED = new ResultCode(LDAPMessages.INFO_RC_INTERACTIVE_TRANSACTION_ABORTED.get(), 30221001);
        DATABASE_LOCK_CONFLICT = new ResultCode(LDAPMessages.INFO_RC_DATABASE_LOCK_CONFLICT.get(), 30221002);
        MIRRORED_SUBTREE_DIGEST_MISMATCH = new ResultCode(LDAPMessages.INFO_RC_MIRRORED_SUBTREE_DIGEST_MISMATCH.get(), 30221003);
        TOKEN_DELIVERY_MECHANISM_UNAVAILABLE = new ResultCode(LDAPMessages.INFO_RC_TOKEN_DELIVERY_MECHANISM_UNAVAILABLE.get(), 30221004);
        TOKEN_DELIVERY_ATTEMPT_FAILED = new ResultCode(LDAPMessages.INFO_RC_TOKEN_DELIVERY_ATTEMPT_FAILED.get(), 30221005);
        TOKEN_DELIVERY_INVALID_RECIPIENT_ID = new ResultCode(LDAPMessages.INFO_RC_TOKEN_DELIVERY_INVALID_RECIPIENT_ID.get(), 30221006);
        TOKEN_DELIVERY_INVALID_ACCOUNT_STATE = new ResultCode(LDAPMessages.INFO_RC_TOKEN_DELIVERY_INVALID_ACCOUNT_STATE.get(), 30221007);
        UNDEFINED_RESULT_CODES = new ConcurrentHashMap<Integer, ResultCode>(StaticUtils.computeMapCapacity(10));
    }
}
