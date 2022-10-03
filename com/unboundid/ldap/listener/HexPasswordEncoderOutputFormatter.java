package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class HexPasswordEncoderOutputFormatter extends PasswordEncoderOutputFormatter
{
    private static final HexPasswordEncoderOutputFormatter LOWERCASE_INSTANCE;
    private static final HexPasswordEncoderOutputFormatter UPPERCASE_INSTANCE;
    private final boolean useLowercaseLetters;
    
    private HexPasswordEncoderOutputFormatter(final boolean useLowercaseLetters) {
        this.useLowercaseLetters = useLowercaseLetters;
    }
    
    public static HexPasswordEncoderOutputFormatter getLowercaseInstance() {
        return HexPasswordEncoderOutputFormatter.LOWERCASE_INSTANCE;
    }
    
    public static HexPasswordEncoderOutputFormatter getUppercaseInstance() {
        return HexPasswordEncoderOutputFormatter.UPPERCASE_INSTANCE;
    }
    
    public boolean useLowercaseLetters() {
        return this.useLowercaseLetters;
    }
    
    @Override
    public byte[] format(final byte[] unformattedData) throws LDAPException {
        String hexString = StaticUtils.toHex(unformattedData);
        if (!this.useLowercaseLetters) {
            hexString = hexString.toUpperCase();
        }
        return StaticUtils.getBytes(hexString);
    }
    
    @Override
    public byte[] unFormat(final byte[] formattedData) throws LDAPException {
        try {
            return StaticUtils.fromHex(StaticUtils.toUTF8String(formattedData));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_HEX_PW_FORMATTER_CANNOT_DECODE.get(), e);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("HexPasswordEncoderOutputFormatter(useLowercaseLetters=");
        buffer.append(this.useLowercaseLetters);
        buffer.append(')');
    }
    
    static {
        LOWERCASE_INSTANCE = new HexPasswordEncoderOutputFormatter(true);
        UPPERCASE_INSTANCE = new HexPasswordEncoderOutputFormatter(false);
    }
}
