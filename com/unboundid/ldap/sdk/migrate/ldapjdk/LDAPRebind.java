package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDAPRebind
{
    LDAPRebindAuth getRebindAuthentication(final String p0, final int p1);
}
