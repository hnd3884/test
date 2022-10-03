package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum HealthCheckState
{
    AVAILABLE("available"), 
    DEGRADED("degraded"), 
    UNAVAILABLE("unavailable"), 
    NO_LOCAL_SERVERS("no-local-servers"), 
    NO_REMOTE_SERVERS("no-remote-servers");
    
    private final String name;
    
    private HealthCheckState(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static HealthCheckState forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "available": {
                return HealthCheckState.AVAILABLE;
            }
            case "degraded": {
                return HealthCheckState.DEGRADED;
            }
            case "unavailable": {
                return HealthCheckState.UNAVAILABLE;
            }
            case "nolocalservers":
            case "no-local-servers":
            case "no_local_servers": {
                return HealthCheckState.NO_LOCAL_SERVERS;
            }
            case "noremoteservers":
            case "no-remote-servers":
            case "no_remote_servers": {
                return HealthCheckState.NO_REMOTE_SERVERS;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
