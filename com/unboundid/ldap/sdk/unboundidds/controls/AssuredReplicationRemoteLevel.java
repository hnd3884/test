package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AssuredReplicationRemoteLevel
{
    NONE(0), 
    RECEIVED_ANY_REMOTE_LOCATION(1), 
    RECEIVED_ALL_REMOTE_LOCATIONS(2), 
    PROCESSED_ALL_REMOTE_SERVERS(3);
    
    private final int intValue;
    
    private AssuredReplicationRemoteLevel(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static AssuredReplicationRemoteLevel valueOf(final int intValue) {
        for (final AssuredReplicationRemoteLevel l : values()) {
            if (l.intValue == intValue) {
                return l;
            }
        }
        return null;
    }
    
    public static AssuredReplicationRemoteLevel forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "none": {
                return AssuredReplicationRemoteLevel.NONE;
            }
            case "receivedanyremotelocation":
            case "received-any-remote-location":
            case "received_any_remote_location": {
                return AssuredReplicationRemoteLevel.RECEIVED_ANY_REMOTE_LOCATION;
            }
            case "receivedallremotelocations":
            case "received-all-remote-locations":
            case "received_all_remote_locations": {
                return AssuredReplicationRemoteLevel.RECEIVED_ALL_REMOTE_LOCATIONS;
            }
            case "processedallremoteservers":
            case "processed-all-remote-servers":
            case "processed_all_remote_servers": {
                return AssuredReplicationRemoteLevel.PROCESSED_ALL_REMOTE_SERVERS;
            }
            default: {
                return null;
            }
        }
    }
    
    public static AssuredReplicationRemoteLevel getLessStrict(final AssuredReplicationRemoteLevel l1, final AssuredReplicationRemoteLevel l2) {
        if (l1.intValue <= l2.intValue) {
            return l1;
        }
        return l2;
    }
    
    public static AssuredReplicationRemoteLevel getMoreStrict(final AssuredReplicationRemoteLevel l1, final AssuredReplicationRemoteLevel l2) {
        if (l1.intValue >= l2.intValue) {
            return l1;
        }
        return l2;
    }
}
