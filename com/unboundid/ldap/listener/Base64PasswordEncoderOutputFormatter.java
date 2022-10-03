package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Base64;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Base64PasswordEncoderOutputFormatter extends PasswordEncoderOutputFormatter
{
    private static final Base64PasswordEncoderOutputFormatter INSTANCE;
    
    private Base64PasswordEncoderOutputFormatter() {
    }
    
    public static Base64PasswordEncoderOutputFormatter getInstance() {
        return Base64PasswordEncoderOutputFormatter.INSTANCE;
    }
    
    @Override
    public byte[] format(final byte[] unformattedData) throws LDAPException {
        return StaticUtils.getBytes(Base64.encode(unformattedData));
    }
    
    @Override
    public byte[] unFormat(final byte[] formattedData) throws LDAPException {
        try {
            return Base64.decode(StaticUtils.toUTF8String(formattedData));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_BASE64_PW_FORMATTER_CANNOT_DECODE.get(), e);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("Base64PasswordEncoderOutputFormatter()");
    }
    
    static {
        INSTANCE = new Base64PasswordEncoderOutputFormatter();
    }
}
