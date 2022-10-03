package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum TransactionSettingsBackendLockBehavior
{
    DO_NOT_ACQUIRE(0), 
    ACQUIRE_AFTER_RETRIES(1), 
    ACQUIRE_BEFORE_RETRIES(2), 
    ACQUIRE_BEFORE_INITIAL_ATTEMPT(3);
    
    private final int intValue;
    
    private TransactionSettingsBackendLockBehavior(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static TransactionSettingsBackendLockBehavior valueOf(final int intValue) {
        for (final TransactionSettingsBackendLockBehavior v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static TransactionSettingsBackendLockBehavior forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "donotacquire":
            case "do-not-acquire":
            case "do_not_acquire": {
                return TransactionSettingsBackendLockBehavior.DO_NOT_ACQUIRE;
            }
            case "acquireafterretries":
            case "acquire-after-retries":
            case "acquire_after_retries": {
                return TransactionSettingsBackendLockBehavior.ACQUIRE_AFTER_RETRIES;
            }
            case "acquirebeforeretries":
            case "acquire-before-retries":
            case "acquire_before_retries": {
                return TransactionSettingsBackendLockBehavior.ACQUIRE_BEFORE_RETRIES;
            }
            case "acquirebeforeinitialattempt":
            case "acquire-before-initial-attempt":
            case "acquire_before_initial_attempt": {
                return TransactionSettingsBackendLockBehavior.ACQUIRE_BEFORE_INITIAL_ATTEMPT;
            }
            default: {
                return null;
            }
        }
    }
}
