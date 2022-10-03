package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;

public enum MultiUpdateErrorBehavior
{
    ATOMIC(0), 
    ABORT_ON_ERROR(1), 
    CONTINUE_ON_ERROR(2);
    
    private final int intValue;
    
    private MultiUpdateErrorBehavior(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static MultiUpdateErrorBehavior valueOf(final int intValue) {
        for (final MultiUpdateErrorBehavior v : values()) {
            if (intValue == v.intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static MultiUpdateErrorBehavior forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "atomic": {
                return MultiUpdateErrorBehavior.ATOMIC;
            }
            case "abortonerror":
            case "abort-on-error":
            case "abort_on_error": {
                return MultiUpdateErrorBehavior.ABORT_ON_ERROR;
            }
            case "continueonerror":
            case "continue-on-error":
            case "continue_on_error": {
                return MultiUpdateErrorBehavior.CONTINUE_ON_ERROR;
            }
            default: {
                return null;
            }
        }
    }
}
