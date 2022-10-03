package org.apache.tomcat.dbcp.pool2.impl;

import java.security.AccessControlException;
import java.security.Permission;

public final class CallStackUtils
{
    private static boolean canCreateSecurityManager() {
        final SecurityManager manager = System.getSecurityManager();
        if (manager == null) {
            return true;
        }
        try {
            manager.checkPermission(new RuntimePermission("createSecurityManager"));
            return true;
        }
        catch (final AccessControlException ignored) {
            return false;
        }
    }
    
    @Deprecated
    public static CallStack newCallStack(final String messageFormat, final boolean useTimestamp) {
        return newCallStack(messageFormat, useTimestamp, false);
    }
    
    public static CallStack newCallStack(final String messageFormat, final boolean useTimestamp, final boolean requireFullStackTrace) {
        return (canCreateSecurityManager() && !requireFullStackTrace) ? new SecurityManagerCallStack(messageFormat, useTimestamp) : new ThrowableCallStack(messageFormat, useTimestamp);
    }
    
    private CallStackUtils() {
    }
}
