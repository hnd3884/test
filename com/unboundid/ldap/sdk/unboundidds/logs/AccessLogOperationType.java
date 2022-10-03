package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AccessLogOperationType
{
    ABANDON("ABANDON"), 
    ADD("ADD"), 
    BIND("BIND"), 
    COMPARE("COMPARE"), 
    DELETE("DELETE"), 
    EXTENDED("EXTENDED"), 
    MODIFY("MODIFY"), 
    MODDN("MODDN"), 
    SEARCH("SEARCH"), 
    UNBIND("UNBIND");
    
    private final String logIdentifier;
    
    private AccessLogOperationType(final String logIdentifier) {
        this.logIdentifier = logIdentifier;
    }
    
    public String getLogIdentifier() {
        return this.logIdentifier;
    }
    
    public static AccessLogOperationType forName(final String logIdentifier) {
        for (final AccessLogOperationType t : values()) {
            if (t.logIdentifier.equals(logIdentifier)) {
                return t;
            }
        }
        return null;
    }
}
