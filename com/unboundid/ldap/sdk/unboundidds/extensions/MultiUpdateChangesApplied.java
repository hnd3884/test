package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;

public enum MultiUpdateChangesApplied
{
    NONE(0), 
    ALL(1), 
    PARTIAL(2);
    
    private final int intValue;
    
    private MultiUpdateChangesApplied(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static MultiUpdateChangesApplied valueOf(final int intValue) {
        for (final MultiUpdateChangesApplied v : values()) {
            if (intValue == v.intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static MultiUpdateChangesApplied forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "none": {
                return MultiUpdateChangesApplied.NONE;
            }
            case "all": {
                return MultiUpdateChangesApplied.ALL;
            }
            case "partial": {
                return MultiUpdateChangesApplied.PARTIAL;
            }
            default: {
                return null;
            }
        }
    }
}
