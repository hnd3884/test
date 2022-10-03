package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface DisconnectHandler
{
    void handleDisconnect(final LDAPConnection p0, final String p1, final int p2, final DisconnectType p3, final String p4, final Throwable p5);
}
