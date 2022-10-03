package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.StaticUtils;

public enum ContentSyncRequestMode
{
    REFRESH_ONLY(1), 
    REFRESH_AND_PERSIST(3);
    
    private final int intValue;
    
    private ContentSyncRequestMode(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ContentSyncRequestMode valueOf(final int intValue) {
        if (intValue == ContentSyncRequestMode.REFRESH_ONLY.intValue()) {
            return ContentSyncRequestMode.REFRESH_ONLY;
        }
        if (intValue == ContentSyncRequestMode.REFRESH_AND_PERSIST.intValue()) {
            return ContentSyncRequestMode.REFRESH_AND_PERSIST;
        }
        return null;
    }
    
    public static ContentSyncRequestMode forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "refreshonly":
            case "refresh-only":
            case "refresh_only": {
                return ContentSyncRequestMode.REFRESH_ONLY;
            }
            case "refreshandpersist":
            case "refresh-and-persist":
            case "refresh_and_persist": {
                return ContentSyncRequestMode.REFRESH_AND_PERSIST;
            }
            default: {
                return null;
            }
        }
    }
}
