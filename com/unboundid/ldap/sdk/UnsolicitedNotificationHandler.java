package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface UnsolicitedNotificationHandler
{
    void handleUnsolicitedNotification(final LDAPConnection p0, final ExtendedResult p1);
}
