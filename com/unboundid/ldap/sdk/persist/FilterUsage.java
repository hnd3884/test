package com.unboundid.ldap.sdk.persist;

import com.unboundid.util.StaticUtils;

public enum FilterUsage
{
    REQUIRED, 
    ALWAYS_ALLOWED, 
    CONDITIONALLY_ALLOWED, 
    EXCLUDED;
    
    public static FilterUsage forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "required": {
                return FilterUsage.REQUIRED;
            }
            case "alwaysallowed":
            case "always-allowed":
            case "always_allowed": {
                return FilterUsage.ALWAYS_ALLOWED;
            }
            case "conditionallyallowed":
            case "conditionally-allowed":
            case "conditionally_allowed": {
                return FilterUsage.CONDITIONALLY_ALLOWED;
            }
            case "excluded": {
                return FilterUsage.EXCLUDED;
            }
            default: {
                return null;
            }
        }
    }
}
