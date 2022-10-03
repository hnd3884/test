package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;

public enum SubtreeAccessibilityState
{
    ACCESSIBLE(0, "accessible"), 
    READ_ONLY_BIND_ALLOWED(1, "read-only-bind-allowed"), 
    READ_ONLY_BIND_DENIED(2, "read-only-bind-denied"), 
    HIDDEN(3, "hidden");
    
    private final int intValue;
    private final String stateName;
    
    private SubtreeAccessibilityState(final int intValue, final String stateName) {
        this.intValue = intValue;
        this.stateName = stateName;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public String getStateName() {
        return this.stateName;
    }
    
    public boolean isAccessible() {
        return this == SubtreeAccessibilityState.ACCESSIBLE;
    }
    
    public boolean isHidden() {
        return this == SubtreeAccessibilityState.HIDDEN;
    }
    
    public boolean isReadOnly() {
        return this == SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED || this == SubtreeAccessibilityState.READ_ONLY_BIND_DENIED;
    }
    
    public static SubtreeAccessibilityState valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return SubtreeAccessibilityState.ACCESSIBLE;
            }
            case 1: {
                return SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED;
            }
            case 2: {
                return SubtreeAccessibilityState.READ_ONLY_BIND_DENIED;
            }
            case 3: {
                return SubtreeAccessibilityState.HIDDEN;
            }
            default: {
                return null;
            }
        }
    }
    
    public static SubtreeAccessibilityState forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "accessible": {
                return SubtreeAccessibilityState.ACCESSIBLE;
            }
            case "readonlybindallowed":
            case "read-only-bind-allowed":
            case "read_only_bind_allowed": {
                return SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED;
            }
            case "readonlybinddenied":
            case "read-only-bind-denied":
            case "read_only_bind_denied": {
                return SubtreeAccessibilityState.READ_ONLY_BIND_DENIED;
            }
            case "hidden": {
                return SubtreeAccessibilityState.HIDDEN;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.stateName;
    }
}
