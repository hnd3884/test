package com.unboundid.ldap.sdk.unboundidds.extensions;

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
public final class PasswordPolicyStateAccountUsabilityWarning implements Serializable
{
    public static final int WARNING_TYPE_ACCOUNT_EXPIRING = 1;
    public static final String WARNING_NAME_ACCOUNT_EXPIRING = "account-expiring";
    public static final int WARNING_TYPE_PASSWORD_EXPIRING = 2;
    public static final String WARNING_NAME_PASSWORD_EXPIRING = "password-expiring";
    public static final int WARNING_TYPE_OUTSTANDING_BIND_FAILURES = 3;
    public static final String WARNING_NAME_OUTSTANDING_BIND_FAILURES = "outstanding-bind-failures";
    public static final int WARNING_TYPE_ACCOUNT_IDLE = 4;
    public static final String WARNING_NAME_ACCOUNT_IDLE = "account-idle";
    public static final int WARNING_TYPE_REQUIRE_PASSWORD_CHANGE_BY_TIME = 5;
    public static final String WARNING_NAME_REQUIRE_PASSWORD_CHANGE_BY_TIME = "require-password-change-by-time";
    private static final long serialVersionUID = 4256291819633130578L;
    private final int intValue;
    private final String message;
    private final String name;
    private final String stringRepresentation;
    
    public PasswordPolicyStateAccountUsabilityWarning(final int intValue, final String name, final String message) {
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
    
    public PasswordPolicyStateAccountUsabilityWarning(final String stringRepresentation) throws LDAPException {
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
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_NO_CODE.get()));
            }
            if (n == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_NO_NAME.get()));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_CANNOT_DECODE.get(stringRepresentation, StaticUtils.getExceptionMessage(e)), e);
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
