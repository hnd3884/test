package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;

public enum SetNotificationDestinationChangeType
{
    REPLACE(0), 
    ADD(1), 
    DELETE(2);
    
    private final int intValue;
    
    private SetNotificationDestinationChangeType(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static SetNotificationDestinationChangeType valueOf(final int intValue) {
        for (final SetNotificationDestinationChangeType t : values()) {
            if (t.intValue == intValue) {
                return t;
            }
        }
        return null;
    }
    
    public static SetNotificationDestinationChangeType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "replace": {
                return SetNotificationDestinationChangeType.REPLACE;
            }
            case "add": {
                return SetNotificationDestinationChangeType.ADD;
            }
            case "delete": {
                return SetNotificationDestinationChangeType.DELETE;
            }
            default: {
                return null;
            }
        }
    }
}
