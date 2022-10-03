package com.unboundid.ldap.sdk;

import com.unboundid.ldap.protocol.LDAPResponse;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
interface ResponseAcceptor
{
    void responseReceived(final LDAPResponse p0) throws LDAPException;
}
