package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.StaticUtils;

public enum ContentSyncState
{
    PRESENT(0), 
    ADD(1), 
    MODIFY(2), 
    DELETE(3);
    
    private final int intValue;
    
    private ContentSyncState(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ContentSyncState valueOf(final int intValue) {
        if (intValue == ContentSyncState.PRESENT.intValue()) {
            return ContentSyncState.PRESENT;
        }
        if (intValue == ContentSyncState.ADD.intValue()) {
            return ContentSyncState.ADD;
        }
        if (intValue == ContentSyncState.MODIFY.intValue()) {
            return ContentSyncState.MODIFY;
        }
        if (intValue == ContentSyncState.DELETE.intValue()) {
            return ContentSyncState.DELETE;
        }
        return null;
    }
    
    public static ContentSyncState forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "present": {
                return ContentSyncState.PRESENT;
            }
            case "add": {
                return ContentSyncState.ADD;
            }
            case "modify": {
                return ContentSyncState.MODIFY;
            }
            case "delete": {
                return ContentSyncState.DELETE;
            }
            default: {
                return null;
            }
        }
    }
}
