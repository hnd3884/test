package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.StaticUtils;

public enum ContentSyncInfoType
{
    NEW_COOKIE((byte)(-128)), 
    REFRESH_DELETE((byte)(-95)), 
    REFRESH_PRESENT((byte)(-94)), 
    SYNC_ID_SET((byte)(-93));
    
    private final byte type;
    
    private ContentSyncInfoType(final byte type) {
        this.type = type;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public static ContentSyncInfoType valueOf(final byte type) {
        if (type == ContentSyncInfoType.NEW_COOKIE.getType()) {
            return ContentSyncInfoType.NEW_COOKIE;
        }
        if (type == ContentSyncInfoType.REFRESH_DELETE.getType()) {
            return ContentSyncInfoType.REFRESH_DELETE;
        }
        if (type == ContentSyncInfoType.REFRESH_PRESENT.getType()) {
            return ContentSyncInfoType.REFRESH_PRESENT;
        }
        if (type == ContentSyncInfoType.SYNC_ID_SET.getType()) {
            return ContentSyncInfoType.SYNC_ID_SET;
        }
        return null;
    }
    
    public static ContentSyncInfoType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "newcookie":
            case "new-cookie":
            case "new_cookie": {
                return ContentSyncInfoType.NEW_COOKIE;
            }
            case "refreshdelete":
            case "refresh-delete":
            case "refresh_delete": {
                return ContentSyncInfoType.REFRESH_DELETE;
            }
            case "refreshpresent":
            case "refresh-present":
            case "refresh_present": {
                return ContentSyncInfoType.REFRESH_PRESENT;
            }
            case "syncidset":
            case "sync-id-set":
            case "sync_id_set": {
                return ContentSyncInfoType.SYNC_ID_SET;
            }
            default: {
                return null;
            }
        }
    }
}
