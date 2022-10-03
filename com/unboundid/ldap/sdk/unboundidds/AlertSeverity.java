package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AlertSeverity
{
    INFO("info"), 
    WARNING("warning"), 
    ERROR("error"), 
    FATAL("fatal");
    
    private final String name;
    
    private AlertSeverity(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static AlertSeverity forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "info": {
                return AlertSeverity.INFO;
            }
            case "warning": {
                return AlertSeverity.WARNING;
            }
            case "error": {
                return AlertSeverity.ERROR;
            }
            case "fatal": {
                return AlertSeverity.FATAL;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
