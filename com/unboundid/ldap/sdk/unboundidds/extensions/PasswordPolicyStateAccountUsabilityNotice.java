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
public final class PasswordPolicyStateAccountUsabilityNotice implements Serializable
{
    public static final int NOTICE_TYPE_OUTSTANDING_RETIRED_PASSWORD = 1;
    public static final String NOTICE_NAME_OUTSTANDING_RETIRED_PASSWORD = "outstanding-retired-password";
    public static final int NOTICE_TYPE_OUTSTANDING_ONE_TIME_PASSWORD = 2;
    public static final String NOTICE_NAME_OUTSTANDING_ONE_TIME_PASSWORD = "outstanding-one-time-password";
    public static final int NOTICE_TYPE_OUTSTANDING_PASSWORD_RESET_TOKEN = 3;
    public static final String NOTICE_NAME_OUTSTANDING_PASSWORD_RESET_TOKEN = "outstanding-password-reset-token";
    public static final int NOTICE_TYPE_IN_MINIMUM_PASSWORD_AGE = 4;
    public static final String NOTICE_NAME_IN_MINIMUM_PASSWORD_AGE = "in-minimum-password-age";
    public static final int NOTICE_TYPE_NO_STATIC_PASSWORD = 5;
    public static final String NOTICE_NAME_NO_STATIC_PASSWORD = "no-static-password";
    private static final long serialVersionUID = 6147730018701385799L;
    private final int intValue;
    private final String message;
    private final String name;
    private final String stringRepresentation;
    
    public PasswordPolicyStateAccountUsabilityNotice(final int intValue, final String name, final String message) {
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
    
    public PasswordPolicyStateAccountUsabilityNotice(final String stringRepresentation) throws LDAPException {
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
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_NO_CODE.get()));
            }
            if (n == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_CANNOT_DECODE.get(stringRepresentation, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_NO_NAME.get()));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_CANNOT_DECODE.get(stringRepresentation, StaticUtils.getExceptionMessage(e)), e);
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
