package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum UniquenessValidationLevel
{
    NONE(0), 
    ALL_SUBTREE_VIEWS(1), 
    ALL_BACKEND_SETS(2), 
    ALL_AVAILABLE_BACKEND_SERVERS(3);
    
    private final int intValue;
    
    private UniquenessValidationLevel(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static UniquenessValidationLevel valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return UniquenessValidationLevel.NONE;
            }
            case 1: {
                return UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
            }
            case 2: {
                return UniquenessValidationLevel.ALL_BACKEND_SETS;
            }
            case 3: {
                return UniquenessValidationLevel.ALL_AVAILABLE_BACKEND_SERVERS;
            }
            default: {
                return null;
            }
        }
    }
    
    public static UniquenessValidationLevel forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "none": {
                return UniquenessValidationLevel.NONE;
            }
            case "allsubtreeviews":
            case "all-subtree-views":
            case "all_subtree_views": {
                return UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
            }
            case "allbackendsets":
            case "all-backend-sets":
            case "all_backend_sets": {
                return UniquenessValidationLevel.ALL_BACKEND_SETS;
            }
            case "allavailablebackendservers":
            case "all-available-backend-servers":
            case "all_available_backend_servers": {
                return UniquenessValidationLevel.ALL_AVAILABLE_BACKEND_SERVERS;
            }
            default: {
                return null;
            }
        }
    }
}
