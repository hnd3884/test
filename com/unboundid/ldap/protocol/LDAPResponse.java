package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface LDAPResponse
{
    public static final Control[] NO_CONTROLS = new Control[0];
    
    int getMessageID();
    
    void toString(final StringBuilder p0);
}
