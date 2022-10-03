package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface ReferralConnector
{
    LDAPConnection getReferralConnection(final LDAPURL p0, final LDAPConnection p1) throws LDAPException;
}
