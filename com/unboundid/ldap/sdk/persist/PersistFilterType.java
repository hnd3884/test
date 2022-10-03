package com.unboundid.ldap.sdk.persist;

import com.unboundid.util.StaticUtils;

public enum PersistFilterType
{
    PRESENCE, 
    EQUALITY, 
    STARTS_WITH, 
    ENDS_WITH, 
    CONTAINS, 
    GREATER_OR_EQUAL, 
    LESS_OR_EQUAL, 
    APPROXIMATELY_EQUAL_TO;
    
    public static PersistFilterType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "presence": {
                return PersistFilterType.PRESENCE;
            }
            case "equality": {
                return PersistFilterType.EQUALITY;
            }
            case "startswith":
            case "starts-with":
            case "starts_with": {
                return PersistFilterType.STARTS_WITH;
            }
            case "endswith":
            case "ends-with":
            case "ends_with": {
                return PersistFilterType.ENDS_WITH;
            }
            case "contains": {
                return PersistFilterType.CONTAINS;
            }
            case "greaterorequal":
            case "greater-or-equal":
            case "greater_or_equal": {
                return PersistFilterType.GREATER_OR_EQUAL;
            }
            case "lessorequal":
            case "less-or-equal":
            case "less_or_equal": {
                return PersistFilterType.LESS_OR_EQUAL;
            }
            case "approximatelyequalto":
            case "approximately-equal-to":
            case "approximately_equal_to": {
                return PersistFilterType.APPROXIMATELY_EQUAL_TO;
            }
            default: {
                return null;
            }
        }
    }
}
