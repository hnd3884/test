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
public final class PasswordPolicyStateAccountUsabilityError implements Serializable
{
    public static final int ERROR_TYPE_ACCOUNT_DISABLED = 1;
    public static final String ERROR_NAME_ACCOUNT_DISABLED = "account-disabled";
    public static final int ERROR_TYPE_ACCOUNT_NOT_YET_ACTIVE = 2;
    public static final String ERROR_NAME_ACCOUNT_NOT_YET_ACTIVE = "account-not-yet-active";
    public static final int ERROR_TYPE_ACCOUNT_EXPIRED = 3;
    public static final String ERROR_NAME_ACCOUNT_EXPIRED = "account-expired";
    public static final int ERROR_TYPE_ACCOUNT_PERMANENTLY_LOCKED_DUE_TO_BIND_FAILURES = 4;
    public static final String ERROR_NAME_ACCOUNT_PERMANENTLY_LOCKED_DUE_TO_BIND_FAILURES = "account-permanently-locked-due-to-bind-failures";
    public static final int ERROR_TYPE_ACCOUNT_TEMPORARILY_LOCKED_DUE_TO_BIND_FAILURES = 5;
    public static final String ERROR_NAME_ACCOUNT_TEMPORARILY_LOCKED_DUE_TO_BIND_FAILURES = "account-temporarily-locked-due-to-bind-failures";
    public static final int ERROR_TYPE_ACCOUNT_IDLE_LOCKED = 6;
    public static final String ERROR_NAME_ACCOUNT_IDLE_LOCKED = "account-idle-locked";
    public static final int ERROR_TYPE_ACCOUNT_RESET_LOCKED = 7;
    public static final String ERROR_NAME_ACCOUNT_RESET_LOCKED = "account-reset-locked";
    public static final int ERROR_TYPE_PASSWORD_EXPIRED = 8;
    public static final String ERROR_NAME_PASSWORD_EXPIRED = "password-expired";
    public static final int ERROR_TYPE_PASSWORD_NOT_CHANGED_BY_REQUIRED_TIME = 9;
    public static final String ERROR_NAME_PASSWORD_NOT_CHANGED_BY_REQUIRED_TIME = "password-not-changed-by-required-time";
    public static final int ERROR_TYPE_PASSWORD_EXPIRED_WITH_GRACE_LOGINS = 10;
    public static final String ERROR_NAME_PASSWORD_EXPIRED_WITH_GRACE_LOGINS = "password-expired-with-grace-logins";
    public static final int ERROR_TYPE_MUST_CHANGE_PASSWORD = 11;
    public static final String ERROR_NAME_MUST_CHANGE_PASSWORD = "must-change-password";
    private static final long serialVersionUID = -2482863468368980580L;
    private final int intValue;
    private final String message;
    private final String name;
    private final String stringRepresentation;
    
    public PasswordPolicyStateAccountUsabilityError(final int intValue, final String name, final String message) {
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
    
    public PasswordPolicyStateAccountUsabilityError(final String stringRepresentation) throws LDAPException {
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
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_NO_CODE.get()));
            }
            if (n == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_NO_NAME.get()));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_CANNOT_DECODE.get(stringRepresentation, StaticUtils.getExceptionMessage(e)), e);
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
