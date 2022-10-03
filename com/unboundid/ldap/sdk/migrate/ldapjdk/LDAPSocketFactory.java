package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.net.Socket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDAPSocketFactory
{
    Socket makeSocket(final String p0, final int p1) throws LDAPException;
}
