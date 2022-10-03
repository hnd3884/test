package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class PasswordEncoderOutputFormatter
{
    public abstract byte[] format(final byte[] p0) throws LDAPException;
    
    public abstract byte[] unFormat(final byte[] p0) throws LDAPException;
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
}
