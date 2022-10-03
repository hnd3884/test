package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AssuredReplicationServerResultCode
{
    COMPLETE(0), 
    TIMEOUT(1), 
    CONFLICT(2), 
    SERVER_SHUTDOWN(3), 
    UNAVAILABLE(4), 
    DUPLICATE(5);
    
    private final int intValue;
    
    private AssuredReplicationServerResultCode(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static AssuredReplicationServerResultCode valueOf(final int intValue) {
        for (final AssuredReplicationServerResultCode rc : values()) {
            if (rc.intValue == intValue) {
                return rc;
            }
        }
        return null;
    }
    
    public static AssuredReplicationServerResultCode forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "complete": {
                return AssuredReplicationServerResultCode.COMPLETE;
            }
            case "timeout": {
                return AssuredReplicationServerResultCode.TIMEOUT;
            }
            case "conflict": {
                return AssuredReplicationServerResultCode.CONFLICT;
            }
            case "servershutdown":
            case "server-shutdown":
            case "server_shutdown": {
                return AssuredReplicationServerResultCode.SERVER_SHUTDOWN;
            }
            case "unavailable": {
                return AssuredReplicationServerResultCode.UNAVAILABLE;
            }
            case "duplicate": {
                return AssuredReplicationServerResultCode.DUPLICATE;
            }
            default: {
                return null;
            }
        }
    }
}
