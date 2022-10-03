package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;

public enum UniquenessValidationResult
{
    VALIDATION_PASSED("validation-passed"), 
    VALIDATION_FAILED("validation-failed"), 
    VALIDATION_NOT_ATTEMPTED("validation-not-attempted");
    
    private final String name;
    
    private UniquenessValidationResult(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static UniquenessValidationResult forName(final String name) {
        final String n = StaticUtils.toLowerCase(name).replace('_', '-');
        for (final UniquenessValidationResult r : values()) {
            if (r.getName().equals(n)) {
                return r;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
