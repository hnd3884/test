package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum SuppressType
{
    LAST_ACCESS_TIME(0), 
    LAST_LOGIN_TIME(1), 
    LAST_LOGIN_IP(2), 
    LASTMOD(3);
    
    private final int intValue;
    
    private SuppressType(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static SuppressType valueOf(final int intValue) {
        for (final SuppressType t : values()) {
            if (t.intValue == intValue) {
                return t;
            }
        }
        return null;
    }
    
    public static SuppressType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "lastaccesstime":
            case "last-access-time":
            case "last_access_time": {
                return SuppressType.LAST_ACCESS_TIME;
            }
            case "lastlogintime":
            case "last-login-time":
            case "last_login_time": {
                return SuppressType.LAST_LOGIN_TIME;
            }
            case "lastloginip":
            case "last-login-ip":
            case "last_login_ip": {
                return SuppressType.LAST_LOGIN_IP;
            }
            case "lastmod": {
                return SuppressType.LASTMOD;
            }
            default: {
                return null;
            }
        }
    }
}
