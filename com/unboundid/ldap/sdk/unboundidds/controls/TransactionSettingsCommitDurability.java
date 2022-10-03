package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum TransactionSettingsCommitDurability
{
    NON_SYNCHRONOUS(0), 
    PARTIALLY_SYNCHRONOUS(1), 
    FULLY_SYNCHRONOUS(2);
    
    private final int intValue;
    
    private TransactionSettingsCommitDurability(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static TransactionSettingsCommitDurability valueOf(final int intValue) {
        for (final TransactionSettingsCommitDurability v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static TransactionSettingsCommitDurability forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "nonsynchronous":
            case "non-synchronous":
            case "non_synchronous": {
                return TransactionSettingsCommitDurability.NON_SYNCHRONOUS;
            }
            case "partiallysynchronous":
            case "partially-synchronous":
            case "partially_synchronous": {
                return TransactionSettingsCommitDurability.PARTIALLY_SYNCHRONOUS;
            }
            case "fullysynchronous":
            case "fully-synchronous":
            case "fully_synchronous": {
                return TransactionSettingsCommitDurability.FULLY_SYNCHRONOUS;
            }
            default: {
                return null;
            }
        }
    }
}
