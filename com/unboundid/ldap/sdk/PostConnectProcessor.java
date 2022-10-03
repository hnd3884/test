package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface PostConnectProcessor
{
    void processPreAuthenticatedConnection(final LDAPConnection p0) throws LDAPException;
    
    void processPostAuthenticatedConnection(final LDAPConnection p0) throws LDAPException;
}
