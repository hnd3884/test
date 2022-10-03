package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AssuredReplicationLocalLevel
{
    NONE(0), 
    RECEIVED_ANY_SERVER(1), 
    PROCESSED_ALL_SERVERS(2);
    
    private final int intValue;
    
    private AssuredReplicationLocalLevel(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static AssuredReplicationLocalLevel valueOf(final int intValue) {
        for (final AssuredReplicationLocalLevel l : values()) {
            if (l.intValue == intValue) {
                return l;
            }
        }
        return null;
    }
    
    public static AssuredReplicationLocalLevel forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "none": {
                return AssuredReplicationLocalLevel.NONE;
            }
            case "receivedanyserver":
            case "received-any-server":
            case "received_any_server": {
                return AssuredReplicationLocalLevel.RECEIVED_ANY_SERVER;
            }
            case "processedallservers":
            case "processed-all-servers":
            case "processed_all_servers": {
                return AssuredReplicationLocalLevel.PROCESSED_ALL_SERVERS;
            }
            default: {
                return null;
            }
        }
    }
    
    public static AssuredReplicationLocalLevel getLessStrict(final AssuredReplicationLocalLevel l1, final AssuredReplicationLocalLevel l2) {
        if (l1.intValue <= l2.intValue) {
            return l1;
        }
        return l2;
    }
    
    public static AssuredReplicationLocalLevel getMoreStrict(final AssuredReplicationLocalLevel l1, final AssuredReplicationLocalLevel l2) {
        if (l1.intValue >= l2.intValue) {
            return l1;
        }
        return l2;
    }
}
