package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum RouteToBackendSetRoutingType
{
    ABSOLUTE_ROUTING((byte)(-96)), 
    ROUTING_HINT((byte)(-95));
    
    private final byte berType;
    
    private RouteToBackendSetRoutingType(final byte berType) {
        this.berType = berType;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public static RouteToBackendSetRoutingType valueOf(final byte berType) {
        for (final RouteToBackendSetRoutingType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
    
    public static RouteToBackendSetRoutingType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "absoluterouting":
            case "absolute-routing":
            case "absolute_routing": {
                return RouteToBackendSetRoutingType.ABSOLUTE_ROUTING;
            }
            case "routinghint":
            case "routing-hint":
            case "routing_hint": {
                return RouteToBackendSetRoutingType.ROUTING_HINT;
            }
            default: {
                return null;
            }
        }
    }
}
