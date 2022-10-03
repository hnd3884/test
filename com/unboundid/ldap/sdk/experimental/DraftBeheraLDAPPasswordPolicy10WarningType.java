package com.unboundid.ldap.sdk.experimental;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum DraftBeheraLDAPPasswordPolicy10WarningType
{
    TIME_BEFORE_EXPIRATION("time before expiration"), 
    GRACE_LOGINS_REMAINING("grace logins remaining");
    
    private final String name;
    
    private DraftBeheraLDAPPasswordPolicy10WarningType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static DraftBeheraLDAPPasswordPolicy10WarningType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "timebeforeexpiration":
            case "time-before-expiration":
            case "time_before_expiration":
            case "time before expiration": {
                return DraftBeheraLDAPPasswordPolicy10WarningType.TIME_BEFORE_EXPIRATION;
            }
            case "graceloginsremaining":
            case "grace-logins-remaining":
            case "grace_logins_remaining":
            case "grace logins remaining": {
                return DraftBeheraLDAPPasswordPolicy10WarningType.GRACE_LOGINS_REMAINING;
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
