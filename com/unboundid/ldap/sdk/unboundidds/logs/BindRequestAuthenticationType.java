package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum BindRequestAuthenticationType
{
    INTERNAL, 
    SASL, 
    SIMPLE;
    
    public static BindRequestAuthenticationType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "internal": {
                return BindRequestAuthenticationType.INTERNAL;
            }
            case "sasl": {
                return BindRequestAuthenticationType.SASL;
            }
            case "simple": {
                return BindRequestAuthenticationType.SIMPLE;
            }
            default: {
                return null;
            }
        }
    }
}
