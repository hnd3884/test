package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.StringTokenizer;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuthenticationFailureReason implements Serializable
{
    public static final int FAILURE_TYPE_ACCOUNT_NOT_USABLE = 1;
    public static final String FAILURE_NAME_ACCOUNT_NOT_USABLE = "account-not-usable";
    public static final int FAILURE_TYPE_CANNOT_ASSIGN_CLIENT_CONNECTION_POLICY = 3;
    public static final String FAILURE_NAME_CANNOT_ASSIGN_CLIENT_CONNECTION_POLICY = "cannot-assign-client-connection-policy";
    public static final int FAILURE_TYPE_CANNOT_IDENTIFY_USER = 4;
    public static final String FAILURE_NAME_CANNOT_IDENTIFY_USER = "cannot-identify-user";
    public static final int FAILURE_TYPE_CONSTRAINT_VIOLATION = 5;
    public static final String FAILURE_NAME_CONSTRAINT_VIOLATION = "constraint-violation";
    public static final int FAILURE_TYPE_CONTROL_PROBLEM = 6;
    public static final String FAILURE_NAME_CONTROL_PROBLEM = "control-problem";
    public static final int FAILURE_TYPE_IMPROPER_SASL_CREDENTIALS = 7;
    public static final String FAILURE_NAME_IMPROPER_SASL_CREDENTIALS = "improper-sasl-credentials";
    public static final int FAILURE_TYPE_INSUFFICIENT_ACCESS_RIGHTS = 8;
    public static final String FAILURE_NAME_INSUFFICIENT_ACCESS_RIGHTS = "insufficient-access-rights";
    public static final int FAILURE_TYPE_INVALID_CREDENTIALS = 9;
    public static final String FAILURE_NAME_INVALID_CREDENTIALS = "invalid-credentials";
    public static final int FAILURE_TYPE_LOCKDOWN_MODE = 10;
    public static final String FAILURE_NAME_LOCKDOWN_MODE = "lockdown-mode";
    public static final int FAILURE_TYPE_SECURE_AUTHENTICATION_REQUIRED = 11;
    public static final String FAILURE_NAME_SECURE_AUTHENTICATION_REQUIRED = "secure-authentication-required";
    public static final int FAILURE_TYPE_SERVER_ERROR = 12;
    public static final String FAILURE_NAME_SERVER_ERROR = "server-error";
    public static final int FAILURE_TYPE_THIRD_PARTY_SASL_AUTHENTICATION_FAILURE = 13;
    public static final String FAILURE_NAME_THIRD_PARTY_SASL_AUTHENTICATION_FAILURE = "third-party-sasl-authentication-failure";
    public static final int FAILURE_TYPE_UNAVAILABLE_AUTHENTICATION_TYPE = 14;
    public static final String FAILURE_NAME_UNAVAILABLE_AUTHENTICATION_TYPE = "unavailable-authentication-type";
    public static final int FAILURE_TYPE_OTHER = 15;
    public static final String FAILURE_NAME_OTHER = "other";
    private static final long serialVersionUID = -5752716527356924347L;
    private final int intValue;
    private final String message;
    private final String name;
    private final String stringRepresentation;
    
    public AuthenticationFailureReason(final int intValue, final String name, final String message) {
        Validator.ensureNotNull(name);
        this.intValue = intValue;
        this.name = name;
        this.message = message;
        final StringBuilder buffer = new StringBuilder();
        buffer.append("code=");
        buffer.append(intValue);
        buffer.append("\tname=");
        buffer.append(name);
        if (message != null) {
            buffer.append("\tmessage=");
            buffer.append(message);
        }
        this.stringRepresentation = buffer.toString();
    }
    
    public AuthenticationFailureReason(final String stringRepresentation) throws LDAPException {
        this.stringRepresentation = stringRepresentation;
        try {
            Integer i = null;
            String n = null;
            String m = null;
            final StringTokenizer tokenizer = new StringTokenizer(stringRepresentation, "\t");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                final int equalPos = token.indexOf(61);
                final String fieldName = token.substring(0, equalPos);
                final String fieldValue = token.substring(equalPos + 1);
                if (fieldName.equals("code")) {
                    i = Integer.valueOf(fieldValue);
                }
                else if (fieldName.equals("name")) {
                    n = fieldValue;
                }
                else {
                    if (!fieldName.equals("message")) {
                        continue;
                    }
                    m = fieldValue;
                }
            }
            if (i == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_AUTH_FAILURE_REASON_CANNOT_DECODE.get(stringRepresentation, ControlMessages.ERR_AUTH_FAILURE_REASON_NO_CODE.get()));
            }
            if (n == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_AUTH_FAILURE_REASON_CANNOT_DECODE.get(stringRepresentation, ControlMessages.ERR_AUTH_FAILURE_REASON_NO_NAME.get()));
            }
            this.intValue = i;
            this.name = n;
            this.message = m;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_AUTH_FAILURE_REASON_CANNOT_DECODE.get(stringRepresentation, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
}
